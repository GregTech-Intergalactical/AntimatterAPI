package muramasa.antimatter;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.datagen.ICraftingLoader;
import muramasa.antimatter.datagen.providers.AntimatterRecipeProvider;
import muramasa.antimatter.datagen.resources.DynamicResourcePack;
import muramasa.antimatter.event.AntimatterCraftingEvent;
import muramasa.antimatter.event.AntimatterLoaderEvent;
import muramasa.antimatter.event.AntimatterProvidersEvent;
import muramasa.antimatter.integration.kubejs.RecipeLoaderEventKubeJS;
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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AntimatterDynamics {
    private static final Object2ObjectOpenHashMap<String, List<Function<DataGenerator, IAntimatterProvider>>> PROVIDERS = new Object2ObjectOpenHashMap<>();

    public static void onProviderInit(String domain, DataGenerator gen, Dist side) {
        //PROVIDERS.getOrDefault(domain, Collections.emptyList()).stream().map(f -> f.apply(gen))
        //        .filter(p -> p.getSide().equals(side)).forEach(gen::addProvider);
    }

    /**
     * Providers and Dynamic Resource Pack Section
     **/
    public static void addProvider(String domain, Function<DataGenerator, IAntimatterProvider> providerFunc) {
        PROVIDERS.computeIfAbsent(domain, k -> new ObjectArrayList<>()).add(providerFunc);
    }

    // Can't run this in parallel since ItemTagsProviders need BlockTagsProviders to
    // run first
    public static void runDataProvidersDynamically() {
        DynamicResourcePack.clearServer();
        AntimatterProvidersEvent ev = new AntimatterProvidersEvent(Ref.BACKGROUND_GEN, Dist.DEDICATED_SERVER, Antimatter.INSTANCE);
        MinecraftForge.EVENT_BUS.post(ev);
        Collection<IAntimatterProvider> providers = ev.getProviders();
        long time = System.currentTimeMillis();
        Stream<IAntimatterProvider> async = providers.stream().filter(t -> t.async()).parallel();
        Stream<IAntimatterProvider> sync = providers.stream().filter(t -> !t.async());
        Stream.concat(async, sync).forEach(IAntimatterProvider::run);
        providers.forEach(IAntimatterProvider::onCompletion);
        Antimatter.LOGGER.info("Time to run data providers: " + (System.currentTimeMillis() - time) + " ms.");
    }

    public static void runAssetProvidersDynamically() {
        DynamicResourcePack.clearClient();
        List<IAntimatterProvider> providers = PROVIDERS.object2ObjectEntrySet().stream()
                .flatMap(v -> v.getValue().stream().map(f -> f.apply(Ref.BACKGROUND_GEN)))
                .collect(Collectors.toList());
        //AntimatterProvidersEvent ev = new AntimatterProvidersEvent(Ref.BACKGROUND_GEN, Dist.CLIENT, Antimatter.INSTANCE);
        //MinecraftForge.EVENT_BUS.post(ev);
        //Collection<IAntimatterProvider> providers = ev.getProviders();
        long time = System.currentTimeMillis();
        Stream<IAntimatterProvider> async = providers.stream().filter(IAntimatterProvider::async).parallel();
        Stream<IAntimatterProvider> sync = providers.stream().filter(t -> !t.async());
        Stream.concat(async, sync).forEach(IAntimatterProvider::run);
        providers.forEach(IAntimatterProvider::onCompletion);
        Antimatter.LOGGER.info("Time to run asset providers: " + (System.currentTimeMillis() - time) + " ms.");
    }

    /**
     * Collects all antimatter registered recipes, pushing them to @rec.
     *
     * @param rec consumer for IFinishedRecipe.
     */
    public static void collectRecipes(Consumer<IFinishedRecipe> rec) {
        Set<ResourceLocation> set = Sets.newHashSet();
        AntimatterRecipeProvider provider = new AntimatterRecipeProvider(Ref.ID, "provider", Ref.BACKGROUND_GEN);
        AntimatterCraftingEvent ev = new AntimatterCraftingEvent(Antimatter.INSTANCE);
        MinecraftForge.EVENT_BUS.post(ev);
        for (ICraftingLoader loader : ev.getLoaders()) {
            loader.loadRecipes(t -> {
                if (set.add(t.getID())) {
                    rec.accept(t);
                }
            }, provider);
        }
    }

    public static void onRecipeManagerBuild(Consumer<IFinishedRecipe> objectIn) {
        Antimatter.LOGGER.info("Antimatter recipe manager running..");
        collectRecipes(objectIn);
        AntimatterAPI.all(ModRegistrar.class, t -> {
            for (String mod : t.modIds()) {
                if (!AntimatterAPI.isModLoaded(mod))
                    return;
            }
            t.craftingRecipes(new AntimatterRecipeProvider("Antimatter", "Custom recipes", Ref.BACKGROUND_GEN));
        });
        Antimatter.LOGGER.info("Antimatter recipe manager done..");
    }

    public static void onRecipeCompile(boolean server, RecipeManager manager, ITagCollectionSupplier tags) {
        TagUtils.setSupplier(tags);
        Antimatter.LOGGER.info("Compiling GT recipes");
        long time = System.nanoTime();

        final Set<ResourceLocation> filter;
        // Fire KubeJS event to cancel possible maps.
        if (AntimatterAPI.isModLoaded(Ref.MOD_KJS)) {
            RecipeLoaderEventKubeJS ev = RecipeLoaderEventKubeJS.createAndPost(server);
            filter = ev.forMachines;
        } else {
            filter = Collections.emptySet();
        }
        AntimatterAPI.all(RecipeMap.class, rm -> {
            if (filter.contains(rm.getLoc())) {
                rm.resetCompiled();
                return;
            }
            rm.compile(manager, tags);
        });

        List<Recipe> recipes = manager.getRecipesForType(Recipe.RECIPE_TYPE);
        Map<String, List<Recipe>> map = recipes.stream().collect(Collectors.groupingBy(recipe -> recipe.mapId));

        for (Map.Entry<String, List<Recipe>> entry : map.entrySet()) {
            String[] split = entry.getKey().split(":");
            if (split.length != 2)
                continue;
            RecipeMap<?> rmap = AntimatterAPI.get(RecipeMap.class, split[1], split[0]);
            if (rmap != null)
                entry.getValue().forEach(rec -> rmap.compileRecipe(rec, tags));
        }
        time = System.nanoTime() - time;
        int size = AntimatterAPI.all(RecipeMap.class).stream().mapToInt(t -> t.getRecipes(false).size()).sum();

        Antimatter.LOGGER.info("Time to compile GT recipes: (ms) " + (time) / (1000 * 1000));
        Antimatter.LOGGER.info("No. of GT recipes: " + size);
        Antimatter.LOGGER.info("Average loading time / recipe: (µs) " + (size > 0 ? time / size : time) / 1000);

        /*
         * AntimatterAPI.all(RecipeMap.class, t -> {
         * Antimatter.LOGGER.info("Recipe map " + t.getId() + " compiled " +
         * t.getRecipes(false).size() + " recipes."); });
         */
        // Invalidate old tag getter.
        TagUtils.resetSupplier();
    }

    public static void onResourceReload(boolean server) {
        if (server)
            runDataProvidersDynamically();
        AntimatterAPI.all(RecipeMap.class, RecipeMap::reset);
        final Set<ResourceLocation> filter;
        if (AntimatterAPI.isModLoaded(Ref.MOD_KJS)) {
            RecipeLoaderEventKubeJS ev = RecipeLoaderEventKubeJS.createAndPost(server);
            filter = ev.forLoaders;
        } else {
            filter = Collections.emptySet();
        }
        Map<ResourceLocation, IRecipeRegistrate.IRecipeLoader> loaders = new Object2ObjectOpenHashMap<>(30);
        MinecraftForge.EVENT_BUS.post(new AntimatterLoaderEvent(Antimatter.INSTANCE, (a, b, c) -> {
            if (filter.contains(new ResourceLocation(a, b)))
                return;
            if (loaders.put(new ResourceLocation(a, b), c) != null) {
                Antimatter.LOGGER.warn("Duplicate recipe loader: " + new ResourceLocation(a, b));
            }
        }));

        loaders.values().forEach(IRecipeRegistrate.IRecipeLoader::init);
        AntimatterAPI.all(ModRegistrar.class, t -> {
            for (String mod : t.modIds()) {
                if (!AntimatterAPI.isModLoaded(mod))
                    return;
            }
            // t.antimatterRecipes(AntimatterAPI.getRecipeRegistrate(Ref.ID));
        });
        Antimatter.LOGGER.info("Amount of Antimatter Recipe Loaders registered: " + loaders.size());
        if (server) {
            TagUtils.getTags(Item.class).forEach((k, v) -> {
                DynamicResourcePack.ensureTagAvailable("items", k); // builder.serialize(), false);
            });
            TagUtils.getTags(Fluid.class).forEach((k, v) -> {
                DynamicResourcePack.ensureTagAvailable("fluids", k); // builder.serialize(), false);
            });
            TagUtils.getTags(Block.class).forEach((k, v) -> {
                DynamicResourcePack.ensureTagAvailable("blocks", k); // builder.serialize(), false);
            });
        }
    }
    /*
     * public static void runBackgroundProviders() {
     * Antimatter.LOGGER.info("Running DummyTagProviders...");
     * Ref.BACKGROUND_GEN.addProviders(DummyTagProviders.DUMMY_PROVIDERS); try {
     * Ref.BACKGROUND_GEN.run(); } catch (IOException e) { e.printStackTrace(); } }
     */
}
