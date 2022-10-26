package muramasa.antimatter.material;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;

import java.util.Map;

public class NumberMaterialTag extends MaterialTag {
    private final Map<Material, Integer> mapping = new Object2IntArrayMap<>();
    public NumberMaterialTag(String id) {
        super(id);
    }

    public NumberMaterialTag add(Material mat, int map) {
        if (!mapping.containsKey(mat)){
            super.add(mat);
        }
        mapping.put(mat, map);
        return this;
    }

    public Map<Material, Integer> getAll() {
        return mapping;
    }

    public int getInt(Material mat){
        return mapping.get(mat);
    }

}
