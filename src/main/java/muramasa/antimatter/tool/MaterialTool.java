package muramasa.antimatter.tool;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.behaviour.IBehaviour;
import muramasa.antimatter.behaviour.IBlockDestroyed;
import muramasa.antimatter.behaviour.IItemUse;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.UnbreakingEnchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class MaterialTool extends ToolItem implements IAntimatterTool {

    protected String domain;
    protected IItemTier tier;
    protected AntimatterToolType type;
    protected Material primary;
    @Nullable protected Material secondary;
    protected Set<ToolType> toolTypes;

    protected int energyTier;
    protected long maxEnergy;

    public MaterialTool(String domain, AntimatterToolType type, IItemTier tier, Properties properties, Material primary, @Nullable Material secondary) {
        super(type.getBaseAttackDamage(), type.getBaseAttackSpeed(), tier, type.getEffectiveBlocks(), properties);
        this.domain = domain;
        this.type = type;
        this.tier = tier;
        this.primary = primary;
        this.secondary = secondary;
        this.toolTypes = type.getToolTypes();
        /*
        if (type.getUseAction() == UseAction.BOW) {
            this.addPropertyOverride(new ResourceLocation("pull"), (stack, world, entity) -> {
                if (entity == null) {
                    return 0.0F;
                } else {
                    return entity.getActiveItemStack().getItem() instanceof IAntimatterTool &&
                            ((IAntimatterTool) entity.getActiveItemStack().getItem()).getType().getUseAction() == UseAction.BOW ? 20.0F : 0.0F;
                }
            });
            this.addPropertyOverride(new ResourceLocation("pulling"),
                    (stack, world, entity) -> entity != null && entity.isHandActive() && entity.getActiveItemStack() == stack ? 1.0F : 0.0F);
        }
         */
        setRegistryName(domain, getId());
        AntimatterAPI.register(IAntimatterTool.class, this);
    }

    // Powered variant
    public MaterialTool(String domain, AntimatterToolType type, IItemTier tier, Properties properties, Material primary, @Nullable Material secondary, int energyTier) {
        super(type.getBaseAttackDamage(), type.getBaseAttackSpeed(), tier, type.getEffectiveBlocks(), properties);
        this.domain = domain;
        this.type = type;
        this.tier = tier;
        this.primary = primary;
        this.secondary = secondary;
        this.toolTypes = type.getToolTypes();
        this.energyTier = energyTier;
        this.maxEnergy = type.getBaseMaxEnergy() * energyTier; // Utils.getNumberOfDigits(type.getBaseMaxEnergy(), true);
        setRegistryName(domain, getId());
        AntimatterAPI.register(IAntimatterTool.class, this);
    }

    @Override
    public String getId() {
        String id = primary.getId() + "_";
        if (secondary != null) {
            id = id.concat(secondary.getId() + "_");
        }
        id = id.concat(type.getId());
        if (type.isPowered() && type.getEnergyTiers().length > 0) {
            id = id.concat("_" + Ref.VN[energyTier].toLowerCase(Locale.ENGLISH));
        }
        return id;
    }

    @Override
    public String getDomain() { return domain; }

    @Override
    public AntimatterToolType getType() { return type; }

    @Override
    public IItemTier getTier() { return tier; }

    @Override
    public Material getPrimaryMaterial() { return primary; }

    @Nullable
    @Override
    public Material getSecondaryMaterial() { return secondary; }

    @Override
    public Item asItem() { return this; }

    @Override
    public Set<ToolType> getToolTypes(ItemStack stack) {
        return toolTypes;
    }

    public int getEnergyTier() {
        return energyTier;
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> list) {
        //NOOP
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        if (flag.isAdvanced() && type.isPowered()) {
            tooltip.add(new StringTextComponent("Energy: " + getEnergy(stack) + " / " + getMaxEnergy(stack)));
        }
        if (type.getTooltip().size() != 0) {
            for (ITextComponent text : type.getTooltip()) {
                tooltip.add(text);
            }
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return type.getUseAction();
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return type.getUseAction() == UseAction.NONE ? super.getUseDuration(stack) : 72000;
    }

    @Override
    public boolean canHarvestBlock(BlockState state) {
        return Utils.isToolEffective(type, state) && tier.getHarvestLevel() >= state.getHarvestLevel();
    }

    @Override
    public int getHarvestLevel(ItemStack stack, net.minecraftforge.common.ToolType tool, @Nullable PlayerEntity player, @Nullable BlockState blockState) {
        return tier.getHarvestLevel();
    }

    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (type.getUseSound() != null) target.getEntityWorld().playSound(null, target.getPosX(), target.getPosY(), target.getPosZ(), type.getUseSound(), SoundCategory.HOSTILE, 0.75F, 0.75F);
        stack.damageItem(type.getAttackDurability(), attacker, (onBroken) -> onBroken.sendBreakAnimation(EquipmentSlotType.MAINHAND));
        return true;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return Utils.isToolEffective(type, state) ? tier.getEfficiency() : 1.0F;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity entity) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            if (getType().getUseSound() != null) player.playSound(getType().getUseSound(), SoundCategory.BLOCKS, 0.84F, 0.75F);
            boolean isToolEffective = Utils.isToolEffective(getType(), state);
            if (state.getBlockHardness(world, pos) != 0.0F) {
                stack.damageItem(isToolEffective ? getType().getUseDurability() : getType().getUseDurability() + 1, entity, (onBroken) -> onBroken.sendBreakAnimation(EquipmentSlotType.MAINHAND));
            }
        }
        boolean returnValue = true;
        for (Map.Entry<String, IBehaviour<MaterialTool>> e : type.getBehaviours().entrySet()) {
            IBehaviour b = e.getValue();
            if (!(b instanceof IBlockDestroyed)) continue;
            IBlockDestroyed<MaterialTool> d = (IBlockDestroyed) b;
            returnValue = d.onBlockDestroyed(this, stack, world, state, pos ,entity);
        }
        return returnValue;
    }

    @Override
    public boolean canPlayerBreakBlockWhileHolding(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        return type.getBlockBreakability();
    }

    /*
    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slotType) {
        Multimap<String, AttributeModifier> modifiers = super.getAttributeModifiers(slotType);
        if (slotType == EquipmentSlotType.MAINHAND) {
            modifiers.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", tier.getAttackDamage(), AttributeModifier.Operation.ADDITION));
            modifiers.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", type.getBaseAttackSpeed(), AttributeModifier.Operation.ADDITION));
        }
        return modifiers;
    }
     */

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        ActionResultType result = ActionResultType.PASS;
        for (Map.Entry<String, IBehaviour<MaterialTool>> e : type.getBehaviours().entrySet()) {
            IBehaviour b = e.getValue();
            if (!(b instanceof IItemUse)) continue;
            IItemUse<MaterialTool> u = (IItemUse) b;
            ActionResultType r = u.onItemUse(this, context);
            if (result != ActionResultType.SUCCESS) result = r;
        }
        return result;

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
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        if (entity instanceof PlayerEntity && ((PlayerEntity) entity).isCreative()) return 0;
        return damage(stack, amount);
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return true;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return !type.isPowered();
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (type.isPowered()) return enchantment != Enchantments.UNBREAKING;
        return super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack) {
        stack = stack.copy();
        int amount = damage(stack, type.getCraftingDurability());
        if (!type.isPowered()) { // Powered items can't enchant with Unbreaking
            int level = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack), j = 0;
            for (int k = 0; level > 0 && k < amount; k++) {
                if (UnbreakingEnchantment.negateDamage(stack, level, Ref.RNG)) j++;
            }
            amount -= j;
        }
        if (amount > 0) stack.setDamage(stack.getDamage() - amount);
        return stack;
    }

    protected int damage(ItemStack stack, int amount) {
        if (!type.isPowered()) return amount;
        CompoundNBT tag = getTag(stack);
        long currentEnergy = tag.getLong(Ref.KEY_TOOL_DATA_ENERGY);
        int multipliedDamage = amount * 100;
        if (Ref.RNG.nextInt(20) == 0) return amount; // 1/20 chance of taking durability off the tool
        else if (currentEnergy >= multipliedDamage) {
            tag.putLong(Ref.KEY_TOOL_DATA_ENERGY, currentEnergy - multipliedDamage); // Otherwise take energy off of tool if energy is larger than multiplied damage
            return 0; // Nothing is taken away from main durability
        }
        else { // Lastly, set energy to 0 and take leftovers off of tool durability itself
            int leftOver = (int) (multipliedDamage - currentEnergy);
            tag.putLong(Ref.KEY_TOOL_DATA_ENERGY, 0);
            return Math.max(1, leftOver / 100);
        }
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        // return MathHelper.hsvToRGB(Math.max(0.0F, (float) (1.0F - getDurabilityForDisplay(stack))) / 3.0F, 1.0F, 1.0F);
        if (type.isPowered()) return getEnergy(stack) > 0 ? 0x00BFFF : super.getRGBDurabilityForDisplay(stack);
        return super.getRGBDurabilityForDisplay(stack);
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        if (!type.isPowered()) return super.getDurabilityForDisplay(stack);
        long currentEnergy = getEnergy(stack);
        if (currentEnergy > 0) {
            double maxAmount = getMaxEnergy(stack), difference = maxAmount - currentEnergy;
            return difference / maxAmount;
        }
        return super.getDurabilityForDisplay(stack);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        if (type.isPowered()) return true;
        return super.showDurabilityBar(stack);
    }

    public boolean enoughDurability(ItemStack stack, int damage, boolean energy) {
        if (energy) {
            if (getEnergy(stack) >= damage * 100) return true;
        }
        return getDamage(stack) >= damage;
    }

    public long getEnergy(ItemStack stack) {
        return getTag(stack).getLong(Ref.KEY_TOOL_DATA_ENERGY);
    }

    public long getMaxEnergy(ItemStack stack) {
        return getTag(stack).getLong(Ref.KEY_TOOL_DATA_MAX_ENERGY);
    }

    public CompoundNBT getTag(ItemStack stack) {
        if (!stack.hasTag() || stack.getTag().get(Ref.TAG_TOOL_DATA) == null) validateTag(stack);
        return (CompoundNBT) stack.getTag().get(Ref.TAG_TOOL_DATA);
    }

    protected void validateTag(ItemStack stack) {
        stack.setTag(new CompoundNBT());
        CompoundNBT compound = new CompoundNBT();
        compound.putLong(Ref.KEY_TOOL_DATA_ENERGY, 0);
        compound.putLong(Ref.KEY_TOOL_DATA_MAX_ENERGY, maxEnergy);
        stack.getTag().put(Ref.TAG_TOOL_DATA, compound);
    }

}