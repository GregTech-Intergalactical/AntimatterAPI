package muramasa.antimatter.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.Ref;
import muramasa.antimatter.client.model.AntimatterModel;
import muramasa.antimatter.client.model.DynamicModel;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelLoader;
import org.apache.commons.lang3.tuple.Triple;

public class AntimatterModelLoader implements IModelLoader<AntimatterModel> {

    public static AntimatterModelLoader MAIN = new AntimatterModelLoader(new ResourceLocation(Ref.ID, "main"));
    public static DynamicModelLoader DYNAMIC = new DynamicModelLoader(new ResourceLocation(Ref.ID, "dynamic"));

    protected ResourceLocation loc;

    public AntimatterModelLoader(ResourceLocation loc) {
        this.loc = loc;
    }

    public ResourceLocation getLoc() {
        return loc;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {

    }

    @Override
    public AntimatterModel read(JsonDeserializationContext context, JsonObject json) {
        try {
            IUnbakedModel baseModel = (json.has("model") && json.get("model").isJsonObject()) ? context.deserialize(json.get("model"), BlockModel.class) : ModelUtils.getMissingModel();
            return new AntimatterModel(baseModel);
        } catch (Exception e) {
            return onModelLoadingException(e);
        }
    }

    public AntimatterModel onModelLoadingException(Exception e) {
        Antimatter.LOGGER.error("ModelLoader Exception for " + getLoc().toString());
        e.printStackTrace();
        return new AntimatterModel(ModelUtils.getMissingModel());
    }

    public int[] buildRotations(JsonObject e) {
        int[] rotations = new int[3];
        if (e.has("rotation") && e.get("rotation").isJsonArray()) {
            JsonArray array = e.get("rotation").getAsJsonArray();
            for (int i = 0; i < Math.min(rotations.length, array.size()); i++) {
                if (array.get(i).isJsonPrimitive() && array.get(i).getAsJsonPrimitive().isNumber()) {
                    rotations[i] = array.get(i).getAsJsonPrimitive().getAsInt();
                }
            }
        }
        return rotations;
    }

    public static class DynamicModelLoader extends AntimatterModelLoader {

        public DynamicModelLoader(ResourceLocation loc) {
            super(loc);
        }

        @Override
        public AntimatterModel read(JsonDeserializationContext context, JsonObject json) {
            try {
                AntimatterModel baseModel = super.read(context, json);
                if (!json.has("config") || !json.get("config").isJsonArray()) return baseModel;
                Int2ObjectOpenHashMap<Triple<String, IUnbakedModel, int[]>> configs = new Int2ObjectOpenHashMap<>();
                for (JsonElement e : json.getAsJsonArray("config")) {
                    if (!e.isJsonObject() || !e.getAsJsonObject().has("id") || !e.getAsJsonObject().has("model")) continue;
                    IUnbakedModel model = context.deserialize(e.getAsJsonObject().get("model"), BlockModel.class);
                    configs.put(e.getAsJsonObject().get("id").getAsInt(), Triple.of(e.toString(), model, buildRotations(e.getAsJsonObject())));
                }
                return new DynamicModel(baseModel, configs);
            } catch (Exception e) {
                return onModelLoadingException(e);
            }
        }
    }
}
