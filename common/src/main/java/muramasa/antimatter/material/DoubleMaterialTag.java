package muramasa.antimatter.material;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Map;

public class DoubleMaterialTag extends MaterialTag {

    private final Map<Material, Material> mapping = new Object2ObjectOpenHashMap<>();

    public DoubleMaterialTag(String id) {
        super(id);
    }
    
    public Material getMapping(Material mat) {
        return mapping.get(mat);
    }

    public DoubleMaterialTag add(Material mat, Material map) {
        super.add(mat);
        mapping.put(mat, map);
        return this;
    }

    public Map<Material, Material> getAll() {
        return mapping;
    }
}
