package muramasa.antimatter.material.data;

import com.google.common.collect.ImmutableMap;
import net.minecraft.world.item.enchantment.Enchantment;

public record ArmorData(int[] armor, float toughness, float knockbackResistance, int armorDurabilityFactor,
                        ImmutableMap<Enchantment, Integer> toolEnchantment) {
}
