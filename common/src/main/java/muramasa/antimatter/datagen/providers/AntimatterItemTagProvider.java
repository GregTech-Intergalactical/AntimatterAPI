package muramasa.antimatter.datagen.providers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.BlockStone;
import muramasa.antimatter.block.BlockStorage;
import muramasa.antimatter.datagen.AntimatterRuntimeResourceGeneration;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.datagen.resources.DynamicResourcePack;
import muramasa.antimatter.item.ItemFluidCell;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialItem;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.material.SubTag;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.ore.BlockOreStone;
import muramasa.antimatter.pipe.PipeSize;
import muramasa.antimatter.pipe.types.Wire;
import muramasa.antimatter.tool.IAntimatterTool;
import muramasa.antimatter.util.TagUtils;
import net.devtech.arrp.json.tags.JTag;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import static muramasa.antimatter.Data.BLOCK;
import static muramasa.antimatter.Data.FRAME;
import static muramasa.antimatter.material.MaterialTags.WIRE;
import static muramasa.antimatter.util.TagUtils.*;
import static muramasa.antimatter.util.Utils.getConventionalMaterialType;
import static muramasa.antimatter.util.Utils.getConventionalStoneType;

public class AntimatterItemTagProvider extends ItemTagsProvider implements IAntimatterProvider {

    private final String providerDomain, providerName;
    private final boolean replace;

    public Object2ObjectMap<ResourceLocation, JTag> TAGS = new Object2ObjectOpenHashMap<>();

    public AntimatterItemTagProvider(String providerDomain, String providerName, boolean replace, DataGenerator gen, BlockTagsProvider p) {
        super(gen, p);
        this.providerDomain = providerDomain;
        this.providerName = providerName;
        this.replace = replace;
    }

    @Override
    protected Tag.Builder getOrCreateRawBuilder(TagKey<Item> p_126563_) {
        return this.builders.computeIfAbsent(p_126563_.location(), (p_176838_) -> new Tag.Builder());
    }

    @Override
    public void run() {
        Map<ResourceLocation, Tag.Builder> b = new HashMap<>(this.builders);
        this.builders.clear();
        addTags();
        //TagUtils.getTags(Item.class).forEach((k,v) -> addTag(k, getOrCreateBuilder(v).getInternalBuilder()));
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
        if (this.providerDomain.equals(Ref.ID)) antimatterTags();
    }

    private void antimatterTags() {
        this.tag(TagUtils.getForgelikeItemTag("pistons")).add(Items.PISTON, Items.STICKY_PISTON);
    }

