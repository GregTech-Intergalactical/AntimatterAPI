package muramasa.antimatter.datagen.providers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.datagen.AntimatterRuntimeResourceGeneration;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.datagen.resources.DynamicResourcePack;
import muramasa.antimatter.fluid.AntimatterFluid;
import muramasa.antimatter.fluid.AntimatterMaterialFluid;
import muramasa.antimatter.material.Material;
import net.devtech.arrp.json.tags.JTag;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static muramasa.antimatter.util.TagUtils.getForgelikeFluidTag;

public class AntimatterFluidTagProvider extends FluidTagsProvider implements IAntimatterProvider {

    private final String providerDomain, providerName;
    private final boolean replace;

    public Object2ObjectMap<ResourceLocation, JTag> TAGS = new Object2ObjectOpenHashMap<>();

    public AntimatterFluidTagProvider(String providerDomain, String providerName, boolean replace, DataGenerator gen) {
        super(gen);
        this.providerDomain = providerDomain;
        this.providerName = providerName;
        this.replace = replace;
    }

    @Override
    protected Tag.Builder getOrCreateRawBuilder(TagKey<Fluid> p_126563_) {
        return this.builders.computeIfAbsent(p_126563_.location(), (p_176838_) -> new Tag.Builder());
    }

    @Override
    public void run() {
        Map<ResourceLocation, Tag.Builder> b = new HashMap<>(this.builders);
        this.builders.clear();
        addTags();
        //TagUtils.getTags(Fluid.class).forEach((k,v)-> addTag(k, getOrCreateBuilder(v).getInternalBuilder()));
        builders.forEach(this::addTag);
        b.forEach(builders::put);
    }

    @Override
    public void run(HashCache cache) {

    }

    @Override
    public boolean async() {
        return false;
    }

    @Override
    public void addTags() {
        processTags(providerDomain);
    }

    protected void processTags(String domain) {
        AntimatterAPI.all(AntimatterFluid.class, domain).forEach(f -> {
            tag(getForgelikeFluidTag(f.getId()))
                    .add(f.getFluid(), f.getFlowingFluid())
                    .replace(replace);
            if (f instanceof AntimatterMaterialFluid) {
                Material m = ((AntimatterMaterialFluid) f).getMaterial();
                tag(getForgelikeFluidTag(m.getId()))
                        .add(f.getFluid(), f.getFlowingFluid())
                        .replace(replace);
            }
        });
    }

    @Override
    public String getName() {
        return providerName;
    }

    // Must append 's' in the identifier
    public void addTag(ResourceLocation loc, JTag obj) {
        this.TAGS.put(loc, obj);
    }

    public JTag fromJson(JsonObject obj){
        JTag tag = JTag.tag();
        if (obj.getAsJsonObject("replace").getAsBoolean()) tag.replace();
        JsonArray array = obj.getAsJsonArray("values");
        array.forEach(e -> {
            tag.add(new ResourceLocation(e.getAsString()));
        });
        return tag;
    }

    // Must append 's' in the identifier
    // Appends data to the tag.
    public void addTag(ResourceLocation loc, Tag.Builder obj) {
        JTag jTag = TAGS.get(loc);
        //if no tag just put this one in.
        if (jTag == null) {
            addTag(loc, fromJson(obj.serializeToJson()));
        } else {
            JsonObject json = fromJTag(jTag);
            obj = obj.addFromJson(json, "Antimatter - Dynamic Data");
            addTag(loc, fromJson(obj.serializeToJson()));
        }
    }

    public JsonObject fromJTag(JTag tag){
        JsonObject json = new JsonObject();
        try {
            Field replace = tag.getClass().getDeclaredField("replace");
            replace.setAccessible(true);
            json.addProperty("replace", (Boolean) replace.get(tag));
            Field values = tag.getClass().getDeclaredField("values");
            values.setAccessible(true);
            List<String> entries = (List<String>) values.get(tag);
            JsonArray array = new JsonArray();
            entries.forEach(array::add);
            json.add("values", array);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
        }
        return json;
    }

    @Override
    protected TagAppender<Fluid> tag(TagKey<Fluid> tag) {
        Tag.Builder builder = this.getOrCreateRawBuilder(tag);
        return new TagAppender(builder, this.registry, providerDomain);
    }

    @Override
    public void onCompletion() {
        TAGS.forEach((k, v) -> {
            AntimatterRuntimeResourceGeneration.DYNAMIC_RESOURCE_PACK.addTag(AntimatterRuntimeResourceGeneration.getTagLoc("fluids", k), v);
            //DynamicResourcePack.addTag("blocks", k, v);
        });
    }
}
