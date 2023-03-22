package muramasa.antimatter.material.tags;

import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.tags.TypeMaterialTag;

public class DoubleMaterialTag extends TypeMaterialTag<Material> {

    public DoubleMaterialTag(String id) {
        super(id);
    }
    
    public Material getMapping(Material mat) {
        return mapping.get(mat);
    }
}
