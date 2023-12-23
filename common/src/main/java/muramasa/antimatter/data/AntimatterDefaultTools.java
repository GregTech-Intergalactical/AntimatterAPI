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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import static muramasa.antimatter.data.AntimatterMaterialTypes.*;
import static net.minecraft.world.level.material.Material.*;

public class AntimatterDefaultTools {
    public static final AntimatterToolType SWORD = AntimatterAPI.register(AntimatterToolType.class, new AntimatterToolType(Ref.ID, "sword", 2, 1, 10, 3.0F, -2.4F, false)).setToolClass(MaterialSword.class).addEffectiveBlocks(Blocks.COBWEB).setHasContainer(false).setMaterialTypeItem(SWORD_BLADE);
    public static final AntimatterToolType PICKAXE = AntimatterAPI.register(AntimatterToolType.class, new AntimatterToolType(Ref.ID, "pickaxe", 1, 2, 10, 1.0F, -2.8F, true)).addEffectiveMaterials(ICE_SOLID, PISTON).setHasContainer(false).setMaterialTypeItem(PICKAXE_HEAD);
    public static final AntimatterToolType SHOVEL = AntimatterAPI.register(AntimatterToolType.class, new AntimatterToolType(Ref.ID, "shovel", 1, 2, 10, 1.5F, -3.0F, true)).addEffectiveMaterials(CLAY, SAND, TOP_SNOW, SNOW, DIRT).setHasContainer(false).setMaterialTypeItem(SHOVEL_HEAD);
    public static final AntimatterToolType AXE = AntimatterAPI.register(AntimatterToolType.class, new AntimatterToolType(Ref.ID, "axe", 1, 1, 10, 6.0F, -3.0F, true)).addEffectiveMaterials(PLANT, REPLACEABLE_PLANT, BAMBOO).setHasContainer(false).setMaterialTypeItem(AXE_HEAD);
    public static final AntimatterToolType HOE = AntimatterAPI.register(AntimatterToolType.class, new AntimatterToolType(Ref.ID, "hoe", 1, 2, 10, -2.0F, -1.0F, true)).setHasContainer(false).setMaterialTypeItem(HOE_HEAD);
    public static final AntimatterToolType HAMMER = AntimatterAPI.register(AntimatterToolType.class, new AntimatterToolType(Ref.ID, "hammer", 1, 2, 2, 3.0F, -3.0F, false)).addTags("pickaxe").addEffectiveMaterials(net.minecraft.world.level.material.Material.METAL, STONE).setUseSound(SoundEvents.ANVIL_PLACE).setRepairable(false).setMaterialTypeItem(HAMMER_HEAD);
    public static final AntimatterToolType WRENCH = AntimatterAPI.register(AntimatterToolType.class, new AntimatterToolType(Ref.ID, "wrench", 1, 2, 2, 1.5F, -2.8F, false)).setUseSound(Ref.WRENCH).addEffectiveBlocks(Blocks.HOPPER).setHasSecondary(false).setOverlayLayers(0).setRepairable(false).addBlacklistedEnchantments(Enchantments.BLOCK_EFFICIENCY);
    public static final AntimatterToolType WRENCH_ALT = AntimatterAPI.register(AntimatterToolType.class, new AntimatterToolType(Ref.ID, "wrench_alt", 1, 2, 2, 1.5F, -2.8F, false)).setUseSound(Ref.WRENCH).addEffectiveBlocks(Blocks.HOPPER).addTags("wrench").setHasSecondary(false).setOverlayLayers(0).setRepairable(false).addBlacklistedEnchantments(Enchantments.BLOCK_EFFICIENCY).setCustomName("Wrench (Alt)");
    public static final AntimatterToolType SAW = AntimatterAPI.register(AntimatterToolType.class, new AntimatterToolType(Ref.ID, "saw", 1, 2, 2, 2.0F, -2.8F, false)).addEffectiveBlocks(Blocks.ICE, Blocks.PACKED_ICE, Blocks.BLUE_ICE).setRepairable(false).setMaterialTypeItem(SAW_BLADE);
     public static final AntimatterToolType FILE = AntimatterAPI.register(AntimatterToolType.class, new AntimatterToolType(Ref.ID, "file", 1, 2, 2, -2.0F, -2.4F, false)).setRepairable(false).setMaterialTypeItem(FILE_HEAD);
    public static final AntimatterToolType CROWBAR = AntimatterAPI.register(AntimatterToolType.class, new AntimatterToolType(Ref.ID, "crowbar", 1, 10, 5, 1.0F, -2.0F, false)).setUseSound(SoundEvents.ITEM_BREAK).setHasSecondary(false).setRepairable(false);
    public static final AntimatterToolType SOFT_HAMMER = AntimatterAPI.register(AntimatterToolType.class, new AntimatterToolType(Ref.ID, "soft_hammer", 1, 2, 2, 1.0F, -3.0F, false)).setRepairable(false).setPrimaryRequirement(MaterialTags.RUBBERTOOLS);//.setUseSound();
    public static final AntimatterToolType SCREWDRIVER = AntimatterAPI.register(AntimatterToolType.class, new AntimatterToolType(Ref.ID, "screwdriver", 1, 2, 2, 0.0F, -1.0F, false)).setUseSound(Ref.WRENCH).setRepairable(false).setMaterialTypeItem(SCREWDRIVER_TIP);
    public static final AntimatterToolType MORTAR = AntimatterAPI.register(AntimatterToolType.class, new AntimatterToolType(Ref.ID, "mortar", 1, 5, 2, -2.0F, 0.0F, false)).setUseSound(SoundEvents.GRINDSTONE_USE).setBlockBreakability(false).setRepairable(false);
    public static final AntimatterToolType WIRE_CUTTER = AntimatterAPI.register(AntimatterToolType.class, new AntimatterToolType(Ref.ID, "wire_cutter", 1, 3, 2, 0.0F, -1.5F, false)).setUseSound(SoundEvents.SHEEP_SHEAR).addEffectiveMaterials(WOOL, SPONGE, WEB, CLOTH_DECORATION).setRepairable(false).addBlacklistedEnchantments(Enchantments.BLOCK_EFFICIENCY);
    public static final AntimatterToolType BRANCH_CUTTER = AntimatterAPI.register(AntimatterToolType.class, new AntimatterToolType(Ref.ID, "branch_cutter", 1, 3, 2, 0.0F, -1.5F, false)).addTags("grafter").addEffectiveMaterials(LEAVES).setHasContainer(false).setDurabilityMultiplier(0.25f);
    public static final AntimatterToolType KNIFE = AntimatterAPI.register(AntimatterToolType.class, new AntimatterToolType(Ref.ID, "knife", 1, 2, 1, 2.1F, -2.0F, false)).addEffectiveBlocks(Blocks.COBWEB).setRepairable(false).setTag(new ResourceLocation(Ref.ID, "knives")).setOriginalTag(true);
    public static final AntimatterToolType SCISSORS = AntimatterAPI.register(AntimatterToolType.class, new AntimatterToolType(Ref.ID, "scissors", 1, 2, 2, 1.0f, -1.5f, false));
    public static final AntimatterToolType PLUNGER = AntimatterAPI.register(AntimatterToolType.class, new AntimatterToolType(Ref.ID, "plunger", 5, 5, 10, 0.0F, -2.9F, false)).setUseSound(SoundEvents.BUCKET_EMPTY).setHasSecondary(false).setRepairable(false);
    public static final AntimatterToolType SCYTHE = AntimatterAPI.register(AntimatterToolType.class, new AntimatterToolType(Ref.ID, "scythe", 1, 2, 5, 6.5f, -3.1f, false)).setMaterialTypeItem(SCYTHE_HEAD);
    public static final AntimatterArmorType HELMET = new AntimatterArmorType(Ref.ID, "helmet", 40, 0, 0.0F, 0.0F, EquipmentSlot.HEAD);
    public static final AntimatterArmorType CHESTPLATE = new AntimatterArmorType(Ref.ID, "chestplate", 40, 0, 0.0F, 0.0F, EquipmentSlot.CHEST);
    public static final AntimatterArmorType LEGGINGS = new AntimatterArmorType(Ref.ID, "leggings", 40, 0, 0.0F, 0.0F, EquipmentSlot.LEGS);
    public static final AntimatterArmorType BOOTS = new AntimatterArmorType(Ref.ID, "boots", 40, 0, 0.0F, 0.0F, EquipmentSlot.FEET);

