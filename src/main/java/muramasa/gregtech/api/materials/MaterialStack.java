package muramasa.gregtech.api.materials;

import muramasa.gregtech.api.data.Materials;

public class MaterialStack {

    public String name;
    public int size;

    public MaterialStack(String name, int size) {
        this.name = name;
        this.size = size;
    }

    public Material get() {
        return Materials.get(name);
    }
}
