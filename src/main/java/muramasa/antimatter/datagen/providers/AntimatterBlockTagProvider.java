package muramasa.antimatter.datagen.providers;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.block.BlockStone;
import muramasa.antimatter.block.BlockStorage;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.datagen.resources.DynamicResourcePack;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.ore.BlockOreStone;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.HashMap;
import java.util.Map;

import static muramasa.antimatter.util.Utils.*;

public class AntimatterBlockTagProvider extends BlockTagsProvider implements IAntimatterProvider {

    private final String providerDomain, providerName;
    private final boolean replace;

    public Object2ObjectMap<ResourceLocation, JsonObject> TAGS = new Object2ObjectOpenHashMap<>();

    public AntimatterBlockTagProvider(String providerDomain, String providerName, boolean replace, DataGenerator gen, ExistingFileHelper helper) {
        super(gen, "antimatter", helper);
        this.providerDomain = providerDomain;
        this.providerName = providerName;
        this.replace = replace;
    }

    @Override
    public void run() {
        Map<ResourceLocation, ITag.Builder> b = new HashMap<>(this.tagToBuilder);
        this.tagToBuilder.clear();
        registerTags();
        Utils.getTags(Block.class).forEach(t -> addTag("blocks", t.getName(), getOrCreateBuilder(t).getInternalBuilder()));
        tagToBuilder.forEach((k, v) -> addTag("blocks", k, v));
        b.forEach(tagToBuilder::put);
    }

    @Override
    public Types staticDynamic() {
        return Types.DYNAMIC;
    }


    @Override
    public Dist getSide() {
        return Dist.DEDICATED_SERVER;
    }

    @Override
    public void registerTags() {
        processTags(providerDomain);
    }

    protected void processTags(String domain) {
        AntimatterAPI.all(BlockOre.class,domain, o -> {
            this.getOrCreateBuilder(getForgeBlockTag(String.join("", getConventionalStoneType(o.getStoneType()), "_", getConventionalMaterialType(o.getOreType()), "/", o.getMaterial().getId()))).add(o).replace(replace);
            this.getOrCreateBuilder(getForgeBlockTag(String.join("", getConventionalMaterialType(o.getOreType()), "/", o.getMaterial().getId()))).add(o).replace(replace);
            if (o.getOreType() == Data.ORE) this.getOrCreateBuilder(Tags.Blocks.ORES).add(o);
        });
        AntimatterAPI.all(BlockStone.class,domain, s -> {
            this.getOrCreateBuilder(Tags.Blocks.STONE).add(s);
            this.getOrCreateBuilder(getBlockTag(new ResourceLocation("antimatter", "blocks/".concat(s.getId())))).add(s).replace(replace);
        });
        AntimatterAPI.all(BlockOreStone.class,domain, s -> {
            // String id = getConventionalMaterialType(MaterialType.ORE_STONE);
            this.getOrCreateBuilder(Tags.Blocks.ORES).add(s);
            // this.getBuilder(getForgeBlockTag(id)).add(s);
        });
        AntimatterAPI.all(BlockStorage.class,domain, block -> {
            this.getOrCreateBuilder(block.getType().getTag()).add(block).replace(replace);
            String name = String.join("", block.getType().getTag().getName().getPath(), "/", block.getMaterial().getId());
            this.getOrCreateBuilder(getForgeBlockTag(name)).add(block);
            // if (block.getType() == FRAME) add climbable tag in 1.16
        });
    }

    @Override
    public String getName() {
        return providerName;
    }

    // Must append 's' in the identifier
    public void addTag(String identifier, ResourceLocation loc, JsonObject obj) {
        TAGS.put(getTagLoc(identifier, loc), obj);
    }

    // Must append 's' in the identifier
    // Appends data to the tag.
    public void addTag(String identifier, ResourceLocation loc, ITag.Builder obj) {
        JsonObject json = TAGS.get(getTagLoc(identifier, loc));
        //if no tag just put this one in.
        if (json == null)  {
            addTag(identifier, loc, obj.serialize());
        } else {
            obj = obj.deserialize(json, "Antimatter - Dynamic Data");
            TAGS.put(getTagLoc(identifier, loc), obj.serialize());
        }
    }

    public static ResourceLocation getTagLoc(String identifier, ResourceLocation tagId) {
        return new ResourceLocation(tagId.getNamespace(), String.join("", "tags/", identifier, "/", tagId.getPath(), ".json"));
    }

    @Override
    public void onCompletion() {
        TAGS.forEach(DynamicResourcePack::addTag);
    }
}
