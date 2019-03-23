package muramasa.gregtech.api.pipe.types;

import muramasa.gregtech.api.materials.Material;

public class ItemPipe extends Pipe {

    private int slotCount, stepSize;
    private boolean restrictive;

    public ItemPipe(Material material, int slotCount, int stepSize, boolean restrictive) {
        super(material);
        this.slotCount = slotCount;
        this.stepSize = stepSize;
        this.restrictive = restrictive;
    }
}
