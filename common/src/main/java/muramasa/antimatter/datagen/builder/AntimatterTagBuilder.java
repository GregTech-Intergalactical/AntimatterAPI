package muramasa.antimatter.datagen.builder;

import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class AntimatterTagBuilder<T> {
    public final Tag.Builder builder;
    public final Registry<T> registry;
    public final List<T> removeElements = new ArrayList<>();
    private final String source;
    boolean replace = false;

    public AntimatterTagBuilder(Tag.Builder builder, Registry<T> registry, String string) {
        this.builder = builder;
        this.registry = registry;
        this.source = string;
    }

    public AntimatterTagBuilder<T> add(T item) {
        this.builder.addElement(this.registry.getKey(item), this.source);
        return this;
    }

    public AntimatterTagBuilder<T> add(Tag.BuilderEntry builderEntry){
        this.builder.add(builderEntry);
        return this;
    }

    public AntimatterTagBuilder<T> add(ResourceKey<T>... resourceKeys) {
        for(ResourceKey<T> resourceKey : resourceKeys) {
            this.builder.addElement(resourceKey.location(), this.source);
        }

        return this;
    }

    public AntimatterTagBuilder<T> addOptional(ResourceLocation location) {
        this.builder.addOptionalElement(location, this.source);
        return this;
    }

    public AntimatterTagBuilder<T> addTag(TagKey<T> tag) {
        this.builder.addTag(tag.location(), this.source);
        return this;
    }

    public AntimatterTagBuilder<T> addOptionalTag(ResourceLocation location) {
        this.builder.addOptionalTag(location, this.source);
        return this;
    }

    @SafeVarargs
    public final AntimatterTagBuilder<T> add(T... toAdd) {
        Stream.of(toAdd).map(this.registry::getKey).forEach(resourceLocation -> this.builder.addElement(resourceLocation, this.source));
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
        builder.addFromJson(json, source);
        return this;
    }

    public JsonObject serializeToJson() {
        JsonObject jsonObject = builder.serializeToJson();
        jsonObject.addProperty("replace", replace);
        return jsonObject;
    }
}
