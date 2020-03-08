package muramasa.antimatter.client;

import muramasa.antimatter.Ref;
import muramasa.antimatter.datagen.builder.AntimatterBlockModelBuilder;
import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import muramasa.antimatter.proxy.ClientHandler;
import muramasa.antimatter.registration.IModelProvider;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class AntimatterModelManager {

    public static final AntimatterModelLoader LOADER = new AntimatterModelLoader(new ResourceLocation(Ref.ID, "main"));

    /** A simple cache for Model baking. This avoids 64 models per pipe etc **/
    //TODO clear this at some stage
    private static final Map<String, IBakedModel> BAKED_MODEL_JSON_CACHE = new HashMap<>();

    private static final Map<ResourceLocation, IItemModelOverride> ITEM_OVERRIDES = new HashMap<>();
    private static final Map<ResourceLocation, TriConsumer<Block, AntimatterBlockStateProvider, AntimatterBlockModelBuilder>> BLOCK_OVERRIDES = new HashMap<>();

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
        if (builder != null) ClientHandler.ANTIMATTER_RESOURCES.getPack().addModel(item.asItem().getRegistryName().getNamespace(), "item", item.asItem().getRegistryName().getPath(), builder);
    }

    public static void onBlockModelBuild(IModelProvider b, AntimatterBlockStateProvider prov) {

    }

    public interface IItemModelOverride {
        ItemModelBuilder apply(IItemProvider item, AntimatterItemModelProvider prov);
    }
}
