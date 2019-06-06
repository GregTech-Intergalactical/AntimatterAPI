package muramasa.gtu.api.items;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import muramasa.gtu.GregTech;
import muramasa.gtu.Ref;
import muramasa.gtu.api.capability.GTCapabilities;
import muramasa.gtu.api.capability.IConfigHandler;
import muramasa.gtu.api.capability.ICoverHandler;
import muramasa.gtu.api.data.Materials;
import muramasa.gtu.api.materials.GenerationFlag;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.registration.IColorHandler;
import muramasa.gtu.api.registration.IModelOverride;
import muramasa.gtu.api.tools.ToolType;
import muramasa.gtu.api.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
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
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class MaterialTool extends ItemSword implements IModelOverride, IColorHandler {

    protected ToolType type;

    public MaterialTool(ToolType type) {
        super(ToolMaterial.WOOD);
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
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (type.isPowered()) {
            items.add(get(Materials.Cobalt, Materials.TungstenSteel, 1600000));
        } else {
            items.add(get(Materials.Cobalt, Materials.Wood));
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        Material mat = getPrimary(stack);
        return (mat != null ? mat.getDisplayName() : "NULL") + " " + type.getDisplayName();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltips, ITooltipFlag flagIn) {
        Material primary = getPrimary(stack), secondary = getSecondary(stack);
        if (primary != null) tooltips.add(TextFormatting.WHITE + primary.getDisplayName() + TextFormatting.YELLOW + "(" + getPrimaryQuality(stack) + ")" + TextFormatting.WHITE + " / " + (secondary != null ? secondary.getDisplayName() : "NULL") + TextFormatting.YELLOW + "(" + getSecondaryQuality(stack) + ")");
        if (type.isPowered()) tooltips.add(TextFormatting.WHITE + "Energy: " + TextFormatting.AQUA + Utils.formatNumber(getEnergy(stack)) + " / " + Utils.formatNumber(getMaxEnergy(stack)));
        tooltips.add(TextFormatting.WHITE + "Durability: " + TextFormatting.GREEN + getDurability(stack) + " / " + getMaxDurability(stack));
        tooltips.add(TextFormatting.WHITE + "Attack Damage: " + TextFormatting.BLUE + getAttackDamage(stack));
        tooltips.add(TextFormatting.WHITE + "Attack Speed: " + TextFormatting.BLUE + getAttackSpeed(stack));
        if (primary != null) tooltips.add(TextFormatting.WHITE + "Mining Speed: " + TextFormatting.LIGHT_PURPLE + getMiningSpeed(stack));
        if (!type.getTooltip().equals("")) tooltips.add(TextFormatting.GRAY + "" + TextFormatting.UNDERLINE + type.getTooltip());
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);

        if (world.getBlockState(pos) == Blocks.REDSTONE_BLOCK.getDefaultState()) {
            if (type.isPowered()) {
                getTag(stack).setLong(Ref.KEY_TOOL_DATA_ENERGY, getTag(stack).getLong(Ref.KEY_TOOL_DATA_ENERGY) + 50000);
            }
        }

        TileEntity tile = Utils.getTile(world, pos);
        if (tile == null) return EnumActionResult.PASS;
        EnumActionResult result = EnumActionResult.PASS;
        if (tile.hasCapability(GTCapabilities.CONFIGURABLE, facing)) {
            EnumFacing targetSide = Utils.getInteractSide(facing, hitX, hitY, hitZ);
            IConfigHandler configHandler = tile.getCapability(GTCapabilities.CONFIGURABLE, targetSide);
            if (configHandler != null) {
                if (type != null && configHandler.onInteract(player, hand, targetSide, type)) {
                    damage(stack, type.getDamageCrafting(), player, true);
                    result = EnumActionResult.SUCCESS;
                }
            }
        }
        if (tile.hasCapability(GTCapabilities.COVERABLE, facing)) {
            EnumFacing targetSide = Utils.getInteractSide(facing, hitX, hitY, hitZ);
            ICoverHandler coverHandler = tile.getCapability(GTCapabilities.COVERABLE, targetSide);
            if (coverHandler != null) {
                if (type != null && coverHandler.onInteract(player, hand, targetSide, type)) {
                    damage(stack, type.getDamageCrafting(), player, true);
                    result = EnumActionResult.SUCCESS;
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
    public ItemStack getContainerItem(ItemStack stack) {
        if (type.getUseSound() != null) GregTech.PROXY.playSound(type.getUseSound());
    	return damage(stack, getType().getDamageCrafting());
    }

    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state) {
        for (String clazz : getToolClasses(stack)) {
            if (state.getBlock().isToolEffective(clazz, state)) {
                if (!canMine(stack)) return 1.0F;
                return getMiningSpeed(stack);
            }
        }
        return 1.0F;
//        return this.EFFECTIVE_ON.contains(state.getBlock()) ? getPrimary().getToolSpeed() : 1.0F;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        damage(stack, type.getDamageEntity(), attacker, true);
        return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
        if (!world.isRemote && (double)state.getBlockHardness(world, pos) != 0.0D) {
            damage(stack, type.getDamageMining(), entityLiving, true);
        }
        return true;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }
    
    //TODO: Correspond Item Enchantability to Material mining level
    @Override
    public int getItemEnchantability() {
        return 10;
    }


    @Override
    public String getToolMaterialName() {
        return "gt_material";
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        Material primary = getPrimary(toRepair);
        if (primary != null) {
            ItemStack mat = primary.has(GenerationFlag.BASIC_GEM) ? primary.getGem(1) : primary.getIngot(1);
            if (!mat.isEmpty() && OreDictionary.itemMatches(mat, repair, false)) return true;
        }
        return super.getIsRepairable(toRepair, repair);
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot equipmentSlot, ItemStack stack) {
        Multimap<String, AttributeModifier> multimap = HashMultimap.create();
        if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double) getAttackDamage(stack), 0));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", (double) getAttackSpeed(stack), 0));
        }
        return multimap;
    }

    @Override
    public int getHarvestLevel(ItemStack stack, String toolClass, @Nullable EntityPlayer player, @Nullable IBlockState blockState) {
        int level = super.getHarvestLevel(stack, toolClass,  player, blockState);
        if (level == -1 && getToolClasses(stack).contains(toolClass)) {
            if (!canMine(stack)) return level;
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

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos originPos, EntityPlayer player) {
        //TODO move this to onBlockDestroyed?
        if (getDurability(stack) < type.getDamageMining()) return false;
        if (player.capabilities.isCreativeMode) return false; //TODO temp?
        for (BlockPos pos : getAOEBlocks(stack, player.world, player, originPos)) {
            if (!ForgeHooks.canHarvestBlock(player.world.getBlockState(pos).getBlock(), player, player.world, pos)) continue;
            Utils.breakBlock(stack, player.world, player.world.getBlockState(pos), pos, player);
            if (damage(stack, type.getDamageMining(), player, false) <= 0) {
                System.out.println("broke tool early");
                break;
            }
        }
        return false;
    }

    /** Helper Methods **/
    public Set<BlockPos> getAOEBlocks(ItemStack stack, World world, EntityPlayer player, BlockPos origin) {
        return ImmutableSet.of();
    }

    public boolean canMine(ItemStack stack) {
        boolean can = getDurability(stack) > 0;
        if (type.isPowered() && getEnergy(stack) <= 0) can = false;
        return can;
    }

    public ItemStack get(Material primary, Material secondary) {
        ItemStack stack = new ItemStack(this);
        if (primary != null && !primary.getEnchantments().isEmpty()) {
        	//Added check to stop pickaxes getting looting, swords getting fortune that sort of stuff
        	primary.getEnchantments().forEach(
        			//TODO: canApply normally just takes canApplyAtEnchantingTable, 
        			//      this isn't a problem for vanilla but maybe for modded enchants that may only be applicable on anvils
        			(enchantment, level) -> {
        				if (enchantment.canApply(stack)) {
        					stack.addEnchantment(enchantment, level);
        				}
        			});        	
        	//EnchantmentHelper.setEnchantments(primary.getEnchantments(), stack);
        }
        validateTag(stack);
        NBTTagCompound tag = getTag(stack);
        tag.setString(Ref.KEY_TOOL_DATA_PRIMARY_MAT, primary != null ? primary.getName() : "NULL");
        tag.setString(Ref.KEY_TOOL_DATA_SECONDARY_MAT, secondary != null ? secondary.getName() : "NULL");
        tag.setInteger(Ref.KEY_TOOL_DATA_DURABILITY, getMaxDurability(stack));
        return stack;
    }

    public ItemStack get(Material primary, Material secondary, long... electricStats) {
        ItemStack stack = get(primary, secondary);
        if (type.isPowered() && electricStats.length >= 1) {
            NBTTagCompound tag = getTag(stack);
            tag.setLong(Ref.KEY_TOOL_DATA_ENERGY, electricStats[0]); //TODO temp, should be 0
            tag.setLong(Ref.KEY_TOOL_DATA_MAX_ENERGY, electricStats[0]);
        }
        return stack;
    }
    
    //TODO: Find a way to play sound at client when this is fired in getContainerItem + boolean return version?
    public ItemStack damage(ItemStack stack, int damage) {
    	stack = stack.copy();
    	stack.setCount(1);
    	NBTTagCompound tag = getTag(stack);
    	int newDamage = tag.getInteger(Ref.KEY_TOOL_DATA_DURABILITY) - damage;
    	if (type.isPowered()) {
    		long newEnergy = getEnergy(stack) - damage;
            if (newEnergy > 0) tag.setLong(Ref.KEY_TOOL_DATA_ENERGY, newEnergy);
            if (newEnergy < 0) tag.setLong(Ref.KEY_TOOL_DATA_ENERGY, 0);
    	}
    	else {
    		if (newDamage > 0) {
    			tag.setInteger(Ref.KEY_TOOL_DATA_DURABILITY, newDamage);
            } else {
                newDamage = 0;
                stack.setCount(0);
            }
    	}
    	return stack;
    }

    public int damage(ItemStack stack, int damage, EntityLivingBase living, boolean playSound) {
        NBTTagCompound tag = getTag(stack);
        if (living instanceof EntityPlayer && ((EntityPlayer) living).capabilities.isCreativeMode) {
            return tag.getInteger(Ref.KEY_TOOL_DATA_DURABILITY);
        }
        int newDamage = tag.getInteger(Ref.KEY_TOOL_DATA_DURABILITY) - damage;
        if (type.isPowered()) {
            if (living.getEntityWorld().rand.nextInt(25) == 0) {
                if (newDamage > 0) {
                    tag.setInteger(Ref.KEY_TOOL_DATA_DURABILITY, newDamage);
                } else {
                    newDamage = 0;
                    stack.damageItem(2, living);
                }
            }
            long newEnergy = getEnergy(stack) - damage;
            if (newEnergy > 0) tag.setLong(Ref.KEY_TOOL_DATA_ENERGY, newEnergy);
            if (newEnergy < 0) tag.setLong(Ref.KEY_TOOL_DATA_ENERGY, 0);
        } else {
            if (newDamage > 0) {
                tag.setInteger(Ref.KEY_TOOL_DATA_DURABILITY, newDamage);
            } else {
                newDamage = 0;
                stack.damageItem(2, living);
            }
        }
        if (playSound) type.playUseSound(living.world, living.getPosition());
        return newDamage;
    }

    public int getRGB(ItemStack stack, int i) {
        Material mat = i == 0 ? getPrimary(stack) : getSecondary(stack);
        return mat != null ? mat.getRGB() : 0xffffff;
    }

    /** NBT Section **/
    @Nullable
    public Material getPrimary(ItemStack stack) {
        return Materials.get(getTag(stack).getString(Ref.KEY_TOOL_DATA_PRIMARY_MAT));
    }

    @Nullable
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
        return mat != null ? type.getMiningSpeedMulti() * mat.getToolSpeed() : 1.0f;
    }

    public float getAttackDamage(ItemStack stack) {
        return type.getBaseAttackDamage() + getPrimaryQuality(stack);
    }

    public float getAttackSpeed(ItemStack stack) {
        return type.getBaseAttackSpeed() /*TODO decide + getPrimaryQuality(stack)*/;
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

    @Override
    public int getItemColor(ItemStack stack, @Nullable Block block, int i) {
        return getRGB(stack, i);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onModelRegistration() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(Ref.MODID + ":material_tool", "tool_type=" + type.getName()));
    }
}
