package muramasa.antimatter.datagen.providers;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.*;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.datagen.resources.DynamicResourcePack;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.ore.BlockOreStone;
import muramasa.antimatter.pipe.BlockItemPipe;
import muramasa.antimatter.util.TagUtils;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.HashMap;
import java.util.Map;

import static muramasa.antimatter.util.TagUtils.getBlockTag;
import static muramasa.antimatter.util.TagUtils.getForgeBlockTag;
import static muramasa.antimatter.util.Utils.getConventionalMaterialType;
import static muramasa.antimatter.util.Utils.getConventionalStoneType;

public class AntimatterBlockTagProvider extends BlockTagsProvider implements IAntimatterProvider {

    private final String providerDomain, providerName;
    private final boolean replace;

    public Object2ObjectMap<ResourceLocation, JsonObject> TAGS = new Object2ObjectOpenHashMap<>();

    public AntimatterBlockTagProvider(String providerDomain, String providerName, boolean replace, DataGenerator gen, ExistingFileHelper helper) {
        super(gen, providerDomain, helper);
        this.providerDomain = providerDomain;
        this.providerName = providerName;
        this.replace = replace;
    }

    @Override
    public void run() {
        Map<ResourceLocation, ITag.Builder> b = new HashMap<>(this.builders);
        this.builders.clear();
        addTags();
        builders.forEach(this::addTag);
        b.forEach(builders::put);
    }

    @Override
    public void run(DirectoryCache cache) {

    }

    @Override
    public void addTags() {
        processTags(providerDomain);
    }

    @Override
    public boolean async() {
        return false;
    }

    protected void processTags(String domain) {
        if (domain.equals(Ref.ID)) {
            AntimatterAPI.all(BlockOre.class, o -> {
                this.tag(getForgeBlockTag(String.join("", getConventionalStoneType(o.getStoneType()), "_", getConventionalMaterialType(o.getOreType()), "/", o.getMaterial().getId()))).add(o).replace(replace);
                this.tag(getForgeBlockTag(String.join("", getConventionalMaterialType(o.getOreType()), "/", o.getMaterial().getId()))).add(o).replace(replace);
                if (o.getOreType() == Data.ORE) this.tag(Tags.Blocks.ORES).add(o);
            });
            AntimatterAPI.all(BlockStone.class, s -> {
                if (s.getSuffix().isEmpty()) {
                    this.tag(Tags.Blocks.STONE).add(s);
                } else if (s.getSuffix().equals("cobble")) {
                    this.tag(Tags.Blocks.COBBLESTONE).add(s);
                } else if (s.getSuffix().contains("bricks")) {
                    this.tag(BlockTags.STONE_BRICKS).add(s);
                }
                this.tag(getBlockTag(new ResourceLocation("antimatter", "blocks/".concat(s.getId())))).add(s).replace(replace);
            });
            AntimatterAPI.all(BlockStoneWall.class, b -> {
                this.tag(BlockTags.WALLS).add(b);
            });
            AntimatterAPI.all(BlockStoneSlab.class, b -> {
                this.tag(BlockTags.SLABS).add(b);
            });
            AntimatterAPI.all(BlockStoneStair.class, b -> {
                this.tag(BlockTags.STAIRS).add(b);
            });
            AntimatterAPI.all(BlockOreStone.class, s -> {
                // String id = getConventionalMaterialType(MaterialType.ORE_STONE);
                this.tag(Tags.Blocks.ORES).add(s);
                // this.getBuilder(getForgeBlockTag(id)).add(s);
            });
            AntimatterAPI.all(BlockStorage.class, block -> {
                this.tag(block.getType().getTag()).add(block).replace(replace);
                String name = String.join("", block.getType().getTag().getName().getPath(), "/", block.getMaterial().getId());
                this.tag(getForgeBlockTag(name)).add(block);
                // if (block.getType() == FRAME) add climbable tag in 1.16
            });
            AntimatterAPI.all(BlockItemPipe.class, pipe -> {
                this.tag(TagUtils.getBlockTag(new ResourceLocation(Ref.ID, "item_pipe"))).add(pipe);
            });
        }
    }

    @Override
    public String getName() {
        return providerName;
    }

    // Must append 's' in the identifier
    public void addTag(ResourceLocation loc, JsonObject obj) {
        TAGS.put(loc, obj);
    }

    // Must append 's' in the identifier
    // Appends data to the tag.
    public void addTag(ResourceLocation loc, ITag.Builder obj) {
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
    public void onCompletion() {
        TAGS.forEach((k, v) -> DynamicResourcePack.addTag("blocks", k, v));
    }
}
