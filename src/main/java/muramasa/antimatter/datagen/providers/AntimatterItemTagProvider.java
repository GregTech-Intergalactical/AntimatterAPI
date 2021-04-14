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
import muramasa.antimatter.material.MaterialItem;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.tool.IAntimatterTool;
import muramasa.antimatter.util.TagUtils;
import net.minecraft.block.Block;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.Tags;

import java.util.HashMap;
import java.util.Map;

import static muramasa.antimatter.Data.BLOCK;
import static muramasa.antimatter.Data.FRAME;
import static muramasa.antimatter.util.TagUtils.*;
import static muramasa.antimatter.util.Utils.getConventionalMaterialType;
import static muramasa.antimatter.util.Utils.getConventionalStoneType;

public class AntimatterItemTagProvider extends ItemTagsProvider implements IAntimatterProvider {

    private final String providerDomain, providerName;
    private final boolean replace;

    public Object2ObjectMap<ResourceLocation, JsonObject> TAGS = new Object2ObjectOpenHashMap<>();

    public AntimatterItemTagProvider(String providerDomain, String providerName, boolean replace, DataGenerator gen, BlockTagsProvider p, ExistingFileHelperOverride fh) {
        super(gen, p, "antimatter", fh);
        this.providerDomain = providerDomain;
        this.providerName = providerName;
        this.replace = replace;
    }

    @Override
    public void run() {
        Map<ResourceLocation, ITag.Builder> b = new HashMap<>(this.tagToBuilder);
        this.tagToBuilder.clear();
        registerTags();
        TagUtils.getTags(Item.class).forEach((k,v) -> addTag("items", k, getOrCreateBuilder(v).getInternalBuilder()));
        tagToBuilder.forEach((k, v) -> addTag("items", k, v));
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
        ITag.INamedTag<Block> blockTag = BLOCK.getTag(), frameTag = FRAME.getTag();
        this.copy(Tags.Blocks.ORES, Tags.Items.ORES);
        this.copy(Tags.Blocks.STONE, Tags.Items.STONE);
        this.copy(Tags.Blocks.STORAGE_BLOCKS, Tags.Items.STORAGE_BLOCKS);
        this.copy(blockTag, blockToItemTag(blockTag));
        this.copy(frameTag, blockToItemTag(frameTag));
        AntimatterAPI.all(BlockOre.class, domain, o -> {
            //if (o.getOreType() == ORE_SMALL) return;
            String name = String.join("", getConventionalStoneType(o.getStoneType()), "_", getConventionalMaterialType(o.getOreType()), "/", o.getMaterial().getId());
            this.copy(getForgeBlockTag(name), getForgeItemTag(name));
            String forgeName = String.join("", getConventionalMaterialType(o.getOreType()), "/", o.getMaterial().getId());
            this.copy(getForgeBlockTag(forgeName), getForgeItemTag(forgeName));
        });
        AntimatterAPI.all(BlockStone.class, domain, s -> {
            String id = "blocks/".concat(s.getId());
            this.copy(getBlockTag(new ResourceLocation(Ref.ID, id)), getItemTag(new ResourceLocation(Ref.ID, id)));
        });
        // AntimatterAPI.all(BlockOreStone.class, domain, s -> {
            // String id = s.getId().replaceAll("_stone_", "s/");
            // this.copy(getBlockTag(new ResourceLocation(domain, id)), getItemTag(new ResourceLocation(domain, id)));
        // });
        AntimatterAPI.all(BlockStorage.class, domain, storage -> {
            MaterialType<?> type = storage.getType();
            String name = String.join("", getConventionalMaterialType(type), "/", storage.getMaterial().getId());
            this.copy(getForgeBlockTag(name), getForgeItemTag(name));
        });
        AntimatterAPI.all(MaterialItem.class,domain, item -> {
            this.getOrCreateBuilder(item.getType().getTag()).add(item).replace(replace);
            this.getOrCreateBuilder(item.getTag()).add(item).replace(replace).replace(replace);
            //if (item.getType() == INGOT || item.getType() == GEM) this.getBuilder(Tags.Items.BEACON_PAYMENT).add(item);
        });
        AntimatterAPI.all(IAntimatterTool.class,domain, tool -> {
            this.getOrCreateBuilder(tool.getType().getTag()).add(tool.getItem()).replace(replace);
            this.getOrCreateBuilder(tool.getType().getForgeTag()).add(tool.getItem()).replace(replace);
        });
        this.copy(TagUtils.getBlockTag(new ResourceLocation(Ref.ID, "item_pipe")), TagUtils.getItemTag(new ResourceLocation(Ref.ID, "item_pipe")));
        this.getOrCreateBuilder(ItemFluidCell.getTag()).add(AntimatterAPI.all(ItemFluidCell.class, domain).toArray(new Item[0]));
    }

    @Override
    public String getName() {
        return providerName;
    }

    // Must append 's' in the identifier
    public void addTag(String identifier, ResourceLocation loc, JsonObject obj) {
        this.TAGS.put(getTagLoc(identifier, loc), obj);
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