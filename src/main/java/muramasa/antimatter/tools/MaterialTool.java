package muramasa.antimatter.tools;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.materials.Material;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class MaterialTool extends TieredItem implements IAntimatterTool {

    protected String domain;
    protected IItemTier tier;
    protected AntimatterToolType type;
    protected Material primary;
    protected Material secondary;
    protected Set<ToolType> toolTypes;

    public MaterialTool(String domain, AntimatterToolType type, IItemTier tier, Properties properties, Material primary, Material secondary) {
        super(tier, properties);
        this.domain = domain;
        this.type = type;
        this.tier = tier;
        this.primary = primary;
        this.secondary = secondary;
        this.toolTypes = type.getToolTypes();
        setRegistryName(domain, getId());
        AntimatterAPI.register(IAntimatterTool.class, this);
    }

    @Override
    public String getDomain() { return domain; }

    @Override
    public AntimatterToolType getType() { return type; }

    @Override
    public Material getPrimaryMaterial() { return primary; }

    @Override
    public Material getSecondaryMaterial() { return secondary; }

    @Override
    public Item asItem() { return this; }

    @Override
    public Set<ToolType> getToolTypes(ItemStack stack) {
        return toolTypes;
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (isInGroup(group)) {
            ItemStack stack = new ItemStack(this);
            if (tier instanceof AntimatterItemTier && (!primary.getEnchantments().entrySet().isEmpty())) {
                ((AntimatterItemTier) tier).getNativeEnchantments().entrySet().forEach(e -> {
                    if (stack.canApplyAtEnchantingTable(e.getKey())) stack.addEnchantment(e.getKey(), e.getValue());
                });
            }
            items.add(stack);
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (!type.getTooltip().isEmpty()) tooltip.add(new StringTextComponent(type.getTooltip()));
        //        Material primary = getPrimary(stack), secondary = getSecondary(stack);
//        if (primary == null || secondary == null) { //Under general circumstances, this will only be seen in JEI and creative tab
//            if (Ref.GENERAL_DEBUG) tooltip.add(TextFormatting.WHITE + "Null Material");
//            return;
//        }
//        if (GuiScreen.isCtrlKeyDown()) {
//            tooltip.add(TextFormatting.WHITE + primary.getDisplayName() + TextFormatting.YELLOW + "(" + getPrimaryQuality(stack) + ")" + TextFormatting.WHITE + " / " + (secondary != null ? secondary.getDisplayName() : "NULL") + TextFormatting.YELLOW + "(" + getSecondaryQuality(stack) + ")");
//            if (type.isPowered()) tooltip.add(TextFormatting.WHITE + "Energy: " + TextFormatting.AQUA + Utils.formatNumber(getEnergy(stack)) + " / " + Utils.formatNumber(getMaxEnergy(stack)));
//            tooltip.add(TextFormatting.WHITE + "Durability: " + TextFormatting.GREEN + getDurability(stack) + " / " + getMaxDurability(stack));
//            tooltip.add(TextFormatting.WHITE + "Attack Damage: " + TextFormatting.BLUE + getAttackDamage(stack));
//            tooltip.add(TextFormatting.WHITE + "Attack Speed: " + TextFormatting.BLUE + getAttackSpeed(stack));
//            tooltip.add(TextFormatting.WHITE + "Mining Speed: " + TextFormatting.LIGHT_PURPLE + getMiningSpeed(stack));
//            if (!type.getTooltip().equals("")) tooltip.add(TextFormatting.GRAY + "" + TextFormatting.UNDERLINE + type.getTooltip());
//        }
//        else {
//            tooltip.add(TextFormatting.UNDERLINE + "Press Ctrl Key for Tool Stats");
//        }
    }

    @Override
    public int getHarvestLevel(ItemStack stack, net.minecraftforge.common.ToolType tool, @Nullable PlayerEntity player, @Nullable BlockState blockState) {
        return tier.getHarvestLevel();
    }

    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damageItem(type.getAttackDurability(), attacker, (onBroken) -> onBroken.sendBreakAnimation(EquipmentSlotType.MAINHAND));
        return true;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (getToolTypes(stack).stream().anyMatch(e -> state.isToolEffective(e))) return tier.getEfficiency();
        return type.getEffectiveBlocks().contains(state.getBlock()) ? tier.getEfficiency() : 1.0F;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        if (!worldIn.isRemote && state.getBlockHardness(worldIn, pos) != 0.0F) {
            stack.damageItem(type.getMiningDurability(), entityLiving, (onBroken) -> onBroken.sendBreakAnimation(EquipmentSlotType.MAINHAND));
        }
        return true;
    }

    @Override
    public boolean canPlayerBreakBlockWhileHolding(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        return type.getBlockBreakability();
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slotType) {
        Multimap<String, AttributeModifier> modifiers = super.getAttributeModifiers(slotType);
        if (slotType == EquipmentSlotType.MAINHAND) {
            modifiers.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", tier.getAttackDamage() + type.getBaseAttackDamage(), AttributeModifier.Operation.ADDITION));
            modifiers.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", type.getBaseAttackSpeed(), AttributeModifier.Operation.ADDITION));
        }
        return modifiers;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        ItemStack stack = context.getPlayer().getHeldItem(context.getHand());

        if (context.getWorld().getBlockState(context.getPos()) == Blocks.REDSTONE_BLOCK.getDefaultState()) {
            if (type.isPowered()) {
                getTag(stack).putLong(Ref.KEY_TOOL_DATA_ENERGY, getTag(stack).getLong(Ref.KEY_TOOL_DATA_ENERGY) + 50000);
            }
        }

        //TODO functionality moved to BlockMachine.onBlockActivated
        //TODO determine if other mods need smart interaction on
        //TODO blocks that *don't* extend BlockMachine
//        TileEntity tile = Utils.getTile(world, pos);
//        if (tile == null) return EnumActionResult.PASS;
//        EnumActionResult result = EnumActionResult.PASS;
//        if (tile.hasCapability(GTCapabilities.CONFIGURABLE, facing)) {
//            Direction targetSide = Utils.getInteractSide(facing, hitX, hitY, hitZ);
//            IConfigHandler configHandler = tile.getCapability(GTCapabilities.CONFIGURABLE, targetSide);
//            if (configHandler != null) {
//                if (type != null && configHandler.onInteract(player, hand, targetSide, type)) {
//                    damage(stack, type.getDamageCrafting(), player, true);
//                    result = EnumActionResult.SUCCESS;
//                }
//            }
//        }
//        if (tile.hasCapability(GTCapabilities.COVERABLE, facing)) {
//            Direction targetSide = Utils.getInteractSide(facing, hitX, hitY, hitZ);
//            ICoverHandler coverHandler = tile.getCapability(GTCapabilities.COVERABLE, targetSide);
//            if (coverHandler != null) {
//                if (type != null && coverHandler.onInteract(player, hand, targetSide, type)) {
//                    damage(stack, type.getDamageCrafting(), player, true);
//                    result = EnumActionResult.SUCCESS;
//                }
//            }
//        }
//        return result;

        return ActionResultType.PASS;
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack) {
        stack = stack.copy();
        damage(stack, getType().getCraftingDurability(), null, true);
    	return stack;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getItemEnchantability() {
        return tier.getEnchantability();
    }

//    @Override
//    public int getHarvestLevel(ItemStack stack, String toolClass, @Nullable PlayerEntity player, @Nullable BlockState blockState) {
//        int level = super.getHarvestLevel(stack, toolClass,  player, blockState);
//        if (level == -1 && getToolClasses(stack).contains(toolClass)) {
//            if (!canMine(stack)) return level;
//            Material mat = getPrimary(stack);
//            if (mat == null) return level;
//            return getPrimaryQuality(stack);
//        } else {
//            return level;
//        }
//    }
//
//    @Override
//    public Set<String> getToolClasses(ItemStack stack) {
//        return type.getToolClass();
//    }

    /*
    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1 - ((float) getDurability(stack) / (float) getMaxDurability(stack));
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        int durability = getDurability(stack);
        return durability > 0 && durability < getMaxDurability(stack);
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return MathHelper.hsvToRGB(Math.max(0.0F, (float) (1.0F - getDurabilityForDisplay(stack))) / 3.0F, 1.0F, 1.0F);
    }
     */

//    @Override
//    public boolean onBlockStartBreak(ItemStack stack, BlockPos originPos, PlayerEntity player) {
//        //TODO move this to onBlockDestroyed?
//        if (getDurability(stack) < type.getDamageMining()) return false;
//        if (player.capabilities.isCreativeMode) return false; //TODO temp?
//        for (BlockPos pos : getAOEBlocks(stack, player.world, player, originPos)) {
//            if (!ForgeHooks.canHarvestBlock(player.world.getBlockState(pos).getBlock(), player, player.world, pos)) continue;
//            Utils.breakBlock(stack, player.world, player.world.getBlockState(pos), pos, player);
//            if (damage(stack, type.getDamageMining(), player, false) <= 0) {
//                System.out.println("broke tool early");
//                break;
//            }
//        }
//        return false;
//    }

    /** Helper Methods **/
    public Set<BlockPos> getAOEBlocks(ItemStack stack, World world, PlayerEntity player, BlockPos origin) {
        return ImmutableSet.of();
    }

    public boolean canMine(ItemStack stack) {
        boolean can = stack.getDamage() > 0;
        if (type.isPowered() && getEnergy(stack) <= 0) can = false;
        return can;
    }

    public ItemStack get(Material primary, Material secondary) {
        ItemStack stack = new ItemStack(this);
//        if (primary != null && !primary.getEnchantments().isEmpty()) {
//            //TODO: canApply normally just takes canApplyAtEnchantingTable,
//            //      this isn't a problem for vanilla but maybe for modded enchants that may only be applicable on anvils
//            //      Added check to stop pickaxes getting looting, swords getting fortune that sort of stuff
//        	primary.getEnchantments().forEach((enchantment, level) -> {
//                if (enchantment.canApply(stack)) stack.addEnchantment(enchantment, level);
//            });
//        }
//        validateTag(stack);
//        CompoundNBT tag = getTag(stack);
//        tag.setString(Ref.KEY_TOOL_DATA_PRIMARY_MAT, primary != null ? primary.getId() : "NULL");
//        tag.setString(Ref.KEY_TOOL_DATA_SECONDARY_MAT, secondary != null ? secondary.getId() : "NULL");
//        tag.setInteger(Ref.KEY_TOOL_DATA_DURABILITY, getMaxDurability(stack));
        return stack;
    }

    public ItemStack get(Material primary, Material secondary, long... electricStats) {
        ItemStack stack = get(primary, secondary);
//        if (type.isPowered() && electricStats.length >= 1) {
//            CompoundNBT tag = getTag(stack);
//            tag.setLong(Ref.KEY_TOOL_DATA_ENERGY, electricStats[0]); //TODO temp, should be 0
//            tag.setLong(Ref.KEY_TOOL_DATA_MAX_ENERGY, electricStats[0]);
//        }
        return stack;
    }

    public int damage(ItemStack stack, int damage, @Nullable LivingEntity living, boolean playSound) {
        return damage; //TODO placeholder
//        CompoundNBT tag = getTag(stack);
//        if (living instanceof PlayerEntity && ((PlayerEntity) living).capabilities.isCreativeMode) {
//            return tag.getInteger(Ref.KEY_TOOL_DATA_DURABILITY);
//        }
//        int newDamage = tag.getInteger(Ref.KEY_TOOL_DATA_DURABILITY) - damage;
//        if (type.isPowered()) {
//            if (Ref.RNG.nextInt(25) == 0) {
//                if (newDamage > 0) {
//                    tag.setInteger(Ref.KEY_TOOL_DATA_DURABILITY, newDamage);
//                } else {
//                    newDamage = 0;
//                    stack.shrink(1);
//                }
//            }
//            long newEnergy = getEnergy(stack) - damage;
//            if (newEnergy > 0) tag.setLong(Ref.KEY_TOOL_DATA_ENERGY, newEnergy);
//            if (newEnergy < 0) tag.setLong(Ref.KEY_TOOL_DATA_ENERGY, 0);
//        } else {
//            if (newDamage > 0) {
//                tag.setInteger(Ref.KEY_TOOL_DATA_DURABILITY, newDamage);
//            } else {
//                newDamage = 0;
//                stack.shrink(1);
//            }
//        }
//        if (playSound && type.getUseSound() != null) {
//            if (living != null) type.getUseSound().play(living.world, living.getPosition());
//            else GregTech.PROXY.playSound(type.getUseSound());
//        }
//        return newDamage;
    }

    /** NBT Section **/
    @Nullable
    public Material getPrimary(ItemStack stack) {
        return Material.get(getTag(stack).getString(Ref.KEY_TOOL_DATA_PRIMARY_MAT));
    }

    @Nullable
    public Material getSecondary(ItemStack stack) {
        return Material.get(getTag(stack).getString(Ref.KEY_TOOL_DATA_SECONDARY_MAT));
    }

    public int getPrimaryQuality(ItemStack stack) {
        Material mat = getPrimary(stack);
        return mat != null ? type.getBaseQuality() + mat.getToolQuality() : 1;
    }

    public int getSecondaryQuality(ItemStack stack) {
        Material mat = getSecondary(stack);
        return mat != null ? type.getBaseQuality() + mat.getToolQuality() : 1;
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

    public CompoundNBT getTag(ItemStack stack) {
        if (!stack.hasTag()) validateTag(stack);
        return (CompoundNBT) stack.getTag().get(Ref.TAG_TOOL_DATA);
    }

    public void validateTag(ItemStack stack) {
        if (!stack.hasTag()) stack.setTag(new CompoundNBT());
        if (!stack.getTag().contains(Ref.TAG_TOOL_DATA)) {
            CompoundNBT compound = new CompoundNBT();
            compound.putString(Ref.KEY_TOOL_DATA_PRIMARY_MAT, "NULL");
            compound.putString(Ref.KEY_TOOL_DATA_SECONDARY_MAT, "NULL");
            compound.putInt(Ref.KEY_TOOL_DATA_DURABILITY, 0);
            compound.putLong(Ref.KEY_TOOL_DATA_ENERGY, 0);
            compound.putLong(Ref.KEY_TOOL_DATA_MAX_ENERGY, 0);
            stack.getTag().put(Ref.TAG_TOOL_DATA, compound);
        }
    }
}
