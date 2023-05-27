package muramasa.antimatter.datagen;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonSerializer;
import dev.latvian.mods.kubejs.script.ScriptType;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.Ref;
import muramasa.antimatter.datagen.json.JAntimatterModel;
import muramasa.antimatter.datagen.providers.AntimatterBlockLootProvider;
import muramasa.antimatter.datagen.providers.AntimatterLanguageProvider;
import muramasa.antimatter.datagen.providers.AntimatterRecipeProvider;
import muramasa.antimatter.datagen.providers.AntimatterTagProvider;
import muramasa.antimatter.event.CraftingEvent;
import muramasa.antimatter.event.ProvidersEvent;
import muramasa.antimatter.event.WorldGenEvent;
import muramasa.antimatter.integration.kubejs.AMWorldEvent;
import muramasa.antimatter.integration.kubejs.KubeJSRegistrar;
import muramasa.antimatter.integration.kubejs.RecipeLoaderEventKubeJS;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.recipe.IRecipe;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.recipe.loader.IRecipeRegistrate;
import muramasa.antimatter.recipe.map.IRecipeMap;
import muramasa.antimatter.recipe.map.RecipeBuilder;
import muramasa.antimatter.recipe.map.RecipeMap;
import muramasa.antimatter.registration.IAntimatterRegistrar;
import muramasa.antimatter.registration.ModRegistrar;
import muramasa.antimatter.registration.Side;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import muramasa.antimatter.worldgen.AntimatterWorldGenerator;
import muramasa.antimatter.worldgen.smallore.WorldGenSmallOre;
import muramasa.antimatter.worldgen.vanillaore.WorldGenVanillaOre;
import muramasa.antimatter.worldgen.vein.WorldGenVein;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.loot.JCondition;
import net.devtech.arrp.json.models.JTextures;
import net.devtech.arrp.util.UnsafeByteArrayOutputStream;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.storage.loot.Deserializers;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AntimatterDynamics {
    public static final RuntimeResourcePack DYNAMIC_RESOURCE_PACK = RuntimeResourcePack.create(new ResourceLocation(Ref.ID, "dynamic"));
    public static final RuntimeResourcePack RUNTIME_DATA_PACK = RuntimeResourcePack.create(new ResourceLocation(Ref.ID, "data"), 8);
    public static final Gson GSON = Deserializers.createLootTableSerializer()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .registerTypeAdapter(Advancement.Builder.class, (JsonSerializer<Advancement.Builder>) (src, typeOfSrc, context) -> src.serializeToJson())
            .registerTypeAdapter(FinishedRecipe.class, (JsonSerializer<FinishedRecipe>) (src, typeOfSrc, context) -> src.serializeRecipe())
            .registerTypeAdapter(JAntimatterModel.class, new JAntimatterModel.JAntimatterModelSerializer())
            .registerTypeAdapter(JTextures.class, new JTextures.Serializer())
            .registerTypeAdapter(JCondition.class, new JCondition.Serializer())
            .create();
    private static final boolean exportPack = true;

    private static boolean initialized = false;

    public static final Set<ResourceLocation> RECIPE_IDS = Sets.newHashSet();

    public static final Consumer<FinishedRecipe> FINISHED_RECIPE_CONSUMER = f -> {
        if (RECIPE_IDS.add(f.getId())){
            DynamicDataPack.addRecipe(f);
        } else {
            Antimatter.LOGGER.catching(new RuntimeException("Recipe duplicated: " + f.getId()));
        }
    };

    private static final Object2ObjectOpenHashMap<String, List<Supplier<IAntimatterProvider>>> PROVIDERS = new Object2ObjectOpenHashMap<>();

    public static void addResourcePacks(Consumer<PackResources> function){
        if (initialized){
            AntimatterAPI.all(Material.class, Material::setChemicalFormula);
        }
        function.accept(DYNAMIC_RESOURCE_PACK);
    }

    public static void setInitialized(){
        initialized = true;
    }

    public static void addDataPacks(Consumer<PackResources> function){
        if (initialized) {
            AntimatterDynamics.onResourceReload(AntimatterAPI.getSIDE().isServer());
        }
        function.accept(RUNTIME_DATA_PACK);
        function.accept(new DynamicDataPack("antimatter:recipes", AntimatterAPI.all(IAntimatterRegistrar.class).stream().map(IAntimatterRegistrar::getDomain).collect(Collectors.toSet())));

    }

    public static void onProviderInit(String domain, DataGenerator gen, Side side) {
        if (side == Side.CLIENT) {
            PROVIDERS.getOrDefault(domain, Collections.emptyList()).stream().map(Supplier::get)
                    .filter(p -> p instanceof AntimatterLanguageProvider).forEach(gen::addProvider);
        }
    }

    /**
     * Providers and Dynamic Resource Pack Section
     **/
    public static void clientProvider(String domain, Supplier<IAntimatterProvider> providerFunc) {
        PROVIDERS.computeIfAbsent(domain, k -> new ObjectArrayList<>()).add(providerFunc);
    }

    public static void runDataProvidersDynamically() {
        AntimatterBlockLootProvider.init();
        ProvidersEvent ev = AntimatterPlatformUtils.postProviderEvent(AntimatterAPI.getSIDE(), Antimatter.INSTANCE);
        Collection<IAntimatterProvider> providers = ev.getProviders();
        long time = System.currentTimeMillis();
        Stream<IAntimatterProvider> async = providers.stream().filter(IAntimatterProvider::async).parallel();
        Stream<IAntimatterProvider> sync = providers.stream().filter(t -> !t.async());
        Stream.concat(async, sync).forEach(IAntimatterProvider::run);
        providers.forEach(IAntimatterProvider::onCompletion);
        AntimatterTagProvider.afterCompletion();
        Antimatter.LOGGER.info("Time to run data providers: " + (System.currentTimeMillis() - time) + " ms.");
        if (AntimatterConfig.GAMEPLAY.EXPORT_DEFAULT_RECIPES) {
            RUNTIME_DATA_PACK.dump(AntimatterPlatformUtils.getConfigDir().getParent().resolve("dumped"));
        }
    }

    public static void runAssetProvidersDynamically() {
        List<IAntimatterProvider> providers = PROVIDERS.object2ObjectEntrySet().stream()
                .flatMap(v -> v.getValue().stream().map(Supplier::get)).toList();
        long time = System.currentTimeMillis();
        Stream<IAntimatterProvider> async = providers.stream().filter(IAntimatterProvider::async).parallel();
        Stream<IAntimatterProvider> sync = providers.stream().filter(t -> !t.async());
        Stream.concat(async, sync).forEach(IAntimatterProvider::run);
        providers.forEach(IAntimatterProvider::onCompletion);
        AntimatterLanguageProvider.postCompletion();
        Antimatter.LOGGER.info("Time to run asset providers: " + (System.currentTimeMillis() - time) + " ms.");
        if (AntimatterConfig.GAMEPLAY.EXPORT_DEFAULT_RECIPES) {
            DYNAMIC_RESOURCE_PACK.dump(AntimatterPlatformUtils.getConfigDir().getParent().resolve("dumped"));
        }
    }

    /**
     * Collects all antimatter registered recipes, pushing them to @rec.
     *
     * @param rec consumer for IFinishedRecipe.
     */
    public static void collectRecipes(AntimatterRecipeProvider provider, Consumer<FinishedRecipe> rec) {
        CraftingEvent ev = AntimatterPlatformUtils.postCraftingEvent(Antimatter.INSTANCE);
        for (ICraftingLoader loader : ev.getLoaders()) {
            loader.loadRecipes(rec, provider);
        }
    }

    public static void onRecipeManagerBuild(Consumer<FinishedRecipe> objectIn) {
        Antimatter.LOGGER.info("Antimatter recipe manager running..");
        collectRecipes(new AntimatterRecipeProvider(Ref.ID, "provider"), objectIn);
        AntimatterAPI.all(ModRegistrar.class, t -> {
            for (String mod : t.modIds()) {
                if (!AntimatterAPI.isModLoaded(mod))
                    return;
            }
            t.craftingRecipes(new AntimatterRecipeProvider("Antimatter", "Custom recipes"));
        });
        Antimatter.LOGGER.info("Antimatter recipe manager done..");
    }

    public static void onRecipeCompile(boolean server, RecipeManager manager) {
        Antimatter.LOGGER.info("Compiling GT recipes");
        long time = System.nanoTime();

        for (RecipeMap m : AntimatterAPI.all(RecipeMap.class)) {
            if (m.getProxy() != null) {
                List<net.minecraft.world.item.crafting.Recipe<?>> recipes = (List<net.minecraft.world.item.crafting.Recipe<?>>) manager.getAllRecipesFor(m.getProxy().loc());
                recipes.forEach(recipe -> m.compileRecipe(m.getProxy().handler().apply(recipe, m.RB())));
            }
        }

        List<IRecipe> recipes = manager.getAllRecipesFor(Recipe.RECIPE_TYPE);
        Map<String, List<IRecipe>> map = recipes.stream().collect(Collectors.groupingBy(IRecipe::getMapId));

        for (Map.Entry<String, List<IRecipe>> entry : map.entrySet()) {
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
            if (rmap != null){
                entry.getValue().forEach(rmap::compileRecipe);
                //entry.getValue().forEach(rmap::add);
                //rmap.compile(manager);
            }
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
     * Reloads dynamic assets during resource reload.
     */
    public static void onResourceReload(boolean serverEvent) {
        AntimatterRecipeProvider provider = new AntimatterRecipeProvider(Ref.ID, "provider");
        DynamicDataPack.clearServer();
        RECIPE_IDS.clear();
        collectRecipes(provider , FINISHED_RECIPE_CONSUMER);
        AntimatterAPI.all(RecipeMap.class, RecipeMap::reset);
        final Set<ResourceLocation> filter;
        if (AntimatterAPI.isModLoaded(Ref.MOD_KJS)) {
            if (serverEvent) KubeJSRegistrar.checkKubeJSServerScriptManager();
            RecipeLoaderEventKubeJS ev = RecipeLoaderEventKubeJS.createAndPost(serverEvent);
            filter = ev.forLoaders;
        } else {
            filter = Collections.emptySet();
        }
        Map<ResourceLocation, IRecipeRegistrate.IRecipeLoader> loaders = new Object2ObjectOpenHashMap<>(30);
        AntimatterPlatformUtils.postLoaderEvent(Antimatter.INSTANCE, (a, b, c) -> {
            if (filter.contains(new ResourceLocation(a, b)))
                return;
            if (loaders.put(new ResourceLocation(a, b), c) != null) {
                Antimatter.LOGGER.warn("Duplicate recipe loader: " + new ResourceLocation(a, b));
            }
        });
        List<WorldGenVein> veins = new ObjectArrayList<>();
        List<WorldGenSmallOre> smallOres = new ObjectArrayList<>();
        List<WorldGenVanillaOre> vanillaOres = new ObjectArrayList<>();
        boolean runRegular = true;
        if (AntimatterAPI.isModLoaded(Ref.MOD_KJS) && serverEvent) {
            AMWorldEvent ev = new AMWorldEvent();
            ev.post(ScriptType.SERVER, "antimatter.worldgen");
            veins.addAll(ev.VEINS);
            runRegular = !ev.disableBuiltin;
        }
        if (runRegular) {
            WorldGenEvent ev = AntimatterPlatformUtils.postWorldEvent(Antimatter.INSTANCE);
            veins.addAll(ev.VEINS);
            smallOres.addAll(ev.SMALL_ORES);
            vanillaOres.addAll(ev.VANILLA_ORES);
        }
        AntimatterWorldGenerator.clear();
        for (WorldGenVein vein : veins) {
            AntimatterWorldGenerator.register(vein.toRegister, vein);
        }
        for (WorldGenSmallOre smallOre : smallOres){
            AntimatterWorldGenerator.register(smallOre.toRegister, smallOre);
        }
        for (WorldGenVanillaOre vanillaOre : vanillaOres){
            AntimatterWorldGenerator.register(vanillaOre.toRegister, vanillaOre);
        }
        if (AntimatterConfig.WORLD.REGENERATE_DEFAULT_WORLDGEN_JSONS) AntimatterConfig.COMMON_CONFIG.REGENERATE_DEFAULT_WORLDGEN_JSONS.set(false);
        loaders.forEach((r, l) -> {
            RecipeBuilder.setCurrentModId(r.getNamespace());
            l.init();
            RecipeBuilder.setCurrentModId(Ref.SHARED_ID);
        });
        AntimatterAPI.all(ModRegistrar.class, t -> {
            for (String mod : t.modIds()) {
                if (!AntimatterAPI.isModLoaded(mod))
                    return;
            }
            // t.antimatterRecipes(AntimatterAPI.getRecipeRegistrate(Ref.ID));
        });

        Antimatter.LOGGER.info("Amount of Antimatter Recipe Loaders registered: " + loaders.size());
    }

    public static ResourceLocation getTagLoc(String identifier, ResourceLocation tagId) {
        return new ResourceLocation(tagId.getNamespace(), String.join("", identifier, "/", tagId.getPath()));
    }

    public static byte[] serialize(Object object) {
        UnsafeByteArrayOutputStream ubaos = new UnsafeByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(ubaos);
        GSON.toJson(object, writer);
        try {
            writer.close();
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        return ubaos.getBytes();
    }

    public static ResourceLocation fix(ResourceLocation identifier, String prefix, String append) {
        return new ResourceLocation(identifier.getNamespace(), prefix + '/' + identifier.getPath() + '.' + append);
    }
    /*
     * public static void runBackgroundProviders() {
     * Antimatter.LOGGER.info("Running DummyTagProviders...");
     * Ref.BACKGROUND_GEN.addProviders(DummyTagProviders.DUMMY_PROVIDERS); try {
     * Ref.BACKGROUND_GEN.run(); } catch (IOException e) { e.printStackTrace(); } }
     */
}
