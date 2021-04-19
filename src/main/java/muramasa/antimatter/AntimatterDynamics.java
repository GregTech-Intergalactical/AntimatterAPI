package muramasa.antimatter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Sets;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.datagen.providers.AntimatterRecipeProvider;
import muramasa.antimatter.datagen.resources.DynamicResourcePack;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.recipe.loader.IRecipeRegistrate;
import muramasa.antimatter.recipe.map.RecipeMap;
import muramasa.antimatter.registration.ModRegistrar;
import muramasa.antimatter.util.TagUtils;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;

public class AntimatterDynamics {
    private static final Object2ObjectOpenHashMap<String, List<Function<DataGenerator, IAntimatterProvider>>> PROVIDERS = new Object2ObjectOpenHashMap<>();

    public static void onProviderInit(String domain, DataGenerator gen, Dist side) {
        PROVIDERS.getOrDefault(domain, Collections.emptyList()).stream().map(f -> f.apply(gen)).filter(p -> p.getSide().equals(side) && p.shouldRun()).forEach(gen::addProvider);
    }

    /** Providers and Dynamic Resource Pack Section **/
    public static void addProvider(String domain, Function<DataGenerator, IAntimatterProvider> providerFunc) {
        PROVIDERS.computeIfAbsent(domain, k -> new ObjectArrayList<>()).add(providerFunc);
    }

    // Can't run this in parallel since ItemTagsProviders need BlockTagsProviders to run first
    public static void runDataProvidersDynamically() {
        DynamicResourcePack.clearServer();
        List<IAntimatterProvider> providers = PROVIDERS.object2ObjectEntrySet().stream().flatMap(v -> v.getValue().stream().map(f -> f.apply(Ref.BACKGROUND_GEN)).filter(p -> p.getSide().equals(Dist.DEDICATED_SERVER) && p.shouldRun())).collect(Collectors.toList());
        long time = System.currentTimeMillis();
        Stream<IAntimatterProvider> async = providers.stream().filter(t -> t.async()).parallel();
        Stream<IAntimatterProvider> sync = providers.stream().filter(t -> !t.async());        
        Stream.concat(async, sync).forEach(t -> t.run());
        providers.forEach(IAntimatterProvider::onCompletion);
        Antimatter.LOGGER.info("Time to run data providers: " + (System.currentTimeMillis() - time) + " ms.");
    }

    public static void runAssetProvidersDynamically() {
        DynamicResourcePack.clearClient();
        List<IAntimatterProvider> providers = PROVIDERS.object2ObjectEntrySet().stream().flatMap(v -> v.getValue().stream().map(f -> f.apply(Ref.BACKGROUND_GEN)).filter(p -> p.getSide().equals(Dist.CLIENT) && p.shouldRun())).collect(Collectors.toList());
        long time = System.currentTimeMillis();
        Stream<IAntimatterProvider> async = providers.stream().filter(t -> t.async()).parallel();
        Stream<IAntimatterProvider> sync = providers.stream().filter(t -> !t.async());        
        Stream.concat(async, sync).forEach(t -> t.run());
        providers.forEach(IAntimatterProvider::onCompletion);
        Antimatter.LOGGER.info("Time to run asset providers: " + (System.currentTimeMillis() - time) + " ms.");
    }

    public static void onDataReady() {
        AntimatterAPI.all(ModRegistrar.class, t -> {
            for (String mod : t.modIds()) {
                if (!AntimatterAPI.isModLoaded(mod)) return;
            }
            t.antimatterRecipes(AntimatterAPI.getRecipeRegistrate());
        });
    }

    /**
     * Collects all antimatter registered recipes, pushing them to @rec.
     * @param rec consumer for IFinishedRecipe.
     */
    public static void collectRecipes(Consumer<IFinishedRecipe> rec) {
        Set<ResourceLocation> set = Sets.newHashSet();
        List<AntimatterRecipeProvider> providers = PROVIDERS.object2ObjectEntrySet().stream().flatMap(v -> v.getValue().stream().map(f -> f.apply(Ref.BACKGROUND_GEN)).filter(p -> p instanceof AntimatterRecipeProvider).map(t -> (AntimatterRecipeProvider)t)).collect(Collectors.toList());
        providers.forEach(prov -> prov.registerRecipes(recipe -> {
            if (set.add(recipe.getID())) {
                rec.accept(recipe);
            }
        }));
    }

    public static void onRecipeManagerBuild(Consumer<IFinishedRecipe> objectIn) {
        Antimatter.LOGGER.info("Recipe manager head executing.");
        collectRecipes(objectIn::accept);
        AntimatterAPI.all(ModRegistrar.class, t -> {
            for (String mod : t.modIds()) {
                if (!AntimatterAPI.isModLoaded(mod)) return;
            }
            t.craftingRecipes(new AntimatterRecipeProvider("Antimatter", "Custom recipes",Ref.BACKGROUND_GEN));
        });
    }

    public static void onRecipeCompile(RecipeManager manager, Function<Item, Collection<ResourceLocation>> tagGetter) {
        Antimatter.LOGGER.info("Compiling GT recipes");
        long time = System.nanoTime();
        AntimatterAPI.all(RecipeMap.class, rm -> rm.compile(manager, tagGetter));
        List<Recipe> recipes = manager.getRecipesForType(Recipe.RECIPE_TYPE);
        recipes.forEach(t -> {
            RecipeMap<?> map = AntimatterAPI.get(RecipeMap.class, "gt.recipe_map." + t.mapId);
            if (map != null) map.compileRecipe(t, tagGetter);
        });
        time = System.nanoTime()-time;
        Antimatter.LOGGER.info("Time to compile GT recipes: (ms) " + (time)/(1000*1000));
        int size = AntimatterAPI.all(RecipeMap.class).stream().mapToInt(t -> t.getRecipes(false).size()).sum();
        Antimatter.LOGGER.info("No. of GT recipes: " + size);
        Antimatter.LOGGER.info("Average loading time / recipe: (µs) " + (size > 0 ? time/size : time)/1000);
    }

    public static void onResourceReload(boolean server) {
        if (server) runDataProvidersDynamically();
        AntimatterAPI.all(RecipeMap.class, RecipeMap::reset);
        AntimatterAPI.all(IRecipeRegistrate.IRecipeLoader.class, IRecipeRegistrate.IRecipeLoader::init);
        if (server) {
            TagUtils.getTags(Item.class).forEach((k,v) -> {
                DynamicResourcePack.ensureTagAvailable("items", k); //builder.serialize(), false);
            });
            TagUtils.getTags(Fluid.class).forEach((k, v) -> {
                DynamicResourcePack.ensureTagAvailable("fluids", k); //builder.serialize(), false);
            });
            TagUtils.getTags(Block.class).forEach((k, v) -> {
                DynamicResourcePack.ensureTagAvailable("blocks", k); //builder.serialize(), false);
            });
        }
    }
    /*
    public static void runBackgroundProviders() {
        Antimatter.LOGGER.info("Running DummyTagProviders...");
        Ref.BACKGROUND_GEN.addProviders(DummyTagProviders.DUMMY_PROVIDERS);
        try {
            Ref.BACKGROUND_GEN.run();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
