package muramasa.antimatter.client.model.loader;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.client.model.AntimatterGroupedModel;
import muramasa.antimatter.client.model.VanillaProxy;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BlockBenchLoader extends AntimatterModelLoader<AntimatterGroupedModel> {

        public BlockBenchLoader(ResourceLocation loc) {
            super(loc);
        }

        @Override
        public AntimatterGroupedModel readModel(JsonDeserializationContext context, JsonObject json) {
            try {
                ResourceLocation particle = json.has("particle") ? new ResourceLocation(json.get("particle").getAsString()) : MissingTextureAtlasSprite.getLocation();
                Map<Integer, String> offsets = new Object2ObjectOpenHashMap<>();
                if (json.has("groups")) {
                    for (JsonElement jsonelement : GsonHelper.getAsJsonArray(json, "groups")) {
                        if (jsonelement.isJsonObject()) {
                            JsonObject obj = jsonelement.getAsJsonObject();
                            String name = obj.get("name").getAsString();
                            JsonArray children = obj.get("children").getAsJsonArray();
                            for (JsonElement child : children) {
                                offsets.put(child.getAsInt(), name);
                            }
                        }
                    }
                }
                Map<String, List<BlockElement>> map = new Object2ObjectOpenHashMap<>();
                if (json.has("elements")) {
                    int index = 0;
                    for (JsonElement jsonelement : GsonHelper.getAsJsonArray(json, "elements")) {
                        String name = offsets.get(index++);
                        map.computeIfAbsent(name == null ? "" : name, a -> new ObjectArrayList<>()).add(context.deserialize(jsonelement, BlockElement.class));
                    }
                }
                return new AntimatterGroupedModel(particle, map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, k -> new VanillaProxy(k.getValue()))));
            } catch (Exception e) {
                throw new RuntimeException("Caught error deserializing model : " + e);
            }
        }
    }