package muramasa.antimatter.datagen.providers;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.BlockStone;
import muramasa.antimatter.block.BlockStorage;
import muramasa.antimatter.datagen.ExistingFileHelperOverride;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.datagen.resources.DynamicResourcePack;
import muramasa.antimatter.item.ItemFluidCell;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialItem;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.material.SubTag;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.pipe.PipeSize;
import muramasa.antimatter.pipe.types.Wire;
import muramasa.antimatter.tool.IAntimatterTool;
import muramasa.antimatter.util.TagUtils;
import net.minecraft.block.Block;
import net.minecraft.data.*;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static muramasa.antimatter.Data.BLOCK;
import static muramasa.antimatter.Data.FRAME;
import static muramasa.antimatter.material.MaterialTag.WIRE;
import static muramasa.antimatter.util.TagUtils.*;
import static muramasa.antimatter.util.Utils.getConventionalMaterialType;
import static muramasa.antimatter.util.Utils.getConventionalStoneType;

public class AntimatterItemTagProvider extends ItemTagsProvider implements IAntimatterProvider {

    private final String providerDomain, providerName;
    private final boolean replace;

    public Object2ObjectMap<ResourceLocation, JsonObject> TAGS = new Object2ObjectOpenHashMap<>();

    public AntimatterItemTagProvider(String providerDomain, String providerName, boolean replace, DataGenerator gen, BlockTagsProvider p, ExistingFileHelperOverride fh) {
        super(gen, p, providerDomain, fh);
        this.providerDomain = providerDomain;
        this.providerName = providerName;
        this.replace = replace;
    }

    @Override
    public void run() {
        Map<ResourceLocation, ITag.Builder> b = new HashMap<>(this.builders);
        this.builders.clear();
        addTags();
        //TagUtils.getTags(Item.class).forEach((k,v) -> addTag(k, getOrCreateBuilder(v).getInternalBuilder()));
        builders.forEach(this::addTag);
        b.forEach(builders::put);
    }

    @Override
    public void run(DirectoryCache cache) {

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
        this.tag(TagUtils.getForgeItemTag("pistons")).add(Items.PISTON, Items.STICKY_PISTON);
    }

    protected void processTags(String domain) {
        ITag.INamedTag<Block> blockTag = BLOCK.getTag(), frameTag = FRAME.getTag();
        this.copy(Tags.Blocks.ORES, Tags.Items.ORES);
        this.copy(Tags.Blocks.STONE, Tags.Items.STONE);
        this.copy(Tags.Blocks.STORAGE_BLOCKS, Tags.Items.STORAGE_BLOCKS);
        this.copy(blockTag, blockToItemTag(blockTag));
        this.copy(frameTag, blockToItemTag(frameTag));
        if (domain.equals(Ref.ID)) {
            AntimatterAPI.all(BlockOre.class, o -> {
                //if (o.getOreType() == ORE_SMALL) return;
                String name = String.join("", getConventionalStoneType(o.getStoneType()), "_", getConventionalMaterialType(o.getOreType()), "/", o.getMaterial().getId());
                this.copy(getForgeBlockTag(name), getForgeItemTag(name));
                String forgeName = String.join("", getConventionalMaterialType(o.getOreType()), "/", o.getMaterial().getId());
                this.copy(getForgeBlockTag(forgeName), getForgeItemTag(forgeName));
            });
            AntimatterAPI.all(BlockStone.class, s -> {
                String id = "blocks/".concat(s.getId());
                if (s.getSuffix().isEmpty()) {
                    this.tag(Tags.Items.STONE).add(s.asItem());
                } else if (s.getSuffix().equals("cobble")) {
                    this.tag(Tags.Items.COBBLESTONE).add(s.asItem());
                } else if (s.getSuffix().contains("bricks")) {
                    this.tag(ItemTags.STONE_BRICKS).add(s.asItem());
                }
                this.copy(getBlockTag(new ResourceLocation(Ref.ID, id)), getItemTag(new ResourceLocation(Ref.ID, id)));
            });
            // AntimatterAPI.all(BlockOreStone.class, domain, s -> {
            // String id = s.getId().replaceAll("_stone_", "s/");
            // this.copy(getBlockTag(new ResourceLocation(domain, id)), getItemTag(new ResourceLocation(domain, id)));
            // });
            AntimatterAPI.all(BlockStorage.class, storage -> {
                MaterialType<?> type = storage.getType();
                String name = String.join("", getConventionalMaterialType(type), "/", storage.getMaterial().getId());
                this.copy(getForgeBlockTag(name), getForgeItemTag(name));
            });
            AntimatterAPI.all(MaterialItem.class, item -> {
                ITag.INamedTag<Item> type = item.getType().getTag();
                TagsProvider.Builder<Item> provider = this.tag(type);
                provider.add(item).replace(replace);
                this.tag(item.getTag()).add(item).replace(replace);
                //if (item.getType() == INGOT || item.getType() == GEM) this.getBuilder(Tags.Items.BEACON_PAYMENT).add(item);
            });
            AntimatterAPI.all(MaterialType.class, t -> {
                t.getOVERRIDES().forEach((m, i) -> {
                    this.tag(t.getMaterialTag((Material) m)).add(i).replace(replace);
                    this.tag(t.getTag()).add(i).replace(replace);
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
    public void addTag(ResourceLocation loc, JsonObject obj) {
        this.TAGS.put(loc, obj);
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
        TAGS.forEach((k, v) -> DynamicResourcePack.addTag("items", k, v));
    }
}