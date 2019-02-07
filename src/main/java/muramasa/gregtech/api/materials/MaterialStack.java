package muramasa.gregtech.api.materials;

import muramasa.gregtech.api.data.Materials;

public class MaterialStack {

    public int id;
    public int size;

    public MaterialStack(int id, int size) {
        this.id = id;
        this.size = size;
    }

    public Material get() {
        return Materials.get(id);
    }
}
