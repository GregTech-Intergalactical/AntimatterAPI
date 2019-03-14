package muramasa.gregtech.api.items;

import com.google.common.collect.Multimap;
import muramasa.gregtech.Ref;
import muramasa.gregtech.api.capability.GTCapabilities;
import muramasa.gregtech.api.capability.IConfigHandler;
import muramasa.gregtech.api.capability.ICoverHandler;
import muramasa.gregtech.api.enums.ToolType;
import muramasa.gregtech.api.materials.Material;
import muramasa.gregtech.api.util.ToolHelper;
import muramasa.gregtech.api.util.Utils;
import muramasa.gregtech.client.creativetab.GregTechTab;
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
import net.minecraft.init.Items;
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

    //TODO flatten

    public MetaTool() {
        setRegistryName("meta_tool");
        setUnlocalizedName(Ref.MODID + ".meta_tool");
        setCreativeTab(Ref.TAB_ITEMS);
        setHasSubtypes(true);
        setMaxStackSize(1);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (tab instanceof GregTechTab) {
            if (((GregTechTab) tab).getTabName().equals("items")) {
                for (int i = 0; i < ToolType.values().length; i++) {
                    items.add(new ItemStack(this, 1, i));
                }
            }
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        Material material = ToolHelper.getPrimaryMaterial(stack);
        return (material != null ? material.getDisplayName() + " " + ToolType.values()[stack.getMetadata()].getDisplayName() : ToolType.values()[stack.getMetadata()].getDisplayName());
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltips, ITooltipFlag flagIn) {
        Material primary = ToolHelper.getPrimaryMaterial(stack);
        Material secondary = ToolHelper.getSecondaryMaterial(stack);
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
        if (tile == null) return EnumActionResult.PASS;
        EnumActionResult result = EnumActionResult.PASS;
        if (tile.hasCapability(GTCapabilities.CONFIGURABLE, facing)) {
            EnumFacing targetSide = Utils.getInteractSide(facing, hitX, hitY, hitZ);
            IConfigHandler configHandler = tile.getCapability(GTCapabilities.CONFIGURABLE, targetSide);
            if (configHandler != null) {
                ToolType type = ToolType.get(stack);
                if (type != null && configHandler.onInteract(targetSide, type)) {
                    if (type.getUseSound() != null) {
                        type.getUseSound().play(world, pos);
                        result = EnumActionResult.SUCCESS;
                    } else {
                        result = EnumActionResult.SUCCESS;
                    }
                }
            }
        }
        if (tile.hasCapability(GTCapabilities.COVERABLE, facing)) {
            EnumFacing targetSide = Utils.getInteractSide(facing, hitX, hitY, hitZ);
            ICoverHandler coverHandler = tile.getCapability(GTCapabilities.COVERABLE, targetSide);
            if (coverHandler != null) {
                ToolType type = ToolType.get(stack);
                if (type != null && coverHandler.onInteract(targetSide, type)) {
                    if (type.getUseSound() != null) {
                        type.getUseSound().play(world, pos);
                        result = EnumActionResult.SUCCESS;
                    } else {
                        result = EnumActionResult.SUCCESS;
                    }
                }
            }
        }
        return result;
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
        System.out.println("harvest");
//        System.out.println("ad[oadk[apkwd");
//        String tool = state.getBlock().getHarvestTool(state);
//        System.out.println("TYPE: " + tool);
//        if (ToolType.PICKAXE.isEqual(stack)) {
//            return tool.equals("pickaxe");
//        } else if (ToolType.SHOVEL.isEqual(stack)) {
//            return tool.equals("shovel");
//        }
        return super.canHarvestBlock(state, stack);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state) {
//        if (ToolType.isPowered(stack) && ToolHelper.getDurability(stack) > 0 && ToolHelper.getEnergy(stack) > 0) {
//            return ToolHelper.getMiningSpeed(stack);
//        } else if (ToolHelper.getDurability(stack) > 0) {
//            return ToolHelper.getMiningSpeed(stack);
//        }
        return Items.DIAMOND_SWORD.getDestroySpeed(stack, state);
//        return 1f;
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
        if (slot == EntityEquipmentSlot.MAINHAND) {
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", (double) ToolHelper.getAttackDamage(stack), 0));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", (double) ToolHelper.getAttackSpeed(stack), 0));
        }
        return multimap;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
//        ToolHelper.damageForEntity(stack, 1);
//        return true;
        return super.onLeftClickEntity(stack, player, entity);
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
//        ToolHelper.damageForMining(stack, 1);
//        ToolType.playDigSound(world, pos, stack);
//        if (ToolHelper.getDurability(stack) <= 0) {
//            ToolHelper.remove(stack, world, pos);
//        }
        return super.onBlockDestroyed(stack, world, state, pos, entityLiving);
    }

//    @Override
//    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
////        if (ToolType.SWORD.isEqual(player.getHeldItem(hand))) {
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
            ModelLoader.setCustomModelResourceLocation(this, i, new ModelResourceLocation(getRegistryName(), "tool_type=" + ToolType.values()[i].getName()));
        }
    }

    public static class ColorHandler implements IItemColor {
        @Override
        public int colorMultiplier(ItemStack stack, int tintIndex) {
            Material primaryMaterial = ToolHelper.getPrimaryMaterial(stack);
            Material secondaryMaterial = ToolHelper.getSecondaryMaterial(stack);
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
