package muramasa.antimatter.material.tags;

import muramasa.antimatter.material.Material;

public class NumberMaterialTag extends TypeMaterialTag<Integer> {
    public NumberMaterialTag(String id) {
        super(id);
    }

    public int getInt(Material mat){
        return get(mat);
    }

}
