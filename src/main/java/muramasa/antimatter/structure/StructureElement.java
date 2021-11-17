package muramasa.antimatter.structure;

import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.types.BasicMultiMachine;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import muramasa.antimatter.util.int3;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public class StructureElement {

    public static StructureElement IGNORE = new StructureElement("ignore").exclude(); //Used to skip positions for non-cubic structures
    protected final String elementId;
    private boolean exclude;

    public StructureElement() {
        this.elementId = "";
    }

    public StructureElement(String elementName) {
        this.elementId = elementName;
    }

    /**
     * Set if this element is not actually used (If this is not a cubic structure, this means the
     * multiblock does not receive structure updates at this position).
     *
     * @return this.
     */
    public StructureElement exclude() {
        exclude = true;
        return this;
    }

    public boolean renderShared() {
        return false;
    }

    public boolean excludes() {
        return exclude;
    }

    /**
     * Evaluate this element and check if the given position is valid for this multiblock.
     *
     * @param machine the multi controller tile.
     * @param pos     the current position.
     * @param result  the structure result.
     * @return if it is valid.
     */
    public boolean evaluate(TileEntityBasicMultiMachine<?> machine, int3 pos, StructureResult result) {
        return false;
    }

    /**
     * After evaluation, this is called to build the structure, after everything is verified. See for example
     * the fake tile element as it here sets the block as a fake tile
     *
     * @param machine the controller tile.
     * @param pos     the current position.
     * @param result  the structure result.
     * @param count   the ref count at this position(1 == first, > 1 means there are more than 1 structure using this position.
     */
    public void onBuild(TileEntityBasicMultiMachine<?> machine, BlockPos pos, StructureResult result, int count) {

    }

    /**
     * Called as the machine removes its proper structure.
     *
     * @param machine the controller tile.
     * @param pos     the current position.
     * @param result  the structure result.
     * @param count   the ref count at this position(0 == no more structures at this position)
     */
    public void onRemove(TileEntityBasicMultiMachine<?> machine, BlockPos pos, StructureResult result, int count) {

    }

    /**
     * called as the tile changes between texture states (For now, ACTIVE -> IDLE -> IDLE...) as INVALID_STRUCTURE does not call this.
     *
     * @param machine  controller.
     * @param newState the new state.
     * @param pos      the blockpos.
     * @param result   the structure result.
     * @param count    refCount as usual
     */
    public void onStateChange(TileEntityBasicMultiMachine<?> machine, MachineState newState, BlockPos pos, StructureResult result, int count) {

    }

    public void onInfoTooltip(List<ITextComponent> text, long count, TileEntityBasicMultiMachine<?> machine) {

    }

    /**
     * Should this element tick?
     *
     * @return if it ticks.
     */
    public boolean ticks() {
        return false;
    }

    /**
     * Called every tick for elements that tick.
     *
     * @param machine the controller
     * @param pos     this elements position.
     */
    public void tick(TileEntityBasicMultiMachine<?> machine, StructureResult res, BlockPos pos) {

    }
}
