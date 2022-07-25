package muramasa.antimatter.datagen.providers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.BlockStone;
import muramasa.antimatter.block.BlockStoneSlab;
import muramasa.antimatter.block.BlockStoneStair;
import muramasa.antimatter.block.BlockStoneWall;
import muramasa.antimatter.block.BlockStorage;
import muramasa.antimatter.datagen.AntimatterRuntimeResourceGeneration;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.ore.BlockOreStone;
import muramasa.antimatter.pipe.BlockItemPipe;
import muramasa.antimatter.pipe.BlockPipe;
import muramasa.antimatter.util.TagUtils;
import net.devtech.arrp.json.tags.JTag;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static muramasa.antimatter.util.TagUtils.getBlockTag;
import static muramasa.antimatter.util.TagUtils.getForgelikeBlockTag;
import static muramasa.antimatter.util.Utils.getConventionalMaterialType;
import static muramasa.antimatter.util.Utils.getConventionalStoneType;

public class AntimatterBlockTagProvider extends BlockTagsProvider implements IAntimatterProvider {

    private final String providerDomain, providerName;
    private final boolean replace;

    public Object2ObjectMap<ResourceLocation, JTag> TAGS = new Object2ObjectOpenHashMap<>();

    public AntimatterBlockTagProvider(String providerDomain, String providerName, boolean replace, DataGenerator gen) {
        super(gen);
        this.providerDomain = providerDomain;
        this.providerName = providerName;
        this.replace = replace;
    }

    @Override
    protected Tag.Builder getOrCreateRawBuilder(TagKey<Block> p_126563_) {
        return this.builders.computeIfAbsent(p_126563_.location(), (p_176838_) -> new Tag.Builder());
    }

    @Override
    public void run() {
        Map<ResourceLocation, Tag.Builder> b = new HashMap<>(this.builders);
        this.builders.clear();
        addTags();
        builders.forEach(this::addTag);
        builders.putAll(b);
    }

    @Override
    public void run(HashCache cache) {

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
                this.tag(getForgelikeBlockTag(String.join("", getConventionalStoneType(o.getStoneType()), "_", getConventionalMaterialType(o.getOreType()), "/", o.getMaterial().getId()))).add(o).replace(replace);
                this.tag(getForgelikeBlockTag(String.join("", getConventionalMaterialType(o.getOreType()), "/", o.getMaterial().getId()))).add(o).replace(replace);
                this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(o).replace(replace);
                if (o.getOreType() == Data.ORE) this.tag(TagUtils.getForgelikeBlockTag("ores")).add(o);
            });
            AntimatterAPI.all(BlockStone.class, s -> {
                if (s.getSuffix().isEmpty()) {
                    this.tag(TagUtils.getForgelikeBlockTag("stone")).add(s);
                } else if (s.getSuffix().equals("cobble")) {
                    this.tag(TagUtils.getForgelikeBlockTag("cobblestone")).add(s);
                } else if (s.getSuffix().contains("bricks")) {
                    this.tag(BlockTags.STONE_BRICKS).add(s);
                }
                this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(s).replace(replace);
                this.tag(getBlockTag(new ResourceLocation("antimatter", "blocks/".concat(s.getId())))).add(s).replace(replace);
            });
            AntimatterAPI.all(BlockStoneWall.class, b -> {
                this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(b).replace(replace);
                this.tag(BlockTags.WALLS).add(b);
            });
            AntimatterAPI.all(BlockStoneSlab.class, b -> {
                this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(b).replace(replace);
                this.tag(BlockTags.SLABS).add(b);
            });
            AntimatterAPI.all(BlockStoneStair.class, b -> {
                this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(b).replace(replace);
                this.tag(BlockTags.STAIRS).add(b);
            });
            AntimatterAPI.all(BlockOreStone.class, s -> {
                String id = "ore_stones/" + s.getMaterial().getId();
                this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(s).replace(replace);
                this.tag(TagUtils.getForgelikeBlockTag("ores")).add(s);
                this.tag(getForgelikeBlockTag(id)).add(s);
            });
            AntimatterAPI.all(BlockStorage.class, block -> {
                this.tag(block.getType().getTag()).add(block).replace(replace);
                String name = String.join("", block.getType().getTag().location().getPath(), "/", (block.getType().getId().equals("raw_ore_block") ? "raw_" : ""), block.getMaterial().getId());
                this.tag(Data.WRENCH.getToolType()).add(block).replace(replace);
                this.tag(getForgelikeBlockTag(name)).add(block);
                // if (block.getType() == FRAME) add climbable tag in 1.16
            });
            AntimatterAPI.all(BlockItemPipe.class, pipe -> {
                this.tag(TagUtils.getBlockTag(new ResourceLocation(Ref.ID, "item_pipe"))).add(pipe);
            });
            AntimatterAPI.all(BlockPipe.class, pipe -> {
                this.tag(pipe.getToolType().getToolType()).add(pipe);
                if (pipe.getType().getMaterial() == Data.Wood){
                    this.tag(Data.WRENCH.getToolType()).add(pipe);
                }
            });
            AntimatterAPI.all(BlockMachine.class, pipe -> {
                this.tag(Data.WRENCH.getToolType()).add(pipe);
            });
        }
    }

    @Override
    public String getName() {
        return providerName;
    }

    // Must append 's' in the identifier
    public void addTag(ResourceLocation loc, JTag obj) {
        TAGS.put(loc, obj);
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
    protected TagAppender<Block> tag(TagKey<Block> tag) {
        Tag.Builder builder = this.getOrCreateRawBuilder(tag);
        return new TagAppender<>(builder, this.registry, providerDomain);
    }

    @Override
    public void onCompletion() {
        TAGS.forEach((k, v) -> {
            AntimatterRuntimeResourceGeneration.DYNAMIC_RESOURCE_PACK.addTag(AntimatterRuntimeResourceGeneration.getTagLoc("blocks", k), v);
            //DynamicResourcePack.addTag("blocks", k, v);
        });
    }
}
