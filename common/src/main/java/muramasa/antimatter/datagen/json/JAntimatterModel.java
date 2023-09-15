package muramasa.antimatter.datagen.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JAntimatterModel extends JRotationModel {
    List<JConfigEntry> config;
    Map<String, Object> properties = new Object2ObjectArrayMap<>();

    public static JAntimatterModel model() {
        return new JAntimatterModel();
    }

    public static JAntimatterModel model(String parent) {
        JAntimatterModel model = new JAntimatterModel();
        model.parent(parent);
        return model;
    }

    /**
     * @return a new jmodel that does not override it's parent's elements
     */
    public static JAntimatterModel modelKeepElements() {
        JAntimatterModel model = new JAntimatterModel();
        model.elements = null;
        return model;
    }

    public JAntimatterModel property(String id, Object object){
        properties.put(id, object);
        return this;
    }

    public JAntimatterModel configEntry(JConfigEntry... entry){
        if (this.config == null){
            this.config = new ArrayList<>();
        }
        config.addAll(List.of(entry));
        return this;
    }

    public static class JAntimatterModelSerializer implements JsonSerializer<JAntimatterModel> {

        @Override
        public JsonElement serialize(JAntimatterModel src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            if (src.parent != null){
                object.addProperty("parent", src.parent);
            }
            if (src.ambientocclusion != null){
                object.addProperty("ambientocclusion", src.ambientocclusion);
            }
            if (src.display != null){
                object.add("display", context.serialize(src.display));
            }
            if (src.textures != null){
                object.add("textures", context.serialize(src.textures));
            }
            if (src.elements != null && !src.elements.isEmpty()){
                object.add("elements", context.serialize(src.elements));
            }
            if (src.overrides != null && !src.overrides.isEmpty()){
                object.add("overrides", context.serialize(src.overrides));
            }
            if (src.loader != null){
                object.addProperty("loader", src.loader);
            }
            if (src.rotation != null){
                object.add("rotation", context.serialize(src.rotation));
            }
            if (src.config != null){
                object.add("config", context.serialize(src.config));
            }
            if (!src.properties.isEmpty()){
                src.properties.forEach((s, o) -> object.add(s, context.serialize(o)));
            }
            return object;
        }
    }
}
