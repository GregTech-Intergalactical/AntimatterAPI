package muramasa.antimatter.material.data;

import com.google.common.collect.ImmutableMap;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.tool.AntimatterToolType;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;

public record ToolData(float toolDamage, float toolSpeed, int toolDurability, int toolQuality, Material handleMaterial,
                       ImmutableMap<Enchantment, Integer> toolEnchantment, List<AntimatterToolType> toolTypes) {
}
