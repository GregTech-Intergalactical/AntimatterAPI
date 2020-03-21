package muramasa.antimatter.structure;

import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.int3;
import net.minecraft.block.BlockState;

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
        BlockState state = machine.getWorld().getBlockState(pos);
        if (predicate.evaluate(machine.getWorld(), pos, state)) {
            result.addState(elementId, pos, state);
            return true;
        }
        result.withError("No valid state found @" + pos);
        return false;
    }
}
