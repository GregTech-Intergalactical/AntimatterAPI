package muramasa.antimatter.tool;

import com.google.common.collect.Multimap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
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
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
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
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class MaterialTool extends ToolItem implements IAntimatterTool {

    protected String domain;
    protected AntimatterToolType type;

    protected int energyTier;
    protected long maxEnergy;

    public MaterialTool(String domain, AntimatterToolType type, Properties properties) {
        super(type.getBaseAttackDamage(), type.getBaseAttackSpeed(), AntimatterItemTier.NULL, type.getEffectiveBlocks(), properties);
        this.domain = domain;
        this.type = type;
        AntimatterAPI.register(IAntimatterTool.class, getId(), this);
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
    }

    // Powered variant
    public MaterialTool(String domain, AntimatterToolType type, Properties properties, int energyTier) {
        super(type.getBaseAttackDamage(), type.getBaseAttackSpeed(), AntimatterItemTier.NULL, type.getEffectiveBlocks(), properties);
        this.domain = domain;
        this.type = type;
        this.energyTier = energyTier;
        this.maxEnergy = type.getBaseMaxEnergy() * energyTier; // Utils.getNumberOfDigits(type.getBaseMaxEnergy(), true);
        AntimatterAPI.register(this);
    }

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public String getId() {
        return type.isPowered() ? String.join("_", type.getId(), Ref.VN[energyTier].toLowerCase(Locale.ENGLISH)) : type.getId();
    }

    @Nonnull
    @Override
    public AntimatterToolType getType() { return type; }

    @Nonnull
    @Override
    public Material getPrimaryMaterial(ItemStack stack) { return getMaterials(stack)[0]; }

    @Nonnull
    @Override
    public Material getSecondaryMaterial(ItemStack stack) { return getMaterials(stack)[1]; }

    @Override
    public Set<ToolType> getToolTypes(ItemStack stack) {
        return type.getToolTypes();
    }

    @Nonnull
    @Override
    public Item asItem() {
        return this;
    }

    @Nonnull
    @Override
    public ItemStack asItemStack(@Nonnull Material primary, @Nonnull Material secondary) {
        ItemStack stack = new ItemStack(this);
        validateTag(stack, primary, secondary);
        if (primary.getEnchantments() != null) primary.getEnchantments().forEach(stack::addEnchantment);
        if (secondary.getEnchantments() != null) secondary.getEnchantments().forEach(stack::addEnchantment);
        return stack;
    }

    public int getEnergyTier() {
        return energyTier;
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> list) {
        if (group != Ref.TAB_TOOLS) return;
        list.add(new ItemStack(this));
    }

    /*
    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        return getPrimaryMaterial(stack).getDisplayName().appendSibling(new StringTextComponent(type.getId()));
    }
     */

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
    public boolean canHarvestBlock(ItemStack stack, BlockState state) {
        Material[] mats = getMaterials(stack);
        return Utils.isToolEffective(type, state) && AntimatterItemTier.getOrCreate(mats[0], mats[1]).getHarvestLevel() >= state.getHarvestLevel();
    }

    @Override
    public int getHarvestLevel(ItemStack stack, ToolType tool, @Nullable PlayerEntity player, @Nullable BlockState blockState) {
        Material[] mats = getMaterials(stack);
        return AntimatterItemTier.getOrCreate(mats[0], mats[1]).getHarvestLevel();
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        Material[] mats = getMaterials(stack);
        return AntimatterItemTier.getOrCreate(mats[0], mats[1]).getMaxUses();
    }

    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (type.getUseSound() != null) target.getEntityWorld().playSound(null, target.getPosX(), target.getPosY(), target.getPosZ(), type.getUseSound(), SoundCategory.HOSTILE, 0.75F, 0.75F);
        stack.damageItem(type.getAttackDurability(), attacker, (onBroken) -> onBroken.sendBreakAnimation(EquipmentSlotType.MAINHAND));
        return true;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        Material[] mats = getMaterials(stack);
        return Utils.isToolEffective(type, state) ? AntimatterItemTier.getOrCreate(mats[0], mats[1]).getEfficiency() : 1.0F;
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

    @Override
    public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
        return type.getToolTypes().contains("axe");
    }

    // ItemStack sensitive version
    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slotType, ItemStack stack) {
        Multimap<String, AttributeModifier> modifiers = super.getAttributeModifiers(slotType);
        if (slotType == EquipmentSlotType.MAINHAND) {
            Material[] mats = getMaterials(stack);
            IItemTier tier = AntimatterItemTier.getOrCreate(mats[0], mats[1]);
            modifiers.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", tier.getAttackDamage() + type.getBaseAttackDamage(), AttributeModifier.Operation.ADDITION));
            modifiers.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", type.getBaseAttackSpeed(), AttributeModifier.Operation.ADDITION));
        }
        return modifiers;
    }

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
    public float getXpRepairRatio(ItemStack stack) {
        return 2f; // TODO
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
        CompoundNBT tag = getDataTag(stack);
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

    public boolean hasEnoughDurability(ItemStack stack, int damage, boolean energy) {
        if (energy && getEnergy(stack) >= damage * 100) return true;
        return getDamage(stack) >= damage;
    }

    public Material[] getMaterials(ItemStack stack) {
        CompoundNBT nbt = getDataTag(stack);
        return IntStream.of(nbt.getInt(Ref.KEY_TOOL_DATA_PRIMARY_MATERIAL), nbt.getInt(Ref.KEY_TOOL_DATA_SECONDARY_MATERIAL)).mapToObj(Material::get).toArray(Material[]::new);
    }

    public long getEnergy(ItemStack stack) {
        return getDataTag(stack).getLong(Ref.KEY_TOOL_DATA_ENERGY);
    }

    public long getMaxEnergy(ItemStack stack) {
        return getDataTag(stack).getLong(Ref.KEY_TOOL_DATA_MAX_ENERGY);
    }

    public CompoundNBT getDataTag(ItemStack stack) {
        CompoundNBT nbt = stack.getChildTag(Ref.TAG_TOOL_DATA);
        return nbt == null ? validateTag(stack, Data.NULL, Data.NULL) : nbt;
    }

    protected CompoundNBT validateTag(ItemStack stack, Material primary, Material secondary) {
        CompoundNBT nbt = stack.getOrCreateChildTag(Ref.TAG_TOOL_DATA);
        if (!nbt.isEmpty()) return nbt;
        nbt.putInt(Ref.KEY_TOOL_DATA_PRIMARY_MATERIAL, primary.getHash());
        nbt.putInt(Ref.KEY_TOOL_DATA_SECONDARY_MATERIAL, secondary.getHash());
        if (!type.isPowered()) return nbt;
        nbt.putLong(Ref.KEY_TOOL_DATA_ENERGY, 0);
        nbt.putLong(Ref.KEY_TOOL_DATA_MAX_ENERGY, maxEnergy);
        // stack.getTag().put(Ref.TAG_TOOL_DATA, compound);
        return nbt;
    }

}