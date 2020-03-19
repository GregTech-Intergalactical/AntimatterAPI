package muramasa.antimatter.client;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class AntimatterModelManager {

    public static final ResourceMethod RESOURCE_METHOD = ResourceMethod.DYNAMIC_PACK;

    /** A simple cache for Model baking. This avoids 64 models per pipe etc **/
    //TODO clear this at some stage
    private static final Object2ObjectOpenHashMap<String, IBakedModel> BAKED_MODEL_JSON_CACHE = new Object2ObjectOpenHashMap<>();

    private static final Object2ObjectOpenHashMap<ResourceLocation, IItemProviderOverride> ITEM_OVERRIDES = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<ResourceLocation, IBlockProviderOverride> BLOCK_OVERRIDES = new Object2ObjectOpenHashMap<>();

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

    public static void put(Item item, IItemProviderOverride override) {
        ITEM_OVERRIDES.put(item.getRegistryName(), override);
    }

    public static void put(Block block, IBlockProviderOverride override) {
        BLOCK_OVERRIDES.put(block.getRegistryName(), override);
    }

    public static void onItemModelBuild(IItemProvider item, AntimatterItemModelProvider prov) {
        IItemProviderOverride override = ITEM_OVERRIDES.get(item.asItem().getRegistryName());
        if (override != null) override.apply(item.asItem(), prov);
        else if (item instanceof IModelProvider) ((IModelProvider) item).onItemModelBuild(item, prov);
    }

    public static void onBlockModelBuild(Block block, AntimatterBlockStateProvider prov) {
        IBlockProviderOverride override = BLOCK_OVERRIDES.get(block.getRegistryName());
        if (override != null) override.apply(block, prov, prov.getBuilder(block));
        else if (block instanceof IModelProvider) ((IModelProvider) block).onBlockModelBuild(block, prov);
    }

    public interface IItemProviderOverride {
        void apply(IItemProvider item, AntimatterItemModelProvider prov);
    }

    public interface IBlockProviderOverride {
        void apply(Block block, AntimatterBlockStateProvider stateProv, AntimatterBlockModelBuilder modelBuilder);
    }
}
