package muramasa.antimatter;

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
import net.minecraft.tags.ITagCollectionSupplier;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        Stream.concat(async, sync).forEach(IAntimatterProvider::run);
        providers.forEach(IAntimatterProvider::onCompletion);
        Antimatter.LOGGER.info("Time to run data providers: " + (System.currentTimeMillis() - time) + " ms.");
    }

    public static void runAssetProvidersDynamically() {
        DynamicResourcePack.clearClient();
        List<IAntimatterProvider> providers = PROVIDERS.object2ObjectEntrySet().stream().flatMap(v -> v.getValue().stream().map(f -> f.apply(Ref.BACKGROUND_GEN)).filter(p -> p.getSide().equals(Dist.CLIENT) && p.shouldRun())).collect(Collectors.toList());
        long time = System.currentTimeMillis();
        Stream<IAntimatterProvider> async = providers.stream().filter(t -> t.async()).parallel();
        Stream<IAntimatterProvider> sync = providers.stream().filter(t -> !t.async());        
        Stream.concat(async, sync).forEach(IAntimatterProvider::run);
        providers.forEach(IAntimatterProvider::onCompletion);
        Antimatter.LOGGER.info("Time to run asset providers: " + (System.currentTimeMillis() - time) + " ms.");
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
        Antimatter.LOGGER.info("Antimatter recipe manager running..");
        collectRecipes(objectIn::accept);
        AntimatterAPI.all(ModRegistrar.class, t -> {
            for (String mod : t.modIds()) {
                if (!AntimatterAPI.isModLoaded(mod)) return;
            }
            t.craftingRecipes(new AntimatterRecipeProvider("Antimatter", "Custom recipes",Ref.BACKGROUND_GEN));
        });        
        Antimatter.LOGGER.info("Antimatter recipe manager done..");
    }

    public static void onRecipeCompile(RecipeManager manager, ITagCollectionSupplier tags) {
        TagUtils.setSupplier(tags);
        Antimatter.LOGGER.info("Compiling GT recipes");
        long time = System.nanoTime();
        AntimatterAPI.all(RecipeMap.class, rm -> rm.compile(manager, tags));
        
        List<Recipe> recipes = manager.getRecipesForType(Recipe.RECIPE_TYPE);
        Map<String, List<Recipe>> map = recipes.stream().collect(Collectors.groupingBy(recipe -> recipe.mapId));
        for (Map.Entry<String, List<Recipe>> entry : map.entrySet()) {
            RecipeMap<?> rmap = AntimatterAPI.get(RecipeMap.class, "gt.recipe_map." + entry.getKey());
            if (rmap != null) entry.getValue().forEach(rec -> rmap.compileRecipe(rec, tags));
        }
        time = System.nanoTime()-time;
        int size = AntimatterAPI.all(RecipeMap.class).stream().mapToInt(t -> t.getRecipes(false).size()).sum();

        Antimatter.LOGGER.info("Time to compile GT recipes: (ms) " + (time)/(1000*1000));
        Antimatter.LOGGER.info("No. of GT recipes: " + size);
        Antimatter.LOGGER.info("Average loading time / recipe: (µs) " + (size > 0 ? time/size : time)/1000);

        /*AntimatterAPI.all(RecipeMap.class, t -> {
            Antimatter.LOGGER.info("Recipe map " + t.getId() + " compiled " + t.getRecipes(false).size() + " recipes.");
        });*/
        //Invalidate old tag getter.
        TagUtils.resetSupplier();
    }

    public static void onResourceReload(boolean server) {
        if (server) runDataProvidersDynamically();
        AntimatterAPI.all(RecipeMap.class, RecipeMap::reset);
        AntimatterAPI.all(IRecipeRegistrate.IRecipeLoader.class, IRecipeRegistrate.IRecipeLoader::init);
        AntimatterAPI.all(ModRegistrar.class, t -> {
            for (String mod : t.modIds()) {
                if (!AntimatterAPI.isModLoaded(mod)) return;
            }
            t.antimatterRecipes(AntimatterAPI.getRecipeRegistrate());
        });
        Antimatter.LOGGER.info("Amount of Antimatter Recipe Loaders registered: " + AntimatterAPI.all(IRecipeRegistrate.IRecipeLoader.class).size());
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
