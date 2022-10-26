package muramasa.antimatter.material;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Map;

public class HandleMaterialTag extends MaterialTag {
    private final Map<Material, HandleData> mapping = new Object2ObjectArrayMap<>();
    HandleMaterialTag() {
        super("handle");
    }

    public HandleMaterialTag add(Material mat, HandleData map) {
        if (!mapping.containsKey(mat)){
            super.add(mat);
        }
        mapping.put(mat, map);
        return this;
    }

    public Map<Material, HandleData> getAll() {
        return mapping;
    }

    public HandleData getHandleData(Material mat){
        return mapping.get(mat);
    }
    public record HandleData(int durability, float speed, ImmutableMap<Enchantment, Integer> toolEnchantment){}
}
