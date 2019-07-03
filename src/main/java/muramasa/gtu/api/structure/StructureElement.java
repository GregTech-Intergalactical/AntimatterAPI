package muramasa.gtu.api.structure;

import muramasa.gtu.api.capability.IComponentHandler;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.api.util.int3;

public class StructureElement {

    protected String elementId;
    protected boolean exclude;

    public StructureElement(String elementId) {
        this.elementId = elementId;
    }

    public StructureElement exclude() {
        exclude = true;
        return this;
    }

    public boolean evaluate(TileEntityMachine machine, int3 pos, StructureResult result) {
        return false;
    }

    public boolean testComponent(IComponentHandler component) {
        return true;
    }
}
