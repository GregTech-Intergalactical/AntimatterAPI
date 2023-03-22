package muramasa.antimatter.material;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Map;

public class DoubleMaterialTag extends TypeMaterialTag<Material> {

    public DoubleMaterialTag(String id) {
        super(id);
    }
    
    public Material getMapping(Material mat) {
        return mapping.get(mat);
    }
}
