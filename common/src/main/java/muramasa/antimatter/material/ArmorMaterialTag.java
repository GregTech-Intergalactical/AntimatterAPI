package muramasa.antimatter.material;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
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
        return mapping.get(mat);
    }
    public record ArmorData(int[] armor, float toughness, float knockbackResistance, int armorDurabilityFactor, ImmutableMap<Enchantment, Integer> toolEnchantment){}
}
