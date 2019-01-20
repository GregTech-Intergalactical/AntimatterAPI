package muramasa.itech.api.items;

import com.google.common.collect.Multimap;
import muramasa.itech.ITech;
import muramasa.itech.api.capability.IConfigurable;
import muramasa.itech.api.capability.ITechCapabilities;
import muramasa.itech.api.enums.ToolType;
import muramasa.itech.api.materials.Materials;
import muramasa.itech.api.util.ToolHelper;
import muramasa.itech.api.util.Utils;
import muramasa.itech.client.creativetab.ITechTab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class MetaTool extends Item {

    public MetaTool() {
        setRegistryName("metatool");
        setUnlocalizedName(ITech.MODID + ".metatool");
        setCreativeTab(ITech.TAB_MATERIALS);
        setHasSubtypes(true);
        setMaxStackSize(1);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (tab instanceof ITechTab) {
            if (((ITechTab) tab).getTabName().equals("materials")) {
                for (int i = 0; i < ToolType.values().length; i++) {
                    items.add(new ItemStack(this, 1, i));
                }
            }
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        Materials material = ToolHelper.getPrimaryMaterial(stack);
        return (material != null ? material.getDisplayName() + " " + ToolType.values()[stack.getMetadata()].getDisplayName() : ToolType.values()[stack.getMetadata()].getDisplayName());
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltips, ITooltipFlag flagIn) {
        Materials primary = ToolHelper.getPrimaryMaterial(stack);
        Materials secondary = ToolHelper.getSecondaryMaterial(stack);
        if (primary != null) {
            tooltips.add(TextFormatting.WHITE + primary.getDisplayName() + TextFormatting.YELLOW + "(" + ToolHelper.getQuality(stack) + ")" + TextFormatting.WHITE + " / " + (secondary != null ? secondary.getDisplayName() : "NULL") + TextFormatting.YELLOW + "(" + ToolHelper.getQuality(stack) + ")");
        }
        if (ToolType.isPowered(stack)) {
            tooltips.add(TextFormatting.WHITE + "Energy: " + TextFormatting.AQUA + Utils.formatNumber(ToolHelper.getEnergy(stack)) + " / " + Utils.formatNumber(ToolHelper.getMaxEnergy(stack)));
        }
        tooltips.add(TextFormatting.WHITE + "Durability: " + TextFormatting.GREEN + Utils.formatNumber(ToolHelper.getDurability(stack)) + " / " + Utils.formatNumber(ToolHelper.getMaxDurability(stack)));
        tooltips.add(TextFormatting.WHITE + "Attack Speed: " + TextFormatting.BLUE + ToolHelper.getAttackSpeed(stack));
        tooltips.add(TextFormatting.WHITE + "Mining Speed: " + TextFormatting.LIGHT_PURPLE + ToolHelper.getMiningSpeed(stack));
        ToolType type = ToolType.get(stack);
        if (!type.getTooltip().equals("")) {
            tooltips.add(TextFormatting.GRAY + "" + TextFormatting.UNDERLINE + type.getTooltip());
        }
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        TileEntity tile = Utils.getTile(world, pos);
//        if (tile != null && tile.hasCapability(ITechCapabilities.COVERABLE, facing)) {
//            System.out.println("COVERABLE");
//            ICoverable coverHandler = tile.getCapability(ITechCapabilities.COVERABLE, null);
//            if (coverHandler != null) {
//                EnumFacing targetSide = Utils.determineInteractionSide(facing, hitX, hitY, hitZ);
//                if (ToolType.WRENCH.isItemEqual(stack)) {
//
//                } else if (ToolType.CROWBAR.isItemEqual(stack)) {
//
//                }
//            }
//        }
        if (tile != null && tile.hasCapability(ITechCapabilities.CONFIGURABLE, facing)) {
            EnumFacing targetSide = Utils.determineInteractionSide(facing, hitX, hitY, hitZ);
            IConfigurable configHandler = tile.getCapability(ITechCapabilities.CONFIGURABLE, targetSide);
            if (configHandler != null) {
                if (ToolType.WRENCH.isItemEqual(stack)) {
                    configHandler.onWrench(targetSide);
                    return EnumActionResult.SUCCESS;
                } else if (ToolType.CROWBAR.isItemEqual(stack)) {
                    configHandler.onCrowbar(targetSide);
                    return EnumActionResult.SUCCESS;
                }
            }
        }
        return EnumActionResult.PASS;
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return true;
    }

    @Override
    public int getHarvestLevel(ItemStack stack, String toolClass, @Nullable EntityPlayer player, @Nullable IBlockState blockState) {
        return ToolHelper.getQuality(stack);
    }

    @Override
    public boolean canHarvestBlock(IBlockState blockIn) {
        System.out.println("harvest");
        return super.canHarvestBlock(blockIn);
    }

    @Override
    public boolean canHarvestBlock(IBlockState state, ItemStack stack) {
//        System.out.println("ad[oadk[apkwd");
//        String tool = state.getBlock().getHarvestTool(state);
//        System.out.println("TYPE: " + tool);
//        if (ToolType.PICKAXE.isItemEqual(stack)) {
//            return tool.equals("pickaxe");
//        } else if (ToolType.SHOVEL.isItemEqual(stack)) {
//            return tool.equals("shovel");
//        }
        return false;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state) {
        if (ToolType.isPowered(stack) && ToolHelper.getDurability(stack) > 0 && ToolHelper.getEnergy(stack) > 0) {
            return ToolHelper.getMiningSpeed(stack);
        } else if (ToolHelper.getDurability(stack) > 0) {
            return ToolHelper.getMiningSpeed(stack);
        }
        return 0f;
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(slot);
        if (slot == EntityEquipmentSlot.MAINHAND) {
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", (double) ToolHelper.getAttackDamage(stack), 0));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", (double) ToolHelper.getAttackSpeed(stack), 0));
        }
        return multimap;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        ToolHelper.damageForEntity(stack, 1);
        return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
        ToolHelper.damageForMining(stack, 1);
        ToolType.playDigSound(world, pos, stack);
        if (ToolHelper.getDurability(stack) <= 0) {
            ToolHelper.remove(stack, world, pos);
        }
        return super.onBlockDestroyed(stack, world, state, pos, entityLiving);
    }

