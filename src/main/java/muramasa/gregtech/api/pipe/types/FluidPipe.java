package muramasa.gregtech.api.pipe.types;

import muramasa.gregtech.api.materials.Material;

public class FluidPipe extends Pipe {

    private int capacity, heatResistance;
    private boolean gasProof;

    public FluidPipe(Material material, int capacity, int heatResistance, boolean gasProof) {
        super(material);
        this.capacity = capacity;
        this.heatResistance = heatResistance;
        this.gasProof = gasProof;
    }
}
