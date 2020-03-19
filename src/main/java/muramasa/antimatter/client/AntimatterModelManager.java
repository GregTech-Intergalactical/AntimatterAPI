package muramasa.antimatter.client;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.Ref;
import muramasa.antimatter.datagen.DummyDataGenerator;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.datagen.builder.AntimatterBlockModelBuilder;
import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import muramasa.antimatter.datagen.resources.DynamicResourcePack;
import muramasa.antimatter.datagen.resources.ResourceMethod;
import muramasa.antimatter.registration.IModelProvider;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class AntimatterModelManager {

    public static final ResourceMethod RESOURCE_METHOD = ResourceMethod.DYNAMIC_PACK;
    public static final AntimatterModelLoader LOADER = new AntimatterModelLoader(new ResourceLocation(Ref.ID, "main"));

    /** A simple cache for Model baking. This avoids 64 models per pipe etc **/
    //TODO clear this at some stage
    private static final Map<String, IBakedModel> BAKED_MODEL_JSON_CACHE = new HashMap<>();

    private static final Map<ResourceLocation, IItemModelOverride> ITEM_OVERRIDES = new HashMap<>();
    private static final Map<ResourceLocation, TriConsumer<Block, AntimatterBlockStateProvider, AntimatterBlockModelBuilder>> BLOCK_OVERRIDES = new HashMap<>();

    private static final Object2ObjectOpenHashMap<String, List<Function<DataGenerator, IAntimatterProvider>>> PROVIDERS = new Object2ObjectOpenHashMap<>();


    public static void addProvider(String domain, Function<DataGenerator, IAntimatterProvider> providerFunc) {
        PROVIDERS.computeIfAbsent(domain, k -> new ArrayList<>()).add(providerFunc);
    }

    public static void onProviderInit(String domain, DataGenerator gen) {
        PROVIDERS.getOrDefault(domain, Collections.emptyList()).forEach(f -> gen.addProvider(f.apply(gen)));
    }

    public static void runProvidersDynamically() {
        DataGenerator gen = new DummyDataGenerator();
        PROVIDERS.forEach((k, v) -> v.forEach(f -> {
            IAntimatterProvider prov = f.apply(gen);
            prov.run();
            if (prov instanceof AntimatterBlockStateProvider) {

            } else if (prov instanceof AntimatterItemModelProvider) {
                ((AntimatterItemModelProvider) prov).generatedModels.forEach(DynamicResourcePack::addItem);
            }
        }));

    }

    public static IBakedModel getBaked(String json, Supplier<IBakedModel> bakedSupplier) {
        IBakedModel existing = BAKED_MODEL_JSON_CACHE.get(json);
        if (existing != null) return existing;
        return bakedSupplier.get();
    }

    public static void put(Item item, IItemModelOverride override) {
        ITEM_OVERRIDES.put(item.getRegistryName(), override);
    }

    public static void put(Block block, TriConsumer<Block, AntimatterBlockStateProvider, AntimatterBlockModelBuilder> consumer) {
        BLOCK_OVERRIDES.put(block.getRegistryName(), consumer);
    }

    public static void onItemModelBuild(IItemProvider item, AntimatterItemModelProvider prov) {
        ItemModelBuilder builder = null;
        IItemModelOverride override = ITEM_OVERRIDES.get(item.asItem().getRegistryName());
        if (override != null) builder = override.apply(item.asItem(), prov);
        if (builder == null && item instanceof IModelProvider) builder = ((IModelProvider) item).onItemModelBuild(item, prov);
        if (builder != null) DynamicResourcePack.addItem(item.asItem().getRegistryName(), builder);
    }

    public static void onBlockModelBuild(IModelProvider b, AntimatterBlockStateProvider prov) {

    }

    public interface IItemModelOverride {
        ItemModelBuilder apply(IItemProvider item, AntimatterItemModelProvider prov);
    }
}
