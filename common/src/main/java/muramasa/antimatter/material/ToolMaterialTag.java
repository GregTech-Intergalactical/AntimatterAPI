package muramasa.antimatter.material;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import muramasa.antimatter.tool.AntimatterToolType;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;
import java.util.Map;

public class ToolMaterialTag extends MaterialTag {
    private final Map<Material, ToolData> mapping = new Object2ObjectArrayMap<>();
    ToolMaterialTag() {
        super("tools");
    }

    public ToolMaterialTag add(Material mat, ToolData map) {
        if (!mapping.containsKey(mat)){
            super.add(mat);
        }
        mapping.put(mat, map);
        return this;
    }

    public Map<Material, ToolData> getAll() {
        return mapping;
    }

    public ToolData getToolData(Material mat){
        return mapping.get(mat);
    }
    public record ToolData(float toolDamage, float toolSpeed, int toolDurability, int toolQuality, ImmutableMap<Enchantment, Integer> toolEnchantment, List<AntimatterToolType> toolTypes){}
}
