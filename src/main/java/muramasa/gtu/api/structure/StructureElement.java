package muramasa.gtu.api.structure;

import muramasa.gtu.api.capability.IComponentHandler;
import muramasa.gtu.api.interfaces.IComponent;
import muramasa.gtu.api.interfaces.IGregTechObject;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.api.util.Utils;
import muramasa.gtu.api.util.int3;
import net.minecraft.tileentity.TileEntity;

public class StructureElement {

    private String elementName = "";
    private String[] elementIds;

    public boolean exclude;

    public StructureElement(String elementName, IGregTechObject... objects) {
        this(objects);
        this.elementName = elementName;
    }

    public StructureElement(IGregTechObject... objects) {
        this.elementIds = new String[objects.length];
        for (int i = 0; i < objects.length; i++) {
            this.elementIds[i] = objects[i].getName();
        }
    }

    public StructureElement exclude() {
        exclude = true;
        return this;
    }

    public boolean evaluate(TileEntityMachine machine, int3 pos, StructureResult result) {
        TileEntity tile = Utils.getTile(machine.getWorld(), pos.asBP());
        if (tile instanceof IComponent) {
            IComponentHandler component = ((IComponent) tile).getComponentHandler();
            for (int i = 0; i < elementIds.length; i++) {
                if (elementIds[i].equals(component.getId())) {
                    if (testComponent(component)) {
                        result.addComponent(elementName, component);
                        return true;
                    }
                    result.withError("Failed Component Requirement: " + component.getId());
                    return false;
                }
            }
            result.withError("Expected: '" + elementName + "' Found: '" + component.getId() + "' @" + pos);
            return false;
        }
        result.withError("No valid component found @" + pos);
        return false;
    }

    public boolean testComponent(IComponentHandler component) {
        return true;
    }
}
