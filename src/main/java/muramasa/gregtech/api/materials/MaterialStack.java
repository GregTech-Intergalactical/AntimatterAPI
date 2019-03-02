package muramasa.gregtech.api.materials;

public class MaterialStack {

    private Material material;
    private int size;

    public MaterialStack(Material material, int size) {
        this.material = material;
        this.size = size;
    }

    public Material get() {
        return material;
    }

    public int size() {
        return size;
    }
}
