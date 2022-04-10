package muramasa.antimatter;

import com.google.common.collect.Sets;
//import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.ScriptType;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.datagen.ICraftingLoader;
import muramasa.antimatter.datagen.providers.AntimatterLanguageProvider;
import muramasa.antimatter.datagen.providers.AntimatterRecipeProvider;
import muramasa.antimatter.datagen.resources.DynamicResourcePack;
import muramasa.antimatter.event.AntimatterCraftingEvent;
import muramasa.antimatter.event.AntimatterLoaderEvent;
import muramasa.antimatter.event.AntimatterProvidersEvent;
import muramasa.antimatter.event.AntimatterWorldGenEvent;
//import muramasa.antimatter.integration.kubejs.AMWorldEvent;
//import muramasa.antimatter.integration.kubejs.RecipeLoaderEventKubeJS;
import muramasa.antimatter.integration.kubejs.AMWorldEvent;
import muramasa.antimatter.integration.kubejs.RecipeLoaderEventKubeJS;
import muramasa.antimatter.proxy.ClientHandler;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.recipe.loader.IRecipeRegistrate;
import muramasa.antimatter.recipe.map.IRecipeMap;
import muramasa.antimatter.recipe.map.RecipeMap;
import muramasa.antimatter.registration.ModRegistrar;
import muramasa.antimatter.util.TagUtils;
import muramasa.antimatter.worldgen.AntimatterWorldGenerator;
import muramasa.antimatter.worldgen.vein.WorldGenVein;
import net.minecraft.client.Minecraft;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.Collection;
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
        if (side == Dist.CLIENT) {
            PROVIDERS.getOrDefault(domain, Collections.emptyList()).stream().map(f -> f.apply(gen))
                    .filter(p -> p instanceof AntimatterLanguageProvider).forEach(gen::addProvider);
        }
    }

    /**
     * Providers and Dynamic Resource Pack Section
     **/
    public static void clientProvider(String domain, Function<DataGenerator, IAntimatterProvider> providerFunc) {
        PROVIDERS.computeIfAbsent(domain, k -> new ObjectArrayList<>()).add(providerFunc);
    }

    public static void runDataProvidersDynamically() {
        DynamicResourcePack.clearServer();
        AntimatterProvidersEvent ev = new AntimatterProvidersEvent(Ref.BACKGROUND_GEN, Dist.DEDICATED_SERVER, Antimatter.INSTANCE);
        MinecraftForge.EVENT_BUS.post(ev);
        Collection<IAntimatterProvider> providers = ev.getProviders();
        long time = System.currentTimeMillis();
        Stream<IAntimatterProvider> async = providers.stream().filter(IAntimatterProvider::async).parallel();
        Stream<IAntimatterProvider> sync = providers.stream().filter(t -> !t.async());
        Stream.concat(async, sync).forEach(IAntimatterProvider::run);
        providers.forEach(IAntimatterProvider::onCompletion);
        Antimatter.LOGGER.info("Time to run data providers: " + (System.currentTimeMillis() - time) + " ms.");
    }

    public static void runAssetProvidersDynamically() {
        DynamicResourcePack.clearClient();
        List<IAntimatterProvider> providers = PROVIDERS.object2ObjectEntrySet().stream()
                .flatMap(v -> v.getValue().stream().map(f -> f.apply(Ref.BACKGROUND_GEN))).toList();
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
    public static void collectRecipes(Consumer<FinishedRecipe> rec) {
        Set<ResourceLocation> set = Sets.newHashSet();
        AntimatterRecipeProvider provider = new AntimatterRecipeProvider(Ref.ID, "provider", Ref.BACKGROUND_GEN);
        AntimatterCraftingEvent ev = new AntimatterCraftingEvent(Antimatter.INSTANCE);
        MinecraftForge.EVENT_BUS.post(ev);
        for (ICraftingLoader loader : ev.getLoaders()) {
            loader.loadRecipes(t -> {
                if (set.add(t.getId())) {
                    rec.accept(t);
                }
            }, provider);
        }
    }

    public static void onRecipeManagerBuild(Consumer<FinishedRecipe> objectIn) {
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

    public static void onRecipeCompile(boolean server, RecipeManager manager) {
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
        AntimatterAPI.all(IRecipeMap.class, rm -> {
            if (filter.contains(rm.getLoc())) {
                rm.resetCompiled();
                return;
            }
            rm.compile(manager);
        });

        List<Recipe> recipes = manager.getAllRecipesFor(Recipe.RECIPE_TYPE);
        Map<String, List<Recipe>> map = recipes.stream().collect(Collectors.groupingBy(recipe -> recipe.mapId));

        for (Map.Entry<String, List<Recipe>> entry : map.entrySet()) {
            String[] split = entry.getKey().split(":");
            String name;
            if (split.length == 2) {
                name = split[1];
            } else if (split.length == 1) {
                name = split[0];
            } else {
                continue;
            }
            IRecipeMap rmap = AntimatterAPI.get(IRecipeMap.class, name);
            if (rmap != null)
                entry.getValue().forEach(rmap::compileRecipe);
        }
        time = System.nanoTime() - time;
        int size = AntimatterAPI.all(IRecipeMap.class).stream().mapToInt(t -> t.getRecipes(false).size()).sum();

        Antimatter.LOGGER.info("Time to compile GT recipes: (ms) " + (time) / (1000 * 1000));
        Antimatter.LOGGER.info("No. of GT recipes: " + size);
        Antimatter.LOGGER.info("Average loading time / recipe: (µs) " + (size > 0 ? time / size : time) / 1000);

        /*
         * AntimatterAPI.all(RecipeMap.class, t -> {
         * Antimatter.LOGGER.info("Recipe map " + t.getId() + " compiled " +
         * t.getRecipes(false).size() + " recipes."); });
         */
        // Invalidate old tag getter.
       // TagUtils.resetSupplier();
    }

    /**
     * Recipe event for local servers, builds recipes.
     * @param ev forge event callback.
     */
    public static void recipeEvent(RecipesUpdatedEvent ev) {
        if (ClientHandler.isLocal()) {
            //AntimatterDynamics.onResourceReload(false);
            AntimatterDynamics.onRecipeCompile(false, ev.getRecipeManager());
        }
    }

    /**
     * Recipe event for online server, builds recipes.
     * @param ev forge event callback.
     */
    public static void tagsEvent(TagsUpdatedEvent ev) {
        if (!ClientHandler.isLocal()) {
            AntimatterDynamics.onResourceReload(false, false);
            AntimatterDynamics.onRecipeCompile(true, Minecraft.getInstance().getConnection().getRecipeManager());
        }
    }
    /**
     * Reloads dynamic assets during resource reload.
     */
    public static void onResourceReload(boolean runProviders, boolean serverEvent) {
        if (runProviders)
            runDataProvidersDynamically();

        AntimatterAPI.all(RecipeMap.class, RecipeMap::reset);
        final Set<ResourceLocation> filter;
        if (AntimatterAPI.isModLoaded(Ref.MOD_KJS)) {
            RecipeLoaderEventKubeJS ev = RecipeLoaderEventKubeJS.createAndPost(serverEvent);
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
        List<WorldGenVein> veins = new ObjectArrayList<>();
        boolean runRegular = true;
        if (AntimatterAPI.isModLoaded(Ref.MOD_KJS) && serverEvent) {
            AMWorldEvent ev = new AMWorldEvent();
            ev.post(ScriptType.SERVER, "antimatter.worldgen");
            veins.addAll(ev.VEINS);
            runRegular = !ev.disableBuiltin;
        }
        if (runRegular) {
            AntimatterWorldGenEvent ev = new AntimatterWorldGenEvent(Antimatter.INSTANCE);
            MinecraftForge.EVENT_BUS.post(ev);
            veins.addAll(ev.VEINS);
        }
        AntimatterWorldGenerator.clear();
        for (WorldGenVein vein : veins) {
            AntimatterWorldGenerator.register(vein.toRegister, vein);
        }
        loaders.values().forEach(IRecipeRegistrate.IRecipeLoader::init);
        AntimatterAPI.all(ModRegistrar.class, t -> {
            for (String mod : t.modIds()) {
                if (!AntimatterAPI.isModLoaded(mod))
                    return;
            }
            // t.antimatterRecipes(AntimatterAPI.getRecipeRegistrate(Ref.ID));
        });

        Antimatter.LOGGER.info("Amount of Antimatter Recipe Loaders registered: " + loaders.size());
        /*if (server) {
            TagUtils.getTags(Item.class).forEach((k, v) -> {
                DynamicResourcePack.ensureTagAvailable("items", k); // builder.serialize(), false);
            });
            TagUtils.getTags(Fluid.class).forEach((k, v) -> {
                DynamicResourcePack.ensureTagAvailable("fluids", k); // builder.serialize(), false);
            });
            TagUtils.getTags(Block.class).forEach((k, v) -> {
                DynamicResourcePack.ensureTagAvailable("blocks", k); // builder.serialize(), false);
            });
        }*/
    }
    /*
     * public static void runBackgroundProviders() {
     * Antimatter.LOGGER.info("Running DummyTagProviders...");
     * Ref.BACKGROUND_GEN.addProviders(DummyTagProviders.DUMMY_PROVIDERS); try {
     * Ref.BACKGROUND_GEN.run(); } catch (IOException e) { e.printStackTrace(); } }
     */
}
