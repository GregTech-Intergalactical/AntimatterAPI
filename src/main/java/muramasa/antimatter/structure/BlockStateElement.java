package muramasa.antimatter.structure;

import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import muramasa.antimatter.util.int3;
import net.minecraft.block.BlockState;

public class BlockStateElement extends StructureElement {

    public static BlockStateElement AIR = new BlockStateElement("air", (r, p, s) -> s.isAir(r, p)); //Air check
    protected IBlockStatePredicate predicate;

    public BlockStateElement(String elementId, IBlockStatePredicate predicate) {
        super(elementId);
        this.predicate = predicate;
    }

    public IBlockStatePredicate getPredicate() {
        return predicate;
    }

    @Override
    public boolean evaluate(TileEntityBasicMultiMachine<?> machine, int3 pos, StructureResult result) {
        BlockState state = machine.getLevel().getBlockState(pos);
        if (predicate.evaluate(machine.getLevel(), pos, state)) {
            result.addState(elementId, pos, state);
            return true;
        }
        result.withError("No valid state found @" + pos);
        return false;
    }
}
