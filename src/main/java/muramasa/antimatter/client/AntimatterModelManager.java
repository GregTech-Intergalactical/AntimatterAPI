package muramasa.antimatter.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.Ref;
import muramasa.antimatter.client.AntimatterModelLoader.DynamicModelLoader;
import muramasa.antimatter.client.baked.MachineBakedModel;
import muramasa.antimatter.client.baked.PipeBakedModel;
import muramasa.antimatter.client.model.AntimatterGroupedModel;
import muramasa.antimatter.client.model.ProxyModel;
import muramasa.antimatter.datagen.builder.AntimatterBlockModelBuilder;
import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import muramasa.antimatter.dynamic.DynamicModel;
import muramasa.antimatter.registration.IModelProvider;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;

import java.util.function.Function;
import java.util.function.Supplier;

public class AntimatterModelManager {

    private static final Object2ObjectOpenHashMap<String, Supplier<Int2ObjectOpenHashMap<IBakedModel[]>>> STATIC_CONFIG_MAPS = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<ResourceLocation, IItemProviderOverride> ITEM_OVERRIDES = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<ResourceLocation, IBlockProviderOverride> BLOCK_OVERRIDES = new Object2ObjectOpenHashMap<>();

    public static final DefaultModelLoader LOADER_MAIN = new DefaultModelLoader(new ResourceLocation(Ref.ID, "main"));
    public static final AntimatterModelLoader.BlockBenchLoader LOADER_COVER = new AntimatterModelLoader.BlockBenchLoader(new ResourceLocation(Ref.ID, "cover")) {
        @Override
        public AntimatterGroupedModel read(JsonDeserializationContext context, JsonObject json) {
            AntimatterGroupedModel model = super.read(context, json);
            return new AntimatterGroupedModel.CoverModel(model);
        }
    };

    public static final DynamicModelLoader LOADER_DYNAMIC = new DynamicModelLoader(new ResourceLocation(Ref.ID, "dynamic"));
    public static final DynamicModelLoader LOADER_MACHINE = new DynamicModelLoader(new ResourceLocation(Ref.ID, "machine")) {
        @Override
        public DynamicModel read(JsonDeserializationContext context, JsonObject json) {
            return new DynamicModel(super.read(context, json)) {
                @Override
                public IBakedModel bakeModel(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> getter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation loc) {
                    return new MachineBakedModel(getter.apply(new RenderMaterial(PlayerContainer.LOCATION_BLOCKS_TEXTURE, particle)), getBakedConfigs(owner, bakery, getter, transform, overrides, loc));
                }
            };
        }
    };
    public static final DynamicModelLoader LOADER_PIPE = new DynamicModelLoader(new ResourceLocation(Ref.ID, "pipe")) {
        @Override
        public DynamicModel read(JsonDeserializationContext context, JsonObject json) {
            return new DynamicModel(super.read(context, json)) {
                @Override
                public IBakedModel bakeModel(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> getter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation loc) {
                    return new PipeBakedModel(getter.apply(new RenderMaterial(PlayerContainer.LOCATION_BLOCKS_TEXTURE, particle)), getBakedConfigs(owner, bakery, getter, transform, overrides, loc));
                }
            };
        }
    };

    public static final AntimatterModelLoader<ProxyModel> LOADER_PROXY = new AntimatterModelLoader<ProxyModel>(new ResourceLocation(Ref.ID, "proxy")) {
        @Override
        public ProxyModel read(JsonDeserializationContext context, JsonObject json) {
            return new ProxyModel();
        }
    };

    public static void init() {
        AntimatterModelManager.registerStaticConfigMap("pipe", () -> PipeBakedModel.CONFIGS);
    }

    public static void registerStaticConfigMap(String staticMapId, Supplier<Int2ObjectOpenHashMap<IBakedModel[]>> configMapSupplier) {
        STATIC_CONFIG_MAPS.put(staticMapId, configMapSupplier);
    }

    public static Int2ObjectOpenHashMap<IBakedModel[]> getStaticConfigMap(String staticMapId) {
        return STATIC_CONFIG_MAPS.getOrDefault(staticMapId, Int2ObjectOpenHashMap::new).get();
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
