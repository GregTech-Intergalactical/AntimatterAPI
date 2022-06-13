package muramasa.antimatter.datagen.providers;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.datagen.resources.DynamicResourcePack;
import muramasa.antimatter.fluid.AntimatterFluid;
import muramasa.antimatter.fluid.AntimatterMaterialFluid;
import muramasa.antimatter.material.Material;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.util.HashMap;
import java.util.Map;

import static muramasa.antimatter.util.TagUtils.getForgelikeFluidTag;

public class AntimatterFluidTagProvider extends FluidTagsProvider implements IAntimatterProvider {

    private final String providerDomain, providerName;
    private final boolean replace;

    public Object2ObjectMap<ResourceLocation, JsonObject> TAGS = new Object2ObjectOpenHashMap<>();

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
    public void addTag(ResourceLocation loc, JsonObject obj) {
        this.TAGS.put(loc, obj);
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
            TAGS.put(loc, obj.serializeToJson());
        }
    }

    @Override
    protected TagAppender<Fluid> tag(TagKey<Fluid> tag) {
        Tag.Builder builder = this.getOrCreateRawBuilder(tag);
        return new TagAppender(builder, this.registry, providerDomain);
    }

    @Override
    public void onCompletion() {
        TAGS.forEach((k, v) -> DynamicResourcePack.addTag("fluids", k, v));
    }
}
