package muramasa.antimatter.client.model.loader;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.client.IAntimatterModel;
import muramasa.antimatter.client.model.AntimatterModel;
import muramasa.antimatter.dynamic.DynamicModel;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

public class DynamicModelLoader extends AntimatterModelLoader<DynamicModel> {

        public DynamicModelLoader(ResourceLocation loc) {
            super(loc);
        }

        @Override
        public DynamicModel readModel(JsonDeserializationContext context, JsonObject json) {
            try {
                Int2ObjectOpenHashMap<IAntimatterModel[]> configs = new Int2ObjectOpenHashMap<>();
                for (JsonElement e : json.getAsJsonArray("config")) {
                    if (!e.isJsonObject() || !e.getAsJsonObject().has("id") || !e.getAsJsonObject().has("models"))
                        continue;
                    int id = e.getAsJsonObject().get("id").getAsInt();
                    configs.put(id, buildModels(context, e.getAsJsonObject().get("models").getAsJsonArray()));
                }
                String staticMapId = "";
                if (json.has("staticMap") && json.get("staticMap").isJsonPrimitive())
                    staticMapId = json.get("staticMap").getAsString();
                ResourceLocation particle = json.has("particle") ? new ResourceLocation(json.get("particle").getAsString()) : MissingTextureAtlasSprite.getLocation();
                return new DynamicModel(particle, configs, staticMapId);
            } catch (Exception e) {
                throw new RuntimeException("Caught error deserializing model : " + e);
            }
        }

        public IAntimatterModel[] buildModels(JsonDeserializationContext context, JsonArray array) {
            IAntimatterModel[] models = new IAntimatterModel[array.size()];
            for (int i = 0; i < array.size(); i++) {
                if (!array.get(i).isJsonObject()) continue;
                models[i] = new AntimatterModel(context.deserialize(array.get(i).getAsJsonObject(), BlockModel.class), buildRotations(array.get(i).getAsJsonObject()));
            }
            return models;
        }
    }