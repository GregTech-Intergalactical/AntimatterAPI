package muramasa.antimatter.material;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import muramasa.antimatter.Data;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Map;

public class ArmorMaterialTag extends MaterialTag {
    private final Map<Material, ArmorData> mapping = new Object2ObjectArrayMap<>();
    ArmorMaterialTag() {
        super("armor");
    }

    public ArmorMaterialTag add(Material mat, ArmorData map) {
        if (!mapping.containsKey(mat)){
            super.add(mat);
        }
        mapping.put(mat, map);
        return this;
    }

    public Map<Material, ArmorData> getAll() {
        return mapping;
    }

    public ArmorData getArmorData(Material mat){
        if (mat == Material.NULL) return mapping.computeIfAbsent(mat, m -> new ArmorData(new int[]{1, 1, 1, 1}, 0.0f, 0.0f, 23, ImmutableMap.of()));
        return mapping.get(mat);
    }
    public record ArmorData(int[] armor, float toughness, float knockbackResistance, int armorDurabilityFactor, ImmutableMap<Enchantment, Integer> toolEnchantment){}
}
