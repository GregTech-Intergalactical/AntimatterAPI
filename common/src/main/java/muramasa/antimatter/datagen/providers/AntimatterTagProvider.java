package muramasa.antimatter.datagen.providers;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.datagen.AntimatterDynamics;
import muramasa.antimatter.datagen.IAntimatterProvider;
import net.devtech.arrp.json.tags.JTag;
import net.minecraft.core.Registry;
import net.minecraft.data.HashCache;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AntimatterTagProvider<T> implements IAntimatterProvider {
    private final String providerDomain, providerName, prefix;
    protected final Map<ResourceLocation, Tag.Builder> builders;
    protected final Registry<T> registry;
    public Object2ObjectMap<ResourceLocation, JsonObject> TAGS = new Object2ObjectOpenHashMap<>();
    public static Object2ObjectOpenHashMap<ResourceLocation, JsonObject> TAGS_GLOBAL = new Object2ObjectOpenHashMap<>();

    public static Object2ObjectOpenHashMap<Registry<?>, Map<ResourceLocation, List<Object>>> TAGS_TO_REMOVE = new Object2ObjectOpenHashMap<>();

    public AntimatterTagProvider(Registry<T> registry, String providerDomain, String providerName, String prefix) {
        this.builders = Maps.newLinkedHashMap();
        this.registry = registry;
        this.providerDomain = providerDomain;
        this.providerName = providerName;
        this.prefix = prefix;
        TAGS_TO_REMOVE.computeIfAbsent(registry, r -> new Object2ObjectOpenHashMap<>());
    }

    @Override
    public void run() {
        Map<ResourceLocation, Tag.Builder> b = new HashMap<>(this.builders);
        this.builders.clear();
        processTags(providerDomain);
        builders.forEach(this::addTag);
        builders.putAll(b);
    }

    protected abstract void processTags(String domain);

    @Override
    public void run(HashCache cache) throws IOException {

    }

    @Override
    public boolean async() {
        return false;
    }

    @Override
    public String getName() {
        return providerName;
    }

    protected TagsProvider.TagAppender<T> tag(TagKey<T> tag) {
        Tag.Builder builder = this.getOrCreateRawBuilder(tag);
        return new TagsProvider.TagAppender<>(builder, registry, providerDomain);
    }

    protected Tag.Builder getOrCreateRawBuilder(TagKey<T> tag) {
        return this.builders.computeIfAbsent(tag.location(), (location) -> new Tag.Builder());
    }

    // Must append 's' in the identifier
    public void addTag(ResourceLocation loc, JsonObject obj) {
        TAGS.put(loc, obj);
    }

    public static JTag fromJson(JsonObject obj){
        JTag tag = JTag.tag();
        if (obj.getAsJsonPrimitive("replace").getAsBoolean()) tag.replace();
        JsonArray array = obj.getAsJsonArray("values");
        array.forEach(e -> {
            String s = e.getAsString();
            if (s.contains("#")){
                tag.tag(new ResourceLocation(s.replace("#", "")));
            } else {
                tag.add(new ResourceLocation(s));
            }
        });
        return tag;
    }

    // Must append 's' in the identifier
    // Appends data to the tag.
    public void addTag(ResourceLocation loc, Tag.Builder obj) {
        JsonObject json = TAGS.get(loc);
        //if no tag just put this one in.
        if (json == null) {
            addTag(loc, obj.serializeToJson());
        } else {
            obj = obj.addFromJson(json, "Antimatter - Dynamic Data");
            addTag(loc, obj.serializeToJson());
        }
    }



    @Override
    public void onCompletion() {
        TAGS.forEach((k, v) -> {
            ResourceLocation fixed = AntimatterDynamics.getTagLoc(prefix, k);
            JsonObject json = TAGS_GLOBAL.get(fixed);
            if (json != null) {
                JsonArray local = v.getAsJsonArray("values");
                JsonArray global = json.getAsJsonArray("values");
                global.forEach(local::add);
            }
            TAGS_GLOBAL.put(fixed, v);
        });
    }

    public static void afterCompletion(){
        TAGS_GLOBAL.forEach((k, v) -> AntimatterDynamics.RUNTIME_DATA_PACK.addTag(k, fromJson(v)));
    }
}
