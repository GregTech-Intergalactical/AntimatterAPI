package muramasa.antimatter.client;

import dev.architectury.injectables.annotations.ExpectPlatform;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.Ref;
import muramasa.antimatter.client.baked.PipeBakedModel;
import muramasa.antimatter.client.model.loader.*;
import muramasa.antimatter.datagen.builder.AntimatterBlockModelBuilder;
import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import muramasa.antimatter.registration.IModelProvider;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class AntimatterModelManager {

    private static final Object2ObjectOpenHashMap<String, Supplier<Int2ObjectOpenHashMap<BakedModel[]>>> STATIC_CONFIG_MAPS = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<ResourceLocation, IItemProviderOverride> ITEM_OVERRIDES = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<ResourceLocation, IBlockProviderOverride> BLOCK_OVERRIDES = new Object2ObjectOpenHashMap<>();

    public static final ResourceLocation LOADER_MAIN = new ResourceLocation(Ref.ID, "main");
    public static final ResourceLocation LOADER_COVER = new ResourceLocation(Ref.ID, "cover");

    public static final ResourceLocation LOADER_MACHINE_SIDE = new ResourceLocation(Ref.ID, "machine_side");


    public static final ResourceLocation LOADER_DYNAMIC = new ResourceLocation(Ref.ID, "dynamic");
    public static final ResourceLocation LOADER_MACHINE = new ResourceLocation(Ref.ID, "machine");
    public static final ResourceLocation LOADER_PIPE = new ResourceLocation(Ref.ID, "pipe");

    public static final ResourceLocation LOADER_PROXY = new ResourceLocation(Ref.ID, "proxy");

    public static void init() {
        AntimatterModelManager.registerStaticConfigMap("pipe", () -> PipeBakedModel.CONFIGS);

        new DefaultModelLoader(LOADER_MAIN);
        new MachineModelLoader.CoverModelLoader(LOADER_COVER);
        new MachineModelLoader.SideModelLoader(LOADER_MACHINE_SIDE);
        new DynamicModelLoader(LOADER_DYNAMIC);
        new MachineModelLoader(LOADER_MACHINE);
        new PipeModelLoader(LOADER_PIPE);
        new ProxyModelLoader(LOADER_PROXY);
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
