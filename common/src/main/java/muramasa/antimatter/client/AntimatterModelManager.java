package muramasa.antimatter.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.Ref;
import muramasa.antimatter.client.model.loader.AntimatterModelLoader;
import muramasa.antimatter.client.model.loader.AntimatterModelLoader.DynamicModelLoader;
import muramasa.antimatter.client.baked.PipeBakedModel;
import muramasa.antimatter.client.model.AntimatterGroupedModel;
import muramasa.antimatter.client.model.loader.DefaultModelLoader;
import muramasa.antimatter.client.model.loader.MachineModelLoader;
import muramasa.antimatter.client.model.ProxyModel;
import muramasa.antimatter.datagen.builder.AntimatterBlockModelBuilder;
import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import muramasa.antimatter.dynamic.DynamicModel;
import muramasa.antimatter.registration.IModelProvider;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.IModelConfiguration;

import java.util.function.Function;
import java.util.function.Supplier;

public class AntimatterModelManager {

    private static final Object2ObjectOpenHashMap<String, Supplier<Int2ObjectOpenHashMap<BakedModel[]>>> STATIC_CONFIG_MAPS = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<ResourceLocation, IItemProviderOverride> ITEM_OVERRIDES = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<ResourceLocation, IBlockProviderOverride> BLOCK_OVERRIDES = new Object2ObjectOpenHashMap<>();

    public static final DefaultModelLoader LOADER_MAIN;
    public static final AntimatterModelLoader.BlockBenchLoader LOADER_COVER;

    public static final AntimatterModelLoader.BlockBenchLoader LOADER_MACHINE_SIDE;


    public static final DynamicModelLoader LOADER_DYNAMIC;
    public static final MachineModelLoader LOADER_MACHINE;
    public static final DynamicModelLoader LOADER_PIPE;

    public static final AntimatterModelLoader<ProxyModel> LOADER_PROXY;

    static {
        LOADER_MAIN = new DefaultModelLoader(new ResourceLocation(Ref.ID, "main"));
        LOADER_COVER = new MachineModelLoader.CoverModelLoader(new ResourceLocation(Ref.ID, "cover"));
        LOADER_MACHINE_SIDE = new MachineModelLoader.SideModelLoader(new ResourceLocation(Ref.ID, "machine_side"));
        LOADER_DYNAMIC = new DynamicModelLoader(new ResourceLocation(Ref.ID, "dynamic"));
        LOADER_MACHINE = new MachineModelLoader(new ResourceLocation(Ref.ID, "machine"));
        LOADER_PIPE = new AntimatterModelLoader.PipeModelLoader(new ResourceLocation(Ref.ID, "pipe"));
        LOADER_PROXY = new AntimatterModelLoader.ProxyModelLoader(new ResourceLocation(Ref.ID, "proxy"));
    }

    public static void init() {
        AntimatterModelManager.registerStaticConfigMap("pipe", () -> PipeBakedModel.CONFIGS);
    }

    public static void registerStaticConfigMap(String staticMapId, Supplier<Int2ObjectOpenHashMap<BakedModel[]>> configMapSupplier) {
        STATIC_CONFIG_MAPS.put(staticMapId, configMapSupplier);
    }

    public static Int2ObjectOpenHashMap<BakedModel[]> getStaticConfigMap(String staticMapId) {
        return STATIC_CONFIG_MAPS.getOrDefault(staticMapId, Int2ObjectOpenHashMap::new).get();
    }

    public static void put(Item item, IItemProviderOverride override) {
        ITEM_OVERRIDES.put(AntimatterPlatformUtils.getIdFromItem(item), override);
    }

    public static void put(Block block, IBlockProviderOverride override) {
        BLOCK_OVERRIDES.put(AntimatterPlatformUtils.getIdFromBlock(block), override);
    }

    public static void onItemModelBuild(ItemLike item, AntimatterItemModelProvider prov) {
        IItemProviderOverride override = ITEM_OVERRIDES.get(AntimatterPlatformUtils.getIdFromItem(item.asItem()));
        if (override != null) override.apply(item.asItem(), prov);
        else if (item instanceof IModelProvider) ((IModelProvider) item).onItemModelBuild(item, prov);
    }

    public static void onBlockModelBuild(Block block, AntimatterBlockStateProvider prov) {
        IBlockProviderOverride override = BLOCK_OVERRIDES.get(AntimatterPlatformUtils.getIdFromBlock(block));
        if (override != null) override.apply(block, prov, prov.getBuilder(block));
        else if (block instanceof IModelProvider) ((IModelProvider) block).onBlockModelBuild(block, prov);
    }

    public interface IItemProviderOverride {
        void apply(ItemLike item, AntimatterItemModelProvider prov);
    }

    public interface IBlockProviderOverride {
        void apply(Block block, AntimatterBlockStateProvider stateProv, AntimatterBlockModelBuilder modelBuilder);
    }
}
