package muramasa.antimatter.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.Ref;
import muramasa.antimatter.client.AntimatterModelLoader.DynamicModelLoader;
import muramasa.antimatter.client.baked.PipeBakedModel;
import muramasa.antimatter.client.model.AntimatterModel;
import muramasa.antimatter.client.model.DynamicModel;
import muramasa.antimatter.datagen.DummyDataGenerator;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.datagen.builder.AntimatterBlockModelBuilder;
import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import muramasa.antimatter.datagen.resources.DynamicResourcePack;
import muramasa.antimatter.datagen.resources.ResourceMethod;
import muramasa.antimatter.registration.IModelProvider;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class AntimatterModelManager {

    public static final ResourceMethod RESOURCE_METHOD = ResourceMethod.DYNAMIC_PACK;

    public static AntimatterModelLoader LOADER_MAIN = new AntimatterModelLoader(new ResourceLocation(Ref.ID, "main"));
    public static DynamicModelLoader LOADER_DYNAMIC = new DynamicModelLoader(new ResourceLocation(Ref.ID, "dynamic"));
    public static DynamicModelLoader LOADER_PIPE = new DynamicModelLoader(new ResourceLocation(Ref.ID, "pipe")) {
        @Override
        public AntimatterModel read(JsonDeserializationContext context, JsonObject json) {
            return new DynamicModel((DynamicModel) super.read(context, json)) {
                @Override
                public IBakedModel bakeModel(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> getter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation loc) {
                    return new PipeBakedModel(getBakedConfigs(owner, bakery, getter, transform, overrides, loc));
                }
            };
        }
    };
    
    private static final Object2ObjectOpenHashMap<String, Supplier<Int2ObjectOpenHashMap<IBakedModel[]>>> STATIC_CONFIG_MAPS = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<ResourceLocation, IItemProviderOverride> ITEM_OVERRIDES = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<ResourceLocation, IBlockProviderOverride> BLOCK_OVERRIDES = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<String, List<Function<DataGenerator, IAntimatterProvider>>> PROVIDERS = new Object2ObjectOpenHashMap<>();

    public static void setup() {
        AntimatterModelManager.registerStaticConfigMap("pipe", () -> PipeBakedModel.CONFIGS);
    }

    public static void registerStaticConfigMap(String staticMapId, Supplier<Int2ObjectOpenHashMap<IBakedModel[]>> configMapSupplier) {
        STATIC_CONFIG_MAPS.put(staticMapId, configMapSupplier);
    }

    public static Int2ObjectOpenHashMap<IBakedModel[]> getStaticConfigMap(String staticMapId) {
        return STATIC_CONFIG_MAPS.getOrDefault(staticMapId, Int2ObjectOpenHashMap::new).get();
    }

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
