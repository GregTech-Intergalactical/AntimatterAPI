package muramasa.antimatter.material;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.util.valueproviders.UniformInt;

import java.util.Map;

public class IntRangeMaterialTag extends MaterialTag {
    private final Map<Material, UniformInt> mapping = new Object2ObjectArrayMap<>();
    public IntRangeMaterialTag(String id) {
        super(id);
    }

    public IntRangeMaterialTag add(Material mat, UniformInt map) {
        if (!mapping.containsKey(mat)){
            super.add(mat);
        }
        mapping.put(mat, map);
        return this;
    }

    public Map<Material, UniformInt> getAll() {
        return mapping;
    }

    public UniformInt getIntRange(Material mat){
        return mapping.get(mat);
    }
}
