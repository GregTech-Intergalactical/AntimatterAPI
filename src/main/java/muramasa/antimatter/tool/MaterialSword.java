package muramasa.antimatter.tool;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.UnbreakingEnchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

//TODO: power-sensitive version of MaterialSword
public class MaterialSword extends SwordItem implements IAntimatterTool {

    protected String domain;
    protected IItemTier tier;
    protected AntimatterToolType type;
    protected Material primary;
    protected Material secondary;

    public MaterialSword(String domain, AntimatterToolType type, IItemTier tier, Properties properties, Material primary, Material secondary) {
        super(tier, (int) type.getBaseAttackDamage(), type.getBaseAttackSpeed(), properties);
        this.domain = domain;
        this.type = type;
        this.tier = tier;
        this.primary = primary;
        this.secondary = secondary;
        setRegistryName(domain, getId());
        AntimatterAPI.register(IAntimatterTool.class, this);
    }

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public AntimatterToolType getType() {
        return type;
    }

    @Override
    public Material getPrimaryMaterial() {
        return primary;
    }

    @Nullable
    @Override
    public Material getSecondaryMaterial() {
        return secondary;
    }

    @Override
    public Item asItem() { return this; }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (isInGroup(group)/* && primary == Data.NULL*/) { // Remove the comments here when finalizing JEI shenanigans
            ItemStack stack = new ItemStack(this);
            items.add(stack);
        }
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
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (type.getUseSound() != null) target.getEntityWorld().playSound(null, target.getPosX(), target.getPosY(), target.getPosZ(), type.getUseSound(), SoundCategory.HOSTILE, 0.75F, 0.75F);
        stack.damageItem(type.getAttackDurability(), attacker, (onBroken) -> onBroken.sendBreakAnimation(EquipmentSlotType.MAINHAND));
        return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity livingEntity) {
        if (livingEntity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) livingEntity;
            if (type.getUseSound() != null) player.playSound(type.getUseSound(), SoundCategory.BLOCKS, 0.84F, 0.75F);
        }
        if (!world.isRemote) {
            stack.damageItem(state.getBlockHardness(world, pos) != 0.0F ? type.getUseDurability() : 0, livingEntity, (onBroken) -> onBroken.sendBreakAnimation(EquipmentSlotType.MAINHAND));
        }
        return true;
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack) {
        stack = stack.copy();
        int amount = type.getCraftingDurability();
        int level = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack), j = 0;
        for (int k = 0; level > 0 && k < amount; k++) {
            if (UnbreakingEnchantment.negateDamage(stack, level, Ref.RNG)) j++;
        }
        amount -= j;
        if (amount > 0) {
            int stackDamage = stack.getDamage();
            int damage = stackDamage + amount;
            // if (damage >= stackDamage) return ItemStack.EMPTY;
            stack.setDamage(damage);
            return stack;
        }
        return stack;
    }

    /*
    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slotType) {
        Multimap<String, AttributeModifier> modifiers = super.getAttributeModifiers(slotType);
        if (slotType == EquipmentSlotType.MAINHAND) {
            modifiers.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", this.getAttackDamage(), AttributeModifier.Operation.ADDITION));
            modifiers.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", type.getBaseAttackSpeed(), AttributeModifier.Operation.ADDITION));
        }
        return modifiers;
    }
     */

}
