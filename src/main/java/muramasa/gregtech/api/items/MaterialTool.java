package muramasa.gregtech.api.items;

import com.google.common.collect.Sets;
import muramasa.gregtech.Ref;
import muramasa.gregtech.api.capability.GTCapabilities;
import muramasa.gregtech.api.capability.IConfigHandler;
import muramasa.gregtech.api.capability.ICoverHandler;
import muramasa.gregtech.api.data.Materials;
import muramasa.gregtech.api.enums.ToolType;
import muramasa.gregtech.api.materials.Material;
import muramasa.gregtech.api.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class MaterialTool extends ItemTool {

    protected ToolType type;
    protected Material primary, secondary;

    public MaterialTool(ToolType type, Material primary, Material secondary) {
        super(createToolMaterial(type, primary), Sets.newHashSet());
        setUnlocalizedName(type.getName() + "_" + primary.getName());
        setRegistryName(type.getName() + "_" + primary.getName());
        setCreativeTab(Ref.TAB_ITEMS);
        setMaxStackSize(1);
        this.type = type;
        this.primary = primary;
        this.secondary = secondary;
    }

    public ToolType getType() {
        return type;
    }

    public Material getPrimary() {
        return primary;
    }

    public Material getSecondary() {
        return secondary;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return primary.getDisplayName() + " " + type.getDisplayName();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltips, ITooltipFlag flagIn) {
        if (primary != null) {
            tooltips.add(TextFormatting.WHITE + primary.getDisplayName() + TextFormatting.YELLOW + "(" + primary.getToolQuality() + ")" + TextFormatting.WHITE + " / " + (secondary != null ? secondary.getDisplayName() : "NULL") + TextFormatting.YELLOW + "(" + secondary.getToolQuality() + ")");
        }
        if (type.isPowered()) {
            tooltips.add(TextFormatting.WHITE + "Energy: " + TextFormatting.AQUA + Utils.formatNumber(getEnergy(stack)) + " / " + Utils.formatNumber(getMaxEnergy(stack)));
        }
        tooltips.add(TextFormatting.WHITE + "Durability: " + TextFormatting.GREEN + (toolMaterial.getMaxUses() - stack.getItemDamage()) + " / " + toolMaterial.getMaxUses());
        tooltips.add(TextFormatting.WHITE + "Attack Speed: " + TextFormatting.BLUE + attackSpeed);
        tooltips.add(TextFormatting.WHITE + "Mining Speed: " + TextFormatting.LIGHT_PURPLE + toolMaterial.getEfficiency());
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
                if (type != null && configHandler.onInteract(player, hand, targetSide, type)) {
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
                if (type != null && coverHandler.onInteract(player, hand, targetSide, type)) {
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

    public long getEnergy(ItemStack stack) {
        return 1; //TODO implement item caps
    }

    public long getMaxEnergy(ItemStack stack) {
        return 100; //TODO implement item caps
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (primary == Materials.Aluminium) {
            items.add(new ItemStack(this));
        }
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        return itemStack;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        stack.damageItem(type.getDamageEntity(), attacker);
        return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
        if (!world.isRemote && (double)state.getBlockHardness(world, pos) != 0.0D) {
            stack.damageItem(type.getDamageMining(), entityLiving);
            type.getUseSound().play(world, pos);
        }
        return true;
    }

    @Override
    public int getHarvestLevel(ItemStack stack, String toolClass, @Nullable EntityPlayer player, @Nullable IBlockState blockState) {
        int level = super.getHarvestLevel(stack, toolClass,  player, blockState);
        if (level == -1 && getToolClasses(stack).contains(toolClass)) {
            if (type.isPowered() && getEnergy(stack) <= 0) return level;
            return this.toolMaterial.getHarvestLevel();
        } else {
            return level;
        }
    }

    @Override
    public Set<String> getToolClasses(ItemStack stack) {
        return type.getToolClass();
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(Ref.MODID + ":material_tool", "tool_type=" + type.getName()));
    }

    public static ToolMaterial createToolMaterial(ToolType type, Material mat) {
        return EnumHelper.addToolMaterial(mat.getName(), mat.getToolQuality(), 100 * (int)(mat.getToolDurability() * type.getDurabilityMulti()), mat.getToolSpeed(), type.getBaseDamage() + mat.getToolQuality(), 0);
    }
}
