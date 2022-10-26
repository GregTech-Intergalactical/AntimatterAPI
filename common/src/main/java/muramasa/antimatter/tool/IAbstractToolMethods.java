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
    <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken);

    int getMaxDamage(ItemStack stack);

    boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment);

    static <T extends LivingEntity> int damageItemStatic(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        if (stack.getItem() instanceof IAbstractToolMethods me){
            return me.damageItem(stack, amount, entity, onBroken);
        }
        return amount;
    }
}
