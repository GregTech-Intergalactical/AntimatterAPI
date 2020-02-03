package muramasa.antimatter.client;

import muramasa.antimatter.Ref;
import muramasa.antimatter.datagen.builder.AntimatterBlockModelBuilder;
import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.util.TriConsumer;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class AntimatterModelManager {

    public static final AntimatterModelLoader LOADER = new AntimatterModelLoader(new ResourceLocation(Ref.ID, "main"));

    /** A simple cache for Model baking. This avoids 64 models per pipe etc **/
    //TODO clear this at some stage
    private static final Map<String, IBakedModel> BAKED_MODEL_JSON_CACHE = new HashMap<>();

    private static final Map<ResourceLocation, BiConsumer<Item, AntimatterItemModelProvider>> ITEM_OVERRIDES = new HashMap<>();
    private static final Map<ResourceLocation, TriConsumer<Block, AntimatterBlockStateProvider, AntimatterBlockModelBuilder>> BLOCK_OVERRIDES = new HashMap<>();

    public static IBakedModel getBaked(String json, Supplier<IBakedModel> bakedSupplier) {
        IBakedModel existing = BAKED_MODEL_JSON_CACHE.get(json);
        if (existing != null) return existing;
        return bakedSupplier.get();
    }

    public static void put(Item item, BiConsumer<Item, AntimatterItemModelProvider> consumer) {
        ITEM_OVERRIDES.put(item.getRegistryName(), consumer);
    }

    public static void put(Block block, TriConsumer<Block, AntimatterBlockStateProvider, AntimatterBlockModelBuilder> consumer) {
        BLOCK_OVERRIDES.put(block.getRegistryName(), consumer);
    }

    @Nullable
    public static boolean onItemModelBuild(Item item, AntimatterItemModelProvider prov) {
        BiConsumer<Item, AntimatterItemModelProvider> consumer = ITEM_OVERRIDES.get(item.getRegistryName());
        if (consumer != null) {
            consumer.accept(item, prov);
            return true;
        }
        return false;
    }

    @Nullable
    public static boolean onBlockModelBuild(Block block, AntimatterBlockStateProvider prov) {
        TriConsumer<Block, AntimatterBlockStateProvider, AntimatterBlockModelBuilder> consumer = BLOCK_OVERRIDES.get(block.getRegistryName());
        if (consumer != null) {
            consumer.accept(block, prov, prov.getBuilder(block));
            return true;
        }
        return false;
    }
}
