package muramasa.antimatter.material.tags;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialTag;

import java.util.List;
import java.util.Map;

public class ListMaterialTag<T> extends MaterialTag {
    private final Map<Material, List<T>> mapping = new Object2ObjectArrayMap<>();
    public ListMaterialTag(String id) {
        super(id);
    }

    public ListMaterialTag <T> add(Material mat, List<T> map) {
        if (!mat.enabled) return this;
        if (!mapping.containsKey(mat)){
            super.add(mat);
        }
        mapping.put(mat, map);
        return this;
    }

    public ListMaterialTag <T> add(Material mat, T map) {
        if (!mat.enabled) return this;
        if (!mapping.containsKey(mat)){
            super.add(mat);
            mapping.put(mat, new ObjectArrayList<>());
        }
        mapping.get(mat).add(map);
        return this;
    }

    public Map<Material, List<T>> getAll() {
        return mapping;
    }

    public List<T> getList(Material mat){
        return mapping.get(mat);
    }
}
