package muramasa.antimatter.datagen.providers;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.BlockStone;
import muramasa.antimatter.block.BlockStorage;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.datagen.builder.AntimatterTagBuilder;
import muramasa.antimatter.item.ItemFluidCell;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialItem;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.material.SubTag;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.ore.BlockOreStone;
import muramasa.antimatter.ore.CobbleStoneType;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.pipe.PipeSize;
import muramasa.antimatter.pipe.types.Cable;
import muramasa.antimatter.pipe.types.Wire;
import muramasa.antimatter.tool.IAntimatterTool;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import muramasa.antimatter.util.TagUtils;
import net.minecraft.core.Registry;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static muramasa.antimatter.data.AntimatterMaterialTypes.BLOCK;
import static muramasa.antimatter.data.AntimatterMaterialTypes.FRAME;
import static muramasa.antimatter.material.MaterialTags.CABLE;
import static muramasa.antimatter.material.MaterialTags.WIRE;
import static muramasa.antimatter.util.TagUtils.*;
import static muramasa.antimatter.util.Utils.getConventionalMaterialType;
import static muramasa.antimatter.util.Utils.getConventionalStoneType;

public class AntimatterItemTagProvider extends AntimatterTagProvider<Item> implements IAntimatterProvider {
    private final boolean replace;
    private final Function<TagKey<Block>, AntimatterTagBuilder<Block>> blockTags;

    public AntimatterItemTagProvider(String providerDomain, String providerName, boolean replace, AntimatterBlockTagProvider p) {
        super(Registry.ITEM, providerDomain, providerName, "items");
        Objects.requireNonNull(p);
        this.blockTags = p::getOrCreateRawBuilder;
        this.replace = replace;
    }

    private void antimatterTags() {
        this.tag(TagUtils.getForgelikeItemTag("pistons")).add(Items.PISTON, Items.STICKY_PISTON);
    }

    protected void processTags(String domain) {
        if (domain.equals(Ref.ID)) antimatterTags();
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
            this.tag(TagUtils.getForgelikeItemTag("stone_ores/coal")).add(Items.COAL_ORE);
            this.tag(TagUtils.getForgelikeItemTag("deepslate_ores/coal")).add(Items.DEEPSLATE_COAL_ORE);
            this.tag(TagUtils.getForgelikeItemTag("stone_ores/iron")).add(Items.IRON_ORE);
            this.tag(TagUtils.getForgelikeItemTag("deepslate_ores/iron")).add(Items.DEEPSLATE_IRON_ORE);
            this.tag(TagUtils.getForgelikeItemTag("stone_ores/copper")).add(Items.COPPER_ORE);
            this.tag(TagUtils.getForgelikeItemTag("deepslate_ores/copper")).add(Items.DEEPSLATE_COPPER_ORE);
            this.tag(TagUtils.getForgelikeItemTag("stone_ores/gold")).add(Items.GOLD_ORE);
            this.tag(TagUtils.getForgelikeItemTag("deepslate_ores/gold")).add(Items.DEEPSLATE_GOLD_ORE);
            this.tag(TagUtils.getForgelikeItemTag("stone_ores/redstone")).add(Items.REDSTONE_ORE);
            this.tag(TagUtils.getForgelikeItemTag("deepslate_ores/redstone")).add(Items.DEEPSLATE_REDSTONE_ORE);
            this.tag(TagUtils.getForgelikeItemTag("stone_ores/emerald")).add(Items.EMERALD_ORE);
            this.tag(TagUtils.getForgelikeItemTag("deepslate_ores/emerald")).add(Items.DEEPSLATE_EMERALD_ORE);
            this.tag(TagUtils.getForgelikeItemTag("stone_ores/lapis")).add(Items.LAPIS_ORE);
            this.tag(TagUtils.getForgelikeItemTag("deepslate_ores/lapis")).add(Items.DEEPSLATE_LAPIS_ORE);
            this.tag(TagUtils.getForgelikeItemTag("stone_ores/diamond")).add(Items.DIAMOND_ORE);
            this.tag(TagUtils.getForgelikeItemTag("deepslate_ores/diamond")).add(Items.DEEPSLATE_DIAMOND_ORE);
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
            AntimatterAPI.all(StoneType.class, s -> {
                if (s instanceof CobbleStoneType c){
                    this.tag(ItemTags.STONE_TOOL_MATERIALS).add(c.getBlock("cobble").asItem());
                }
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
                AntimatterTagBuilder<Item> provider = this.tag(type);
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
            //TODO move this to a felt api module
            if (AntimatterPlatformUtils.isFabric()){
                for (DyeColor dyeColor : DyeColor.values()){
                    this.tag(TagUtils.getForgelikeItemTag("dyes/" + dyeColor.getName())).add(Registry.ITEM.get(new ResourceLocation(dyeColor.getName() + "_dye")));
                }
            }
            processSubtags();
            AntimatterAPI.all(IAntimatterTool.class, tool -> {
                this.tag(tool.getAntimatterToolType().getTag()).add(tool.getItem()).replace(replace);
                this.tag(tool.getAntimatterToolType().getForgeTag()).add(tool.getItem()).replace(replace);
            });
        }


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
            mats = CABLE.allSub(SubTag.COPPER_CABLE);
            if (mats.size() > 0) {
                this.tag(TagUtils.getItemTag(new ResourceLocation(Ref.ID, SubTag.COPPER_CABLE.getId() + "_" + value.getId()))).add(mats.stream().map(t ->
                        AntimatterAPI.get(Cable.class, "cable_" + t.getId())).filter(Objects::nonNull).map(t -> t.getBlockItem(value)).toArray(Item[]::new));
            }
        }
    }

    protected void copy(TagKey<Block> blockTag, TagKey<Item> itemTag) {
        AntimatterTagBuilder<Item> builder = this.getOrCreateRawBuilder(itemTag);
        AntimatterTagBuilder<Block> builder2 = this.blockTags.apply(blockTag);
        Stream<Tag.BuilderEntry> stream = builder2.builder.getEntries();
        Objects.requireNonNull(builder);
        stream.forEach(builder::add);
    }
}