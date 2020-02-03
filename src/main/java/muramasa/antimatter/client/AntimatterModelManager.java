package muramasa.antimatter.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.Ref;
import muramasa.antimatter.client.model.DynamicModel;
import muramasa.antimatter.client.model.LoaderModel;
import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class AntimatterModelManager {

    /** A simple cache for Model baking. This avoids 64 models per pipe etc **/
    //TODO clear this at some stage
    private static final Map<String, IBakedModel> BAKED_MODEL_JSON_CACHE = new HashMap<>();

    private static final Map<ResourceLocation, BiConsumer<Item, AntimatterItemModelProvider>> ITEM_OVERRIDES = new HashMap<>();
    private static final Map<ResourceLocation, BiConsumer<Block, AntimatterBlockStateProvider>> BLOCK_OVERRIDES = new HashMap<>();
    private static final Map<String, Supplier<IBakedModel>> BAKED_OVERRIDES = new HashMap<>();

    public static final AntimatterModelLoader DEFAULT_LOADER = new AntimatterModelLoader<LoaderModel>(new ResourceLocation(Ref.ID, "default")) {
        @Override
        public LoaderModel read(JsonDeserializationContext context, JsonObject json) {
            try {
                return new LoaderModel(json.has("model") ? context.deserialize(json.get("model"), BlockModel.class) : ModelUtils.getMissingModel());
            } catch (Exception e) {
                Antimatter.LOGGER.error("ModelLoader Exception for " + getLoc().toString());
                e.printStackTrace();
                return new LoaderModel(ModelUtils.getMissingModel());
            }
        }
    };

    public static final AntimatterModelLoader DYNAMIC_LOADER = new AntimatterModelLoader<DynamicModel>(new ResourceLocation(Ref.ID, "dynamic")) {
        @Override
        public DynamicModel read(JsonDeserializationContext context, JsonObject json) {
            try {
                IUnbakedModel baseModel = ModelUtils.getMissingModel();
                Int2ObjectOpenHashMap<Tuple<String, IUnbakedModel>> configModels = new Int2ObjectOpenHashMap<>();
                if (json.has("model") && json.get("model").isJsonObject()) baseModel = context.deserialize(json.get("model"), BlockModel.class);
                if (json.has("config") && json.get("config").isJsonArray()) {
                    for (JsonElement e : json.getAsJsonArray("config")) {
                        if (!e.isJsonObject() || !e.getAsJsonObject().has("id") || !e.getAsJsonObject().has("model")) continue;
                        IUnbakedModel model = context.deserialize(e.getAsJsonObject().get("model"), BlockModel.class);
                        configModels.put(e.getAsJsonObject().get("id").getAsInt(), new Tuple<>(e.toString(), model));
                    }
                }
                return new DynamicModel(baseModel, configModels);
            } catch (Exception e) {
                Antimatter.LOGGER.error("ModelLoader Exception for " + getLoc().toString());
                e.printStackTrace();
                return new DynamicModel(ModelUtils.getMissingModel(), new Int2ObjectOpenHashMap<>());
            }
        }
    };

    public static IBakedModel getBaked(String json, Supplier<IBakedModel> baked) {
        IBakedModel existing = BAKED_MODEL_JSON_CACHE.get(json);
        if (existing != null) return existing;
        return baked.get();
    }

    public static void addOverride(Item item, BiConsumer<Item, AntimatterItemModelProvider> consumer) {
        ITEM_OVERRIDES.put(item.getRegistryName(), consumer);
    }

    public static void addOverride(Block block, BiConsumer<Block, AntimatterBlockStateProvider> consumer) {
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
        BiConsumer<Block, AntimatterBlockStateProvider> consumer = BLOCK_OVERRIDES.get(block.getRegistryName());
        if (consumer != null) {
            consumer.accept(block, prov);
            return true;
        }
        return false;
    }
}
