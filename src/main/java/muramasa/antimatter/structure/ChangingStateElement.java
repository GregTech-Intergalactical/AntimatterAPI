package muramasa.antimatter.structure;

import muramasa.antimatter.Data;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import muramasa.antimatter.util.int3;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.BiFunction;

public class ChangingStateElement extends StructureElement {
    private final IBlockStatePredicate regular;
    private final IBlockStatePredicate built;
    private final BiFunction<MachineState, BlockState, BlockState> builder;

    public static final ChangingStateElement TEST = new ChangingStateElement((a,b,c) -> c == Data.ANDESITE.getState(), (a,b,c) -> c == Data.GRANITE.getState() || c == Data.BLACKSTONE.getState(), (s,b) -> s == MachineState.ACTIVE ? Data.BLACKSTONE.getState() : ( s ==MachineState.IDLE ? Data.GRANITE.getState() : Data.ANDESITE.getState()));

    /**
     * ChangingStateElement allows you to vary the block state between machine texture states(Idle, Active, Invalid Structure).
     * @param defaultValidator validator for INVALID_STRUCTURE.
     * @param builtValidator validator for all other states (Idle, Active).
     * @param builder the builder to build a blockstate to place in the world.
     */
    public ChangingStateElement(IBlockStatePredicate defaultValidator, IBlockStatePredicate builtValidator, BiFunction<MachineState, BlockState, BlockState> builder) {
        this.regular = defaultValidator;
        this.built = builtValidator;
        this.builder = builder;
    }

    @Override
    public boolean evaluate(TileEntityMachine machine, int3 pos, StructureResult result) {
        BlockState state = machine.getWorld().getBlockState(pos);
        IBlockStatePredicate pred = machine.getMachineState() == MachineState.INVALID_STRUCTURE ? regular : built;
        if (pred.evaluate(machine.getWorld(), pos, machine.getWorld().getBlockState(pos))) {
            result.addState("changing", pos, state);
            return true;
        }
        return super.evaluate(machine, pos, result);
    }

    @Override
    public void onBuild(TileEntityBasicMultiMachine machine, BlockPos pos, StructureResult result, int count) {
        super.onBuild(machine, pos, result, count);
        if (count > 1) return;
        World world = machine.getWorld();

        world.setBlockState(pos, builder.apply(MachineState.IDLE, world.getBlockState(pos)), 2 | 8);
    }

    @Override
    public void onRemove(TileEntityBasicMultiMachine machine, BlockPos pos, StructureResult result, int count) {
        super.onRemove(machine, pos, result, count);
        if (count == 0)  {
            World world = machine.getWorld();
            world.setBlockState(pos, builder.apply(MachineState.INVALID_STRUCTURE, world.getBlockState(pos)), 2 | 8);
        }
    }

    @Override
    public void onStateChange(TileEntityBasicMultiMachine machine, MachineState newState, BlockPos pos, StructureResult result, int count) {
        super.onStateChange(machine, newState, pos, result, count);
        World world = machine.getWorld();
        BlockState bs = builder.apply(newState, world.getBlockState(pos));
        if (!bs.equals(world.getBlockState(pos))) {
            world.setBlockState(pos, bs, 2 | 8);
        }
    }
}