//    @Override
//    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
////        if (ToolType.SWORD.isItemEqual(player.getHeldItem(hand))) {
//            player.setActiveHand(hand);
//            return new ActionResult<>(EnumActionResult.PASS, player.getHeldItem(hand));
////        }
//    }
//
    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        if (ToolType.DRILL.isItemEqual(stack)) {
            return EnumAction.BOW;
        } else if (ToolType.SWORD.isItemEqual(stack)) {
            return EnumAction.BLOCK;
        }
        return EnumAction.NONE;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false; //TODO compare NBT
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1 - ((float) ToolHelper.getDurability(stack) / (float) ToolHelper.getMaxDurability(stack));
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return ToolHelper.getDurability(stack) < ToolHelper.getMaxDurability(stack);
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return MathHelper.hsvToRGB(Math.max(0.0F, (float) (1.0F - getDurabilityForDisplay(stack))) / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
        ToolHelper.validateTag(stack);
    }

    @Override
    public boolean isFull3D() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        for (int i = 0; i < ToolType.values().length; i++) {
            ModelLoader.setCustomModelResourceLocation(this, i, new ModelResourceLocation(ITech.MODID + ":metatool", "type=" + i));
        }
    }

    public static class ColorHandler implements IItemColor {
        @Override
        public int colorMultiplier(ItemStack stack, int tintIndex) {
            Materials primaryMaterial = ToolHelper.getPrimaryMaterial(stack);
            Materials secondaryMaterial = ToolHelper.getSecondaryMaterial(stack);
            if (primaryMaterial != null && secondaryMaterial != null) {
                if (ToolType.PLUNGER.isItemEqual(stack)) {
                    return tintIndex == 0 ? -1 : secondaryMaterial.getRGB();
                }
                if (ToolType.DRILL.isItemEqual(stack)) {
                    return tintIndex == 0 ? primaryMaterial.getRGB() : secondaryMaterial.getRGB();
                }
                return tintIndex == 0 ? primaryMaterial.getRGB() : secondaryMaterial.getRGB();
            }
            return 0xffffff;
        }
    }
}
