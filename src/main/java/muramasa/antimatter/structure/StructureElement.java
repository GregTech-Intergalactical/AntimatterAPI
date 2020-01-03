package muramasa.antimatter.structure;

import muramasa.antimatter.tileentities.TileEntityMachine;
import muramasa.antimatter.util.int3;

public class StructureElement {

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
