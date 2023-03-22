package muramasa.antimatter.material;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import java.util.Map;

public class TypeMaterialTag<T> extends MaterialTag {

    protected final Map<Material, T> mapping = new Object2ObjectArrayMap<>();
    public TypeMaterialTag(String id) {
        super(id);
    }

    public TypeMaterialTag <T> add(Material mat, T map) {
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
