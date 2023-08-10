package muramasa.antimatter.data;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.material.MaterialTags;
import muramasa.antimatter.pipe.BlockPipe;
import muramasa.antimatter.registration.Side;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.tool.MaterialSword;
import muramasa.antimatter.tool.armor.AntimatterArmorType;
import muramasa.antimatter.tool.behaviour.*;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import static muramasa.antimatter.data.AntimatterMaterialTypes.*;
import static net.minecraft.world.level.material.Material.*;

public class AntimatterDefaultTools {
    public static final AntimatterToolType SWORD = new AntimatterToolType(Ref.ID, "sword", 2, 1, 10, 3.0F, -2.4F, false).setToolClass(MaterialSword.class).addEffectiveBlocks(Blocks.COBWEB).setHasContainer(false).setMaterialType(SWORD_HEAD);
    public static final AntimatterToolType PICKAXE = new AntimatterToolType(Ref.ID, "pickaxe", 1, 2, 10, 1.0F, -2.8F, true).addEffectiveMaterials(ICE_SOLID, PISTON).setHasContainer(false).setMaterialType(PICKAXE_HEAD);
    public static final AntimatterToolType DRILL = new AntimatterToolType(Ref.ID, "drill", 2, 2, 10, 3.0F, -3.0F, false).setType(PICKAXE).setUseAction(UseAnim.BLOCK).setPowered(100000, 1, 2, 3).setMaterialType(DRILLBIT).setUseSound(Ref.DRILL).addTags("pickaxe", "shovel").addEffectiveMaterials(ICE_SOLID, PISTON, DIRT, CLAY, GRASS, SAND).setRepairability(false);
    public static final AntimatterToolType SHOVEL = new AntimatterToolType(Ref.ID, "shovel", 1, 2, 10, 1.5F, -3.0F, true).addEffectiveMaterials(CLAY, SAND, TOP_SNOW, SNOW, DIRT).setHasContainer(false).setMaterialType(SHOVEL_HEAD);
    public static final AntimatterToolType AXE = new AntimatterToolType(Ref.ID, "axe", 1, 1, 10, 6.0F, -3.0F, true).addEffectiveMaterials(PLANT, REPLACEABLE_PLANT, BAMBOO).setHasContainer(false).setMaterialType(AXE_HEAD);
    public static final AntimatterToolType HOE = new AntimatterToolType(Ref.ID, "hoe", 1, 2, 10, -2.0F, -1.0F, true).setHasContainer(false).setMaterialType(HOE_HEAD);
    public static final AntimatterToolType HAMMER = new AntimatterToolType(Ref.ID, "hammer", 1, 2, 2, 3.0F, -3.0F, false).addTags("pickaxe").addEffectiveMaterials(net.minecraft.world.level.material.Material.METAL, STONE).setUseSound(SoundEvents.ANVIL_PLACE).setRepairability(false);
    public static final AntimatterToolType WRENCH = new AntimatterToolType(Ref.ID, "wrench", 2, 2, 2, 1.5F, -2.8F, false).setUseSound(Ref.WRENCH).addEffectiveBlocks(Blocks.HOPPER).setOverlayLayers(0).setRepairability(false);
    public static final AntimatterToolType ELECTRIC_WRENCH = new AntimatterToolType(Ref.ID, "electric_wrench", WRENCH).setTag(WRENCH).setPowered(100000, 1, 2, 3).setUseSound(Ref.WRENCH).addEffectiveBlocks(Blocks.HOPPER).addTags("wrench").setMaterialType(WRENCHBIT);
    public static final AntimatterToolType SAW = new AntimatterToolType(Ref.ID, "saw", 2, 2, 2, 2.0F, -2.8F, false).addEffectiveBlocks(Blocks.ICE, Blocks.PACKED_ICE, Blocks.BLUE_ICE).setRepairability(false).setMaterialType(SAW_HEAD);
    public static final AntimatterToolType BUZZSAW = new AntimatterToolType(Ref.ID, "buzzsaw", 2, 2, 2, 0.5F, -2.7F, false).setTag(SAW).setPowered(100000, 1, 2, 3).setOverlayLayers(2).addTags("saw").setMaterialType(SAW_HEAD);
    public static final AntimatterToolType FILE = new AntimatterToolType(Ref.ID, "file", 2, 2, 2, -2.0F, -2.4F, false).setRepairability(false).setMaterialType(FILE_HEAD);
    public static final AntimatterToolType CROWBAR = new AntimatterToolType(Ref.ID, "crowbar", 2, 10, 5, 1.0F, -2.0F, false).setUseSound(SoundEvents.ITEM_BREAK).setSecondaryRequirement(MaterialTags.RUBBERTOOLS).setRepairability(false);
    public static final AntimatterToolType SOFT_HAMMER = new AntimatterToolType(Ref.ID, "soft_hammer", 2, 2, 2, 1.0F, -3.0F, false).setRepairability(false);//.setUseSound();
    public static final AntimatterToolType SCREWDRIVER = new AntimatterToolType(Ref.ID, "screwdriver", 2, 2, 2, 0.0F, -1.0F, false).setUseSound(Ref.WRENCH).setRepairability(false);
    public static final AntimatterToolType ELECTRIC_SCREWDRIVER = new AntimatterToolType(Ref.ID, "electric_screwdriver", SCREWDRIVER).setTag(SCREWDRIVER).setPowered(100000, 1, 2, 3).setUseSound(Ref.WRENCH).setOverlayLayers(2);
    public static final AntimatterToolType MORTAR = new AntimatterToolType(Ref.ID, "mortar", 5, 5, 2, -2.0F, 0.0F, false).setUseSound(SoundEvents.GRINDSTONE_USE).setBlockBreakability(false).setRepairability(false);
    public static final AntimatterToolType WIRE_CUTTER = new AntimatterToolType(Ref.ID, "wire_cutter", 5, 3, 2, 0.0F, -1.5F, false).setUseSound(SoundEvents.SHEEP_SHEAR).addEffectiveMaterials(WOOL, SPONGE, WEB, CLOTH_DECORATION).setRepairability(false);
    public static final AntimatterToolType BRANCH_CUTTER = new AntimatterToolType(Ref.ID, "branch_cutter", 1, 3, 2, 0.0F, -1.5F, false).addTags("grafter").addEffectiveMaterials(LEAVES).setHasContainer(false);
    public static final AntimatterToolType KNIFE = new AntimatterToolType(Ref.ID, "knife", 2, 2, 5, 2.1F, -2.0F, false).setToolClass(MaterialSword.class).addEffectiveBlocks(Blocks.COBWEB).setRepairability(false);
    public static final AntimatterToolType PLUNGER = new AntimatterToolType(Ref.ID, "plunger", 5, 5, 10, 0.0F, -2.9F, false).setUseSound(SoundEvents.BUCKET_EMPTY).setPrimaryRequirement(MaterialTags.RUBBERTOOLS).setRepairability(false);
    public static final AntimatterToolType CHAINSAW = new AntimatterToolType(Ref.ID, "chainsaw", 2, 1, 5, 3.0F, -2.0F, false).setUseAction(UseAnim.BLOCK).setPowered(100000, 1, 2, 3).setMaterialType(CHAINSAWBIT).addEffectiveMaterials(WOOD, PLANT, REPLACEABLE_PLANT, BAMBOO, LEAVES).addTags("axe", "saw");
    public static final AntimatterToolType JACKHAMMER = new AntimatterToolType(Ref.ID, "jackhammer", 2, 2, 10, 1.0F, -3.2F, false).setPowered(100000, 1, 2, 3).setUseSound(Ref.DRILL).addEffectiveMaterials(STONE, DIRT, SAND, GRASS).setOverlayLayers(2);
    public static final AntimatterArmorType HELMET = new AntimatterArmorType(Ref.ID, "helmet", 40, 0, 0.0F, 0.0F, EquipmentSlot.HEAD);
    public static final AntimatterArmorType CHESTPLATE = new AntimatterArmorType(Ref.ID, "chestplate", 40, 0, 0.0F, 0.0F, EquipmentSlot.CHEST);
    public static final AntimatterArmorType LEGGINGS = new AntimatterArmorType(Ref.ID, "leggings", 40, 0, 0.0F, 0.0F, EquipmentSlot.LEGS);
    public static final AntimatterArmorType BOOTS = new AntimatterArmorType(Ref.ID, "boots", 40, 0, 0.0F, 0.0F, EquipmentSlot.FEET);

