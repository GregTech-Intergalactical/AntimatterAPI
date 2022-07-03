package muramasa.antimatter.material;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;
import java.util.Map;

public class ListMaterialTag<T> extends MaterialTag {
    private final Map<Material, List<T>> mapping = new Object2ObjectArrayMap<>();
    public ListMaterialTag(String id) {
        super(id);
    }

    public ListMaterialTag <T> add(Material mat, List<T> map) {
        if (!mapping.containsKey(mat)){
            super.add(mat);
        }
        mapping.put(mat, map);
        return this;
    }

    public ListMaterialTag <T> add(Material mat, T map) {
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
