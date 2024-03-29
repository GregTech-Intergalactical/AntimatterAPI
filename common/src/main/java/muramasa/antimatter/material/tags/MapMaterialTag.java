package muramasa.antimatter.material.tags;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialTag;

import java.util.Map;

public class MapMaterialTag<K, V> extends MaterialTag {
    private final Map<Material, Map<K, V>> mapping = new Object2ObjectArrayMap<>();
    public MapMaterialTag(String id) {
        super(id);
    }

    public MapMaterialTag<K, V> add(Material mat, Map<K, V> map) {
        if (!mat.enabled) return this;
        if (!mapping.containsKey(mat)){
            super.add(mat);
        }
        mapping.put(mat, map);
        return this;
    }

    public MapMaterialTag<K, V> add(Material mat, K key, V value) {
        if (!mat.enabled) return this;
        if (!mapping.containsKey(mat)){
            super.add(mat);
            mapping.put(mat, new Object2ObjectArrayMap<>());
        }
        mapping.get(mat).put(key, value);
        return this;
    }

    public Map<Material, Map<K, V>> getAll() {
        return mapping;
    }

    public Map<K, V> getMap(Material mat){
        return mapping.get(mat);
    }
}
