package muramasa.antimatter.structure;

import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import muramasa.antimatter.util.int3;
import net.minecraft.util.math.BlockPos;

public class StructureElement {

    public static StructureElement IGNORE = new StructureElement("ignore").exclude(); //Used to skip positions for non-cubic structures

    protected String elementId = "";
    private boolean exclude;

    public StructureElement() {

    }

    public StructureElement(String elementName) {
        this.elementId = elementName;
    }

    public StructureElement exclude() {
        exclude = true;
        return this;
    }

    public boolean excludes() {
        return exclude;
    }

    public boolean evaluate(TileEntityBasicMultiMachine<?> machine, int3 pos, StructureResult result) {
        return false;
    }

    public void onBuild(TileEntityBasicMultiMachine<?> machine, BlockPos pos, StructureResult result, int count) {

    }

    public void onRemove(TileEntityBasicMultiMachine<?> machine, BlockPos pos, StructureResult result, int count) {

    }

    public void onStateChange(TileEntityBasicMultiMachine<?> machine, MachineState newState, BlockPos pos, StructureResult result, int count) {

    }

    public boolean ticks() {
        return false;
    }

    public void tick(TileEntityBasicMultiMachine<?> machine, BlockPos pos) {

    }
}
