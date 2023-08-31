package muramasa.antimatter.tool.enchantment;

import muramasa.antimatter.tool.IAntimatterTool;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class ElectricEnchantment extends Enchantment {
    public ElectricEnchantment(Rarity rarity, EnchantmentCategory enchantmentCategory, EquipmentSlot... equipmentSlots) {
        super(rarity, enchantmentCategory, equipmentSlots);
    }

    public int getMinCost(int level) {
        return 5 + (level - 1) * 8;
    }

    public int getMaxCost(int level) {
        return super.getMinCost(level) + 50;
    }

    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof IAntimatterTool tool && tool.getAntimatterToolType().isPowered() && super.canEnchant(stack);
    }
}
