package muramasa.antimatter.material;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;

import java.util.Map;

public class NumberMaterialTag extends TypeMaterialTag<Integer> {
    public NumberMaterialTag(String id) {
        super(id);
    }

    public int getInt(Material mat){
        return get(mat);
    }

}
