package muramasa.gtu.api.structure;

import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.api.util.int3;
import net.minecraft.block.state.IBlockState;

public class BlockStateElement extends StructureElement {

    protected IBlockStatePredicate predicate;

    public BlockStateElement(String elementId, IBlockStatePredicate predicate) {
        super(elementId);
        this.predicate = predicate;
    }

    public IBlockStatePredicate getPredicate() {
        return predicate;
    }

    @Override
    public boolean evaluate(TileEntityMachine machine, int3 pos, StructureResult result) {
        IBlockState state = machine.getWorld().getBlockState(pos.asBP());
        if (predicate.evaluate(machine.getWorld(), pos.asBP(), state)) {
            result.addState(elementId, pos.asBP(), state);
            return true;
        }
        result.withError("No valid state found @" + pos);
        return false;
    }
}
