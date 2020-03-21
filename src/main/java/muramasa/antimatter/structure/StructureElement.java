package muramasa.antimatter.structure;

import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.int3;

public class StructureElement {

    public static StructureElement IGNORE = new StructureElement("ignore").exclude(); //Used to skip positions for non-cubic structures
    public static BlockStateElement AIR = new BlockStateElement("air", (r, p, s) -> s.isAir(r, p)); //Air check

    protected String elementId = "";
    protected boolean exclude;

    public StructureElement() {

    }

    public StructureElement(String elementName) {
        this.elementId = elementName;
    }

    public StructureElement exclude() {
        exclude = true;
        return this;
    }

    public boolean evaluate(TileEntityMachine machine, int3 pos, StructureResult result) {
        return false;
    }
}
