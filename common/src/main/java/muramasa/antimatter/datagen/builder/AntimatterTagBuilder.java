package muramasa.antimatter.datagen.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class AntimatterTagBuilder<T> {
    public TagBuilder builder;
    public final Registry<T> registry;
    public final List<T> removeElements = new ArrayList<>();
    private final String source;
    boolean replace = false;

    public AntimatterTagBuilder(TagBuilder builder, Registry<T> registry, String string) {
        this.builder = builder;
        this.registry = registry;
        this.source = string;
    }

    public AntimatterTagBuilder<T> add(T item) {
        this.builder.addElement(this.registry.getKey(item));
        return this;
    }

    public AntimatterTagBuilder<T> add(TagEntry builderEntry){
        this.builder.add(builderEntry);
        return this;
    }

    public AntimatterTagBuilder<T> add(ResourceKey<T>... resourceKeys) {
        for(ResourceKey<T> resourceKey : resourceKeys) {
            this.builder.addElement(resourceKey.location());
        }

        return this;
    }

    public AntimatterTagBuilder<T> add(ResourceLocation... ids) {
        for(ResourceLocation id : ids) {
            this.builder.addElement(id);
        }
        return this;
    }

    public AntimatterTagBuilder<T> addOptional(ResourceLocation location) {
        this.builder.addOptionalElement(location);
        return this;
    }

    public AntimatterTagBuilder<T> addTag(TagKey<T> tag) {
        this.builder.addTag(tag.location());
        return this;
    }

    public AntimatterTagBuilder<T> addTag(ResourceLocation tag){
        this.builder.addTag(tag);
        return this;
    }

    public AntimatterTagBuilder<T> addOptionalTag(ResourceLocation location) {
        this.builder.addOptionalTag(location);
        return this;
    }

    @SafeVarargs
    public final AntimatterTagBuilder<T> add(T... toAdd) {
        Stream.of(toAdd).map(this.registry::getKey).forEach(resourceLocation -> this.builder.addElement(resourceLocation));
        return this;
    }

    @SafeVarargs
    public final AntimatterTagBuilder<T> remove(T... remove){
        removeElements.addAll(Arrays.asList(remove));
        return this;
    }

    public AntimatterTagBuilder<T> replace() {
        return replace(true);
    }

    public AntimatterTagBuilder<T> replace(boolean value) {
        replace = value;
        return this;
    }

    public AntimatterTagBuilder<T> addFromJson(JsonObject json, String source) {
        if (json.get("replace").getAsBoolean()) {
            builder = new TagBuilder();
        }
        JsonArray array = json.getAsJsonArray("values");
        if (!array.isEmpty()) {
            for (JsonElement element : array) {
                if (element.isJsonPrimitive()){
                    String entry = element.getAsString();
                    ResourceLocation id = new ResourceLocation(entry.replace("#", "").replace("?", ""));
                    if (entry.startsWith("#")) {
                        if (entry.endsWith("?")) {
                            addOptionalTag(id);
                        } else {
                            addTag(id);
                        }
                    } else {
                        if (entry.endsWith("?")) {
                            addOptional(id);
                        } else {
                            add(id);
                        }
                    }
                }
            }
        }
        return this;
    }

    public JsonObject serializeToJson() {
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        for (TagEntry entry : builder.build()){
            jsonArray.add(entry.toString());
        }

        jsonObject.addProperty("replace", replace);
        jsonObject.add("values", jsonArray);
        return jsonObject;
    }
}
