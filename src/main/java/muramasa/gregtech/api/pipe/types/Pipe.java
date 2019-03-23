package muramasa.gregtech.api.pipe.types;

import muramasa.gregtech.api.materials.Material;

public class Pipe {

    private Material material;

    public Pipe(Material material) {
        this.material = material;
    }

    public Material getMaterial() {
        return material;
    }
}
