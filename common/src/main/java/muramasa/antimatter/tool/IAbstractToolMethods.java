package muramasa.antimatter.tool;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public interface IAbstractToolMethods {
    /* Common */
    default <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        return amount;
    }

    default int getMaxDamage(ItemStack stack){
        return stack.getItem().getMaxDamage();
    }

    default boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
        return this instanceof AxeItem;
    }

    default boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment.category.canEnchant(stack.getItem());
    }

    default boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player){
        return false;
    }
    /* Forge */
    default int getItemEnchantability(ItemStack stack)
    {
        return stack.getItem().getEnchantmentValue();
    }

    default boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return stack.getItem().isCorrectToolForDrops(state);
    }

    default Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slotType, ItemStack stack) {
        return stack.getItem().getDefaultAttributeModifiers(slotType);
    }


    /* Fabric */
    default Multimap<Attribute, AttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slotType) {
        return this.getAttributeModifiers(slotType, stack);
    }

    default boolean isSuitableFor(ItemStack stack, BlockState state) {
        return this.isCorrectToolForDrops(stack, state);
    }

    default int getEnchantability(ItemStack stack)
    {
        return getItemEnchantability(stack);
    }

    static <T extends LivingEntity> int damageItemStatic(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        if (stack.getItem() instanceof IAbstractToolMethods me){
            return me.damageItem(stack, amount, entity, onBroken);
        }
        return amount;
    }
}