    public static void init(Side side){
        for (AntimatterToolType type : AntimatterAPI.all(AntimatterToolType.class)) {
            if (type.getActualTags().contains(BlockTags.MINEABLE_WITH_SHOVEL)) type.addBehaviour(BehaviourVanillaShovel.INSTANCE);
            if (type.getActualTags().contains(BlockTags.MINEABLE_WITH_HOE)) type.addBehaviour(BehaviourBlockTilling.INSTANCE);
            if (type.isPowered()) type.addBehaviour(BehaviourPoweredDebug.INSTANCE);
        }
        AXE.addBehaviour(BehaviourLogStripping.INSTANCE, BehaviourTreeFelling.INSTANCE);
        PICKAXE.addBehaviour(BehaviourTorchPlacing.INSTANCE);
        CHAINSAW.addBehaviour(BehaviourTreeFelling.INSTANCE, BehaviourLogStripping.INSTANCE);
        DRILL.addBehaviour(new BehaviourAOEBreak(1, 1, 1, "3x3"), BehaviourTorchPlacing.INSTANCE);
        JACKHAMMER.addBehaviour(new BehaviourAOEBreak(1, 0, 2, "1x0x2"));
        PLUNGER.addBehaviour(BehaviourWaterlogToggle.INSTANCE);
        KNIFE.addBehaviour(BehaviourPumpkinCarving.INSTANCE);
        if (side == Side.CLIENT) {
            clientInit();
        }
    }
    private static void clientInit() {
        AntimatterDefaultTools.WRENCH.addBehaviour(new BehaviourExtendedHighlight(b -> b instanceof BlockMachine || (b instanceof BlockPipe && b.builtInRegistryHolder().is(AntimatterDefaultTools.WRENCH.getToolType())) || b.defaultBlockState().hasProperty(BlockStateProperties.FACING_HOPPER), BehaviourExtendedHighlight.PIPE_FUNCTION));
        AntimatterDefaultTools.SCREWDRIVER.addBehaviour(new BehaviourExtendedHighlight(b -> b instanceof BlockMachine || b instanceof BlockPipe, BehaviourExtendedHighlight.COVER_FUNCTION));
        AntimatterDefaultTools.ELECTRIC_WRENCH.addBehaviour(new BehaviourExtendedHighlight(b -> b instanceof BlockMachine || (b instanceof BlockPipe && b.builtInRegistryHolder().is(AntimatterDefaultTools.WRENCH.getToolType())), BehaviourExtendedHighlight.PIPE_FUNCTION));
        AntimatterDefaultTools.WIRE_CUTTER.addBehaviour(new BehaviourExtendedHighlight(b -> b instanceof BlockPipe && b.builtInRegistryHolder().is(AntimatterDefaultTools.WIRE_CUTTER.getToolType()), BehaviourExtendedHighlight.PIPE_FUNCTION));
        AntimatterDefaultTools.CROWBAR.addBehaviour(new BehaviourExtendedHighlight(b -> b instanceof BlockMachine || b instanceof BlockPipe, BehaviourExtendedHighlight.COVER_FUNCTION));
    }

}