    protected void processTags(String domain) {
        TagKey<Block> blockTag = BLOCK.getTag(), frameTag = FRAME.getTag();
        this.copy(TagUtils.getForgelikeBlockTag("ores"), TagUtils.getForgelikeItemTag("ores"));
        this.copy(TagUtils.getForgelikeBlockTag("stone"), TagUtils.getForgelikeItemTag("stone"));
        this.copy(TagUtils.getForgelikeBlockTag("storage_blocks"), TagUtils.getForgelikeItemTag("storage_blocks"));
        this.copy(blockTag, blockToItemTag(blockTag));
        this.copy(frameTag, blockToItemTag(frameTag));
        if (domain.equals(Ref.ID)) {
            AntimatterAPI.all(BlockOre.class, o -> {
                //if (o.getOreType() == ORE_SMALL) return;
                String name = String.join("", getConventionalStoneType(o.getStoneType()), "_", getConventionalMaterialType(o.getOreType()), "/", o.getMaterial().getId());
                this.copy(getForgelikeBlockTag(name), getForgelikeItemTag(name));
                String forgeName = String.join("", getConventionalMaterialType(o.getOreType()), "/", o.getMaterial().getId());
                this.copy(getForgelikeBlockTag(forgeName), getForgelikeItemTag(forgeName));
            });
            AntimatterAPI.all(BlockStone.class, s -> {
                String id = "blocks/".concat(s.getId());
                if (s.getSuffix().isEmpty()) {
                    this.tag(TagUtils.getForgelikeItemTag("stone")).add(s.asItem());
                } else if (s.getSuffix().equals("cobble")) {
                    this.tag(TagUtils.getForgelikeItemTag("cobblestone")).add(s.asItem());
                } else if (s.getSuffix().contains("bricks")) {
                    this.tag(ItemTags.STONE_BRICKS).add(s.asItem());
                }
                this.copy(getBlockTag(new ResourceLocation(Ref.ID, id)), getItemTag(new ResourceLocation(Ref.ID, id)));
            });
            AntimatterAPI.all(BlockOreStone.class, domain, s -> {
             String id = "ore_stones/" + s.getMaterial().getId();
             this.copy(getBlockTag(new ResourceLocation(domain, id)), getItemTag(new ResourceLocation(domain, id)));
            });
            AntimatterAPI.all(BlockStorage.class, storage -> {
                MaterialType<?> type = storage.getType();
                String name = String.join("", getConventionalMaterialType(type), "/", storage.getMaterial().getId());
                this.copy(getForgelikeBlockTag(name), getForgelikeItemTag(name));
            });
            AntimatterAPI.all(MaterialItem.class, item -> {
                TagKey<Item> type = item.getType().getTag();
                TagAppender<Item> provider = this.tag(type);
                provider.add(item).replace(replace);
                this.tag(item.getTag()).add(item).replace(replace);
                //if (item.getType() == INGOT || item.getType() == GEM) this.getBuilder(Tags.Items.BEACON_PAYMENT).add(item);
            });
            AntimatterAPI.all(MaterialType.class, t -> {
                t.getReplacements().forEach((m, i) -> {
                    this.tag(t.getMaterialTag((Material) m)).add(((Supplier<Item>)i).get()).replace(replace);
                    this.tag(t.getTag()).add(((Supplier<Item>)i).get()).replace(replace);
                });
            });
            processSubtags();
        }

        AntimatterAPI.all(IAntimatterTool.class, domain, tool -> {
            this.tag(tool.getAntimatterToolType().getTag()).add(tool.getItem()).replace(replace);
            this.tag(tool.getAntimatterToolType().getForgeTag()).add(tool.getItem()).replace(replace);
        });
        this.copy(TagUtils.getBlockTag(new ResourceLocation(Ref.ID, "item_pipe")), TagUtils.getItemTag(new ResourceLocation(Ref.ID, "item_pipe")));
        this.tag(ItemFluidCell.getTag()).add(AntimatterAPI.all(ItemFluidCell.class, domain).toArray(new Item[0]));
    }

    protected void processSubtags() {
        for (PipeSize value : PipeSize.values()) {
            Set<Material> mats = WIRE.allSub(SubTag.COPPER_WIRE);
            if (mats.size() > 0) {
                this.tag(TagUtils.getItemTag(new ResourceLocation(Ref.ID, SubTag.COPPER_WIRE.getId() + "_" + value.getId()))).add(mats.stream().map(t ->
                        AntimatterAPI.get(Wire.class, "wire_" + t.getId())).filter(Objects::nonNull).map(t -> t.getBlockItem(value)).toArray(Item[]::new));
            }
        }
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
    protected TagAppender<Item> tag(TagKey<Item> tag) {
        Tag.Builder builder = this.getOrCreateRawBuilder(tag);
        return new TagAppender(builder, this.registry, providerDomain);
    }

    @Override
    public void onCompletion() {
        TAGS.forEach((k, v) -> {
            AntimatterRuntimeResourceGeneration.DYNAMIC_RESOURCE_PACK.addTag(AntimatterRuntimeResourceGeneration.getTagLoc("items", k), v);
            //DynamicResourcePack.addTag("blocks", k, v);
        });
    }
}