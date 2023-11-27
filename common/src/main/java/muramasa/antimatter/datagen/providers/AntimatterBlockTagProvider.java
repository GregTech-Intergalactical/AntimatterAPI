package muramasa.antimatter.datagen.providers;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.*;
import muramasa.antimatter.data.AntimatterDefaultTools;
import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.data.AntimatterMaterials;
import muramasa.antimatter.data.AntimatterStoneTypes;
import muramasa.antimatter.fluid.AntimatterFluid;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.machine.BlockMultiMachine;
import muramasa.antimatter.material.MaterialTags;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.ore.BlockOreStone;
import muramasa.antimatter.pipe.BlockItemPipe;
import muramasa.antimatter.pipe.BlockPipe;
import muramasa.antimatter.util.TagUtils;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import static muramasa.antimatter.util.TagUtils.getBlockTag;
import static muramasa.antimatter.util.TagUtils.getForgelikeBlockTag;
import static muramasa.antimatter.util.Utils.getConventionalMaterialType;
import static muramasa.antimatter.util.Utils.getConventionalStoneType;

public class AntimatterBlockTagProvider extends AntimatterTagProvider<Block> {
    private final boolean replace;

    public AntimatterBlockTagProvider(String providerDomain, String providerName, boolean replace) {
        super(Registry.BLOCK, providerDomain, providerName, "blocks");
        this.replace = replace;
    }

    protected void processTags(String domain) {
        if (domain.equals(Ref.ID)) {
            AntimatterAPI.all(BlockOre.class, o -> {
                this.tag(getForgelikeBlockTag(String.join("", getConventionalStoneType(o.getStoneType()), "_", getConventionalMaterialType(o.getOreType()), "/", o.getMaterial().getId()))).add(o).replace(replace);
                this.tag(getForgelikeBlockTag(String.join("", getConventionalMaterialType(o.getOreType()), "/", o.getMaterial().getId()))).add(o).replace(replace);

                if (o.getStoneType() == AntimatterStoneTypes.SAND || o.getStoneType() == AntimatterStoneTypes.SAND_RED || o.getStoneType() == AntimatterStoneTypes.GRAVEL)
                    this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(o).replace(replace);
                else
                    this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(o).replace(replace);
                int oreMiningLevel = o.getMaterial().has(MaterialTags.MINING_LEVEL) ? MaterialTags.MINING_LEVEL.getInt(o.getMaterial()) : 0;
                int stoneMiningLevel = o.getStoneType().getHarvestLevel();
                int maxLevel = Math.max(oreMiningLevel, stoneMiningLevel);
                if (maxLevel > 0){
                    this.tag(fromMiningLevel(maxLevel)).add(o);
                }
                if (o.getOreType() == AntimatterMaterialTypes.ORE) this.tag(TagUtils.getForgelikeBlockTag("ores")).add(o);
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
                int stoneMiningLevel = s.getType().getHarvestLevel();
                if (stoneMiningLevel > 0){
                    this.tag(fromMiningLevel(stoneMiningLevel)).add(s);
                }
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
                if (block.getMaterial() == AntimatterMaterials.Wood){
                    this.tag(AntimatterDefaultTools.AXE.getToolType()).add(block);
                } else if (block.getType() == AntimatterMaterialTypes.FRAME){
                    this.tag(AntimatterDefaultTools.WRENCH.getToolType()).add(block).replace(replace);
                } else {
                    this.tag(AntimatterDefaultTools.PICKAXE.getToolType()).add(block);
                }
                this.tag(getForgelikeBlockTag(name)).add(block);
                // if (block.getType() == FRAME) add climbable tag in 1.16
            });
            AntimatterAPI.all(BlockItemPipe.class, pipe -> {
                this.tag(TagUtils.getBlockTag(new ResourceLocation(Ref.ID, "item_pipe"))).add(pipe);
            });
            AntimatterAPI.all(BlockPipe.class, pipe -> {
                this.tag(pipe.getToolType().getToolType()).add(pipe);
                if (pipe.getType().getMaterial() == AntimatterMaterials.Wood){
                    this.tag(AntimatterDefaultTools.AXE.getToolType()).add(pipe);
                }
            });
            AntimatterAPI.all(BlockMachine.class, pipe -> {
                this.tag(AntimatterDefaultTools.WRENCH.getToolType()).add(pipe);
            });
            AntimatterAPI.all(BlockMultiMachine.class, pipe -> {
                this.tag(AntimatterDefaultTools.WRENCH.getToolType()).add(pipe);
            });
            AntimatterAPI.all(AntimatterFluid.class, f -> {
                this.tag(TagUtils.getBlockTag(new ResourceLocation("replaceable"))).add(f.getFluidBlock());
            });
        }
    }

    public TagKey<Block> fromMiningLevel(int miningLevels){
        return switch (miningLevels){
            case 2 -> BlockTags.NEEDS_IRON_TOOL;
            case 3 -> BlockTags.NEEDS_DIAMOND_TOOL;
            default -> BlockTags.NEEDS_STONE_TOOL;
        };
    }
}
