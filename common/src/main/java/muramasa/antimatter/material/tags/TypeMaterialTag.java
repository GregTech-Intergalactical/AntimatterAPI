package muramasa.antimatter.material.tags;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialTag;

import java.util.Map;

public class TypeMaterialTag<T> extends MaterialTag {

    protected final Map<Material, T> mapping = new Object2ObjectArrayMap<>();
    public TypeMaterialTag(String id) {
        super(id);
    }

    public TypeMaterialTag <T> add(Material mat, T map) {
        if (!mat.enabled) return this;
        if (!mapping.containsKey(mat)){
            super.add(mat);
        }
        mapping.put(mat, map);
        return this;
    }

    public Map<Material, T> getAll() {
        return mapping;
    }

    public T get(Material mat){
        return mapping.get(mat);
    }
}