    public static void init(Side side){
        AXE.addBehaviour(BehaviourLogStripping.INSTANCE, BehaviourTreeFelling.INSTANCE);
        PICKAXE.addBehaviour(BehaviourTorchPlacing.INSTANCE);
        PLUNGER.addBehaviour(BehaviourWaterlogToggle.INSTANCE);
        WRENCH.addBehaviour(BehaviourWrenchSwitching.INSTANCE);
        WRENCH_ALT.addBehaviour(BehaviourWrenchSwitching.INSTANCE);
        KNIFE.addBehaviour(BehaviourPumpkinCarving.INSTANCE);
        SCISSORS.addBehaviour(BehaviourShearing.INSTANCE);
        SCYTHE.addBehaviour(BehaviourCropHarvesting.INSTANCE);
        if (side == Side.CLIENT) {
            clientInit();
        }
    }

    public static void postInit(){
        for (AntimatterToolType type : AntimatterAPI.all(AntimatterToolType.class)) {
            if (type.getToolTypes().contains(BlockTags.MINEABLE_WITH_SHOVEL)) type.addBehaviour(BehaviourVanillaShovel.INSTANCE);
            if (type.getToolTypes().contains(BlockTags.MINEABLE_WITH_HOE)) type.addBehaviour(BehaviourBlockTilling.INSTANCE);
            if (type.isPowered()) type.addBehaviour(BehaviourPoweredDebug.INSTANCE);
        }
    }
    private static void clientInit() {
        AntimatterDefaultTools.WRENCH.addBehaviour(new BehaviourExtendedHighlight(b -> b instanceof BlockMachine || (b instanceof BlockPipe && b.builtInRegistryHolder().is(AntimatterDefaultTools.WRENCH.getToolType())) || b.defaultBlockState().hasProperty(BlockStateProperties.FACING_HOPPER) || b.defaultBlockState().hasProperty(BlockStateProperties.HORIZONTAL_FACING), BehaviourExtendedHighlight.PIPE_FUNCTION));
        AntimatterDefaultTools.SCREWDRIVER.addBehaviour(new BehaviourExtendedHighlight(b -> b instanceof BlockMachine || b instanceof BlockPipe, BehaviourExtendedHighlight.COVER_FUNCTION));
        AntimatterDefaultTools.WIRE_CUTTER.addBehaviour(new BehaviourExtendedHighlight(b -> b instanceof BlockPipe && b.builtInRegistryHolder().is(AntimatterDefaultTools.WIRE_CUTTER.getToolType()), BehaviourExtendedHighlight.PIPE_FUNCTION));
        AntimatterDefaultTools.CROWBAR.addBehaviour(new BehaviourExtendedHighlight(b -> b instanceof BlockMachine || b instanceof BlockPipe, BehaviourExtendedHighlight.COVER_FUNCTION));
    }

}
