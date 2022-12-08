package muramasa.antimatter.tool;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.energy.ItemEnergyHandler;
import muramasa.antimatter.item.IContainerItem;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.util.Utils;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import tesseract.api.context.TesseractItemContext;
import tesseract.api.gt.IEnergyHandlerItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MaterialSword extends SwordItem implements IAntimatterTool, IContainerItem {

    protected String domain;
    protected AntimatterToolType type;

    protected int energyTier;
    protected long maxEnergy;

    public MaterialSword(String domain, AntimatterToolType type, Properties properties) {
        super(AntimatterItemTier.NULL, 0, type.getBaseAttackSpeed(), properties);  // 0 as base attack as it adds
        this.domain = domain;
        this.type = type;
        this.energyTier = -1;
        this.maxEnergy = -1;
        AntimatterAPI.register(IAntimatterTool.class, this);
    }

    public MaterialSword(String domain, AntimatterToolType type, Properties properties, int energyTier) {
        super(AntimatterItemTier.NULL, (int) type.getBaseAttackDamage(), type.getBaseAttackSpeed(), properties);
        this.domain = domain;
        this.type = type;
        this.energyTier = energyTier;
        this.maxEnergy = type.getBaseMaxEnergy() * energyTier;
        AntimatterAPI.register(IAntimatterTool.class, this);
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
    public AntimatterToolType getAntimatterToolType() {
        return type;
    }
    /**
     * Returns -1 if its not a powered tool
     **/
    public int getEnergyTier() {
        return energyTier;
    }

    @Nonnull
    @Override
    public ItemStack asItemStack(@Nonnull Material primary, @Nonnull Material secondary) {
        return resolveStack(primary, secondary, 0, maxEnergy);
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player) {
        return false;
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> list) {
        onGenericFillItemGroup(group, list, maxEnergy);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        onGenericAddInformation(stack, tooltip, flag);
        super.appendHoverText(stack, world, tooltip, flag);
    }

    //TODO figure this out
    //@Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return type.getUseAction();
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return type.getUseAction() == UseAnim.NONE ? super.getUseDuration(stack) : 72000;
    }

    /*@Override
    public boolean canHarvestBlock(ItemStack stack, BlockState state) {
        return Utils.isToolEffective(this, state) && getTier(stack).getLevel() >= state.getHarvestLevel();
    }*/

    /*
    @Override
    public int getHarvestLevel(ItemStack stack, Tag<Block> tool, @Nullable Player player, @Nullable BlockState blockState) {
        return getToolTypes().contains(tool) ? getTier(stack).getLevel() : -1;
    }*/

    @Override
    public int getMaxDamage(ItemStack stack) {
        return getTier(stack).getUses();
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return onGenericHitEntity(stack, target, attacker, 0.75F, 0.75F);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (state.getBlock() == Blocks.COBWEB) return 15.0F;
        return Utils.isToolEffective(this, state) ? getTier(stack).getSpeed() : 1.0F;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity entity) {
        return onGenericBlockDestroyed(stack, world, state, pos, entity);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        return onGenericItemUse(ctx);
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level world, BlockPos pos, Player player) {
        return type.getBlockBreakability();
    }

    @Override
    public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
        return type.getActualTags().contains(BlockTags.MINEABLE_WITH_AXE);
    }

    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slotType, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> modifiers = HashMultimap.create();
        if (slotType == EquipmentSlot.MAINHAND) {
            modifiers.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", type.getBaseAttackDamage() + getTier(stack).getAttackDamageBonus(), AttributeModifier.Operation.ADDITION));
            modifiers.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", type.getBaseAttackSpeed(), AttributeModifier.Operation.ADDITION));
        }
        return modifiers;
    }

    //fabric method
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slotType) {
        return this.getAttributeModifiers(slotType, stack);
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        return (entity instanceof Player && ((Player) entity).isCreative()) ? 0 : damage(stack, amount);
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return getTier(stack).getEnchantmentValue();
    }

    public int getEnchantability(ItemStack stack)
    {
        return getItemEnchantability(stack);
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return !type.isPowered() && getTier(toRepair).getRepairIngredient().test(repair);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return type.isPowered() ? enchantment != Enchantments.UNBREAKING : enchantment.category.canEnchant(stack.getItem());
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return type.hasContainer();
    }

    @Override
    public ItemStack getContainerItem(ItemStack oldStack) {
        return getGenericContainerItem(oldStack);
    }

    @Override
    public IEnergyHandlerItem createEnergyHandler(TesseractItemContext context) {
        return new ItemEnergyHandler(context, maxEnergy, 8 * (int) Math.pow(4, this.energyTier), 8 * (int) Math.pow(4, this.energyTier), 1, 1);
    }

}
