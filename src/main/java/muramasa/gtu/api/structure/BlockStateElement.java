package muramasa.gtu.api.structure;

import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.api.util.int3;

public class BlockStateElement extends StructureElement {

    protected IBlockStatePredicate predicate;

    public BlockStateElement(String elementId, IBlockStatePredicate test) {
        super(elementId);
        this.predicate = test;
    }

    public IBlockStatePredicate getPredicate() {
        return predicate;
    }

    @Override
    public boolean evaluate(TileEntityMachine machine, int3 pos, StructureResult result) {
        if (predicate.evaluate(machine.getWorld(), pos.asBP(), machine.getWorld().getBlockState(pos.asBP()))) {
            result.addState(elementId, pos.asBP());
            return true;
        }
        result.withError("No valid state found @" + pos);
        return false;
    }
}
