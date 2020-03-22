package muramasa.antimatter.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.client.model.AntimatterModel;
import muramasa.antimatter.client.model.DynamicModel;
import muramasa.antimatter.registration.IAntimatterObject;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import java.util.Map;

public class AntimatterModelLoader implements IModelLoader<AntimatterModel>, IAntimatterObject {

    protected ResourceLocation loc;

    public AntimatterModelLoader(ResourceLocation loc) {
        this.loc = loc;
        AntimatterAPI.register(AntimatterModelLoader.class, this);
    }

    public ResourceLocation getLoc() {
        return loc;
    }

    @Override
    public String getId() {
        return getLoc().toString();
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {

    }

    @Override
    public AntimatterModel read(JsonDeserializationContext context, JsonObject json) {
        try {
            IUnbakedModel baseModel = (json.has("model") && json.get("model").isJsonObject()) ? context.deserialize(json.get("model"), BlockModel.class) : ModelUtils.getMissingModel();
            return new AntimatterModel(baseModel, buildRotations(json));
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
                if (!json.has("config") || !json.get("config").isJsonObject()) return baseModel;
                Int2ObjectOpenHashMap<IModelGeometry<?>[]> configs = new Int2ObjectOpenHashMap<>();
                IModelGeometry<?>[] models;
                for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("config").entrySet()) {
                    if (!entry.getValue().isJsonArray()) continue;
                    JsonArray array = entry.getValue().getAsJsonArray();
                    models = new IModelGeometry<?>[array.size()];
                    for (int i = 0; i < array.size(); i++) {
                        if (!array.get(i).isJsonObject() || !array.get(i).getAsJsonObject().has("model")) continue;
                        models[i] = new AntimatterModel(context.deserialize(array.get(i).getAsJsonObject().get("model"), BlockModel.class), buildRotations(array.get(i).getAsJsonObject()));
                    }
                    configs.put(Integer.parseInt(entry.getKey()), models);
                }
                String staticMapId = "";
                if (json.has("staticMap") && json.get("staticMap").isJsonPrimitive()) staticMapId = json.get("staticMap").getAsString();
                return new DynamicModel(baseModel, configs, staticMapId);
            } catch (Exception e) {
                return onModelLoadingException(e);
            }
        }
    }
}
