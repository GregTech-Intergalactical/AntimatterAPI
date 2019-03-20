package muramasa.gregtech.api.items;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import muramasa.gregtech.Ref;
import muramasa.gregtech.api.capability.GTCapabilities;
import muramasa.gregtech.api.capability.IConfigHandler;
import muramasa.gregtech.api.capability.ICoverHandler;
import muramasa.gregtech.api.data.Materials;
import muramasa.gregtech.api.enums.ToolType;
import muramasa.gregtech.api.materials.ItemFlag;
import muramasa.gregtech.api.materials.Material;
import muramasa.gregtech.api.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
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
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class MaterialTool extends ItemTool {

    protected ToolType type;

    public MaterialTool(ToolType type) {
        super(ToolMaterial.WOOD, Sets.newHashSet());
        setUnlocalizedName(type.getName());
        setRegistryName(type.getName());
        setCreativeTab(Ref.TAB_ITEMS);
        setMaxDamage(1);
        setMaxStackSize(1);
        this.type = type;
    }

    public ToolType getType() {
        return type;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        Material mat = getPrimary(stack);
        return (mat != null ? mat.getDisplayName() : "NULL") + " " + type.getDisplayName();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltips, ITooltipFlag flagIn) {
        Material primary = getPrimary(stack), secondary = getSecondary(stack);
        if (primary != null) {
            tooltips.add(TextFormatting.WHITE + primary.getDisplayName() + TextFormatting.YELLOW + "(" + getPrimaryQuality(stack) + ")" + TextFormatting.WHITE + " / " + (secondary != null ? secondary.getDisplayName() : "NULL") + TextFormatting.YELLOW + "(" + getSecondaryQuality(stack) + ")");
        }
        if (type.isPowered()) {
            tooltips.add(TextFormatting.WHITE + "Energy: " + TextFormatting.AQUA + Utils.formatNumber(getEnergy(stack)) + " / " + Utils.formatNumber(getMaxEnergy(stack)));
        }
        tooltips.add(TextFormatting.WHITE + "Durability: " + TextFormatting.GREEN + getDurability(stack) + " / " + getMaxDurability(stack));
        tooltips.add(TextFormatting.WHITE + "Attack Speed: " + TextFormatting.BLUE + attackSpeed);
        if (primary != null) {
            tooltips.add(TextFormatting.WHITE + "Mining Speed: " + TextFormatting.LIGHT_PURPLE + primary.getToolSpeed());
        }
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
                    damage(stack, type.getDamageCrafting(), player);
                    type.playUseSound(world, pos);
                    result = EnumActionResult.SUCCESS;
                }
            }
        }
        if (tile.hasCapability(GTCapabilities.COVERABLE, facing)) {
            EnumFacing targetSide = Utils.getInteractSide(facing, hitX, hitY, hitZ);
            ICoverHandler coverHandler = tile.getCapability(GTCapabilities.COVERABLE, targetSide);
            if (coverHandler != null) {
                ToolType type = ToolType.get(stack);
                if (type != null && coverHandler.onInteract(player, hand, targetSide, type)) {
                    damage(stack, type.getDamageCrafting(), player);
                    type.playUseSound(world, pos);
                    result = EnumActionResult.SUCCESS;
                }
            }
        }
        return result;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        items.add(get(Materials.Cobalt, Materials.Wood));
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
    public float getDestroySpeed(ItemStack stack, IBlockState state) {
        for (String type : getToolClasses(stack)) {
            if (state.getBlock().isToolEffective(type, state)) {
                return getMiningSpeed(stack);
            }
        }
        return 1.0F;
//        return this.EFFECTIVE_ON.contains(state.getBlock()) ? getPrimary().getToolSpeed() : 1.0F;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        damage(stack, type.getDamageEntity(), attacker);
        return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
        if (!world.isRemote && (double)state.getBlockHardness(world, pos) != 0.0D) {
            damage(stack, type.getDamageMining(), entityLiving);
            type.playUseSound(world, pos);
        }
        return true;
    }

    @Override
    public int getItemEnchantability() {
        return 0;
    }

    @Override
    public String getToolMaterialName() {
        return "gt_material";
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        Material primary = getPrimary(toRepair);
        if (primary != null) {
            ItemStack mat = primary.has(ItemFlag.BGEM) ? primary.getGem(1) : primary.getIngot(1);
            if (!mat.isEmpty() && OreDictionary.itemMatches(mat, repair, false)) return true;
        }
        return super.getIsRepairable(toRepair, repair);
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(slot);
        if (slot == EntityEquipmentSlot.MAINHAND) {
            Material mat = getPrimary(stack);
            float damage = mat != null ? (type.getBaseDamage() + getPrimaryQuality(stack)) : 1.0f;
            int speed = mat != null ? /*TODO*/3 : 3;
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", damage, 0));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", speed, 0));
        }
        return multimap;
    }

    @Override
    public int getHarvestLevel(ItemStack stack, String toolClass, @Nullable EntityPlayer player, @Nullable IBlockState blockState) {
        int level = super.getHarvestLevel(stack, toolClass,  player, blockState);
        if (level == -1 && getToolClasses(stack).contains(toolClass)) {
            if (type.isPowered() && getEnergy(stack) <= 0) return level;
            Material mat = getPrimary(stack);
            if (mat == null) return level;
            return getPrimaryQuality(stack);
        } else {
            return level;
        }
    }

    @Override
    public Set<String> getToolClasses(ItemStack stack) {
        return type.getToolClass();
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1 - ((float) getDurability(stack) / (float) getMaxDurability(stack));
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return getDurability(stack) < getMaxDurability(stack);
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return MathHelper.hsvToRGB(Math.max(0.0F, (float) (1.0F - getDurabilityForDisplay(stack))) / 3.0F, 1.0F, 1.0F);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(Ref.MODID + ":material_tool", "tool_type=" + type.getName()));
    }

    public ItemStack get(Material primary, Material secondary) {
        ItemStack stack = new ItemStack(this);
        validateTag(stack);
        getTag(stack).setString(Ref.KEY_TOOL_DATA_PRIMARY_MAT, primary != null ? primary.getName() : "NULL");
        getTag(stack).setString(Ref.KEY_TOOL_DATA_SECONDARY_MAT, secondary != null ? secondary.getName() : "NULL");
        getTag(stack).setInteger(Ref.KEY_TOOL_DATA_DURABILITY, getMaxDurability(stack));
        return stack;
    }

    public void damage(ItemStack stack, int damage, EntityLivingBase living) {
        int newDamage = getDurability(stack) - damage;
        if (newDamage > 0) {
            getTag(stack).setInteger(Ref.KEY_TOOL_DATA_DURABILITY, newDamage);
        } else {
            stack.damageItem(2, living);
        }
    }

    public Material getPrimary(ItemStack stack) {
        return Materials.get(getTag(stack).getString(Ref.KEY_TOOL_DATA_PRIMARY_MAT));
    }

    public Material getSecondary(ItemStack stack) {
        return Materials.get(getTag(stack).getString(Ref.KEY_TOOL_DATA_SECONDARY_MAT));
    }

    public int getPrimaryQuality(ItemStack stack) {
        Material mat = getPrimary(stack);
        return mat != null ? type.getBaseQuality() + mat.getToolQuality() : 1;
    }

    public int getSecondaryQuality(ItemStack stack) {
        Material mat = getSecondary(stack);
        return mat != null ? type.getBaseQuality() + mat.getToolQuality() : 1;
    }

    public int getDurability(ItemStack stack) {
        return getTag(stack).getInteger(Ref.KEY_TOOL_DATA_DURABILITY);
    }

    public int getMaxDurability(ItemStack stack) {
        Material mat = getPrimary(stack);
        return mat != null ? 100 * (int)(mat.getToolDurability() * type.getDurabilityMulti()) : 1;
    }

    public float getMiningSpeed(ItemStack stack) {
        Material mat = getPrimary(stack);
        return mat != null ? type.getSpeedMulti() * mat.getToolSpeed() : 1.0f;
    }

    public long getEnergy(ItemStack stack) {
        return getTag(stack).getLong(Ref.KEY_TOOL_DATA_ENERGY);
    }

    public long getMaxEnergy(ItemStack stack) {
        return getTag(stack).getLong(Ref.KEY_TOOL_DATA_MAX_ENERGY);
    }

    public NBTTagCompound getTag(ItemStack stack) {
        if (!stack.hasTagCompound()) validateTag(stack);
        return (NBTTagCompound) stack.getTagCompound().getTag(Ref.TAG_TOOL_DATA);
    }

    public void validateTag(ItemStack stack) {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        if (!stack.getTagCompound().hasKey(Ref.TAG_TOOL_DATA)) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setString(Ref.KEY_TOOL_DATA_PRIMARY_MAT, "NULL");
            compound.setString(Ref.KEY_TOOL_DATA_SECONDARY_MAT, "NULL");
            compound.setInteger(Ref.KEY_TOOL_DATA_DURABILITY, 0);
            compound.setLong(Ref.KEY_TOOL_DATA_ENERGY, 0);
            compound.setLong(Ref.KEY_TOOL_DATA_MAX_ENERGY, 0);
            stack.getTagCompound().setTag(Ref.TAG_TOOL_DATA, compound);
        }
    }
}
