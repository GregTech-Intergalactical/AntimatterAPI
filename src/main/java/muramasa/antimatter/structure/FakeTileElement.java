package muramasa.antimatter.structure;

import muramasa.antimatter.Data;
import muramasa.antimatter.cover.CoverFactory;
import muramasa.antimatter.tile.TileEntityFakeBlock;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import muramasa.antimatter.util.int3;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

/**
 * FakeTileElement represents a fake block for this multiblock. It takes on the
 * appearance of another block
 * as well as rendering possible covers on it. It also forwards capability calls
 * to the master controller.
 * (In the case of multiple controllers, it returns the first one that is
 * non-empty).
 */
public class FakeTileElement extends StructureElement {

    private final IBlockStatePredicate[] preds;
    private final EnumMap<Direction, CoverFactory> covers = new EnumMap<>(Direction.class);

    public FakeTileElement(IBlockStatePredicate... pred) {
        this.preds = pred;
    }

    public FakeTileElement(Block... pred) {
        this.preds = Arrays
                .stream(pred).map(t -> (IBlockStatePredicate) (reader, pos,
                                                               state) -> state.getBlock() == Data.PROXY_INSTANCE || state.getBlock().is(t))
                .toArray(IBlockStatePredicate[]::new);
    }

    public FakeTileElement(BlockState... pred) {
        this.preds = Arrays
                .stream(pred).map(t -> (IBlockStatePredicate) (reader, pos,
                        state) -> state.getBlock() == Data.PROXY_INSTANCE || state.equals(t))
                .toArray(IBlockStatePredicate[]::new);
    }

    public FakeTileElement() {
        this.preds = new IBlockStatePredicate[0];
    }

    @Override
    public boolean evaluate(TileEntityBasicMultiMachine<?> machine, int3 pos, StructureResult result) {
        BlockState state = machine.getLevel().getBlockState(pos);
        if (state.getBlock().is(Data.PROXY_INSTANCE)) {
            TileEntity tile = machine.getLevel().getBlockEntity(pos);
            if (tile instanceof TileEntityFakeBlock) {
                BlockState st = ((TileEntityFakeBlock) tile).getState();
                if (st == null) {
                    result.withError("Missing state in fake tile.");
                    return false;
                }
                for (IBlockStatePredicate pred : preds) {
                    if (pred.evaluate(machine.getLevel(), pos, st)) {
                        result.addState("fake", pos, st);
                        return true;
                    }
                }
                if (preds.length == 0) {
                    result.addState("fake", pos, st);
                    return true;
                }
            }
            result.withError("Invalid BlockProxy state.");
            return false;
        } else if (StructureCache.refCount(machine.getLevel(), pos) > 0) {
            result.withError("FakeTile sharing a block that is not of proxy type.");
            return false;
        }
        if (state.hasTileEntity()) {
            result.withError("BlockProxy replacement should not have Tile.");
            return false;
        }
        if (preds.length == 0) {
            result.addState("fake", pos, state);
            return true;
        }
        for (IBlockStatePredicate pred : preds) {
            if (pred.evaluate(machine.getLevel(), pos, state)) {
                result.addState("fake", pos, state);
                return true;
            }
        }
        result.withError("No matching blocks for FakeTile");
        return false;
    }

    public FakeTileElement cover(Direction side, CoverFactory cover) {
        this.covers.put(side, cover);
        return this;
    }

    @Override
    public void onBuild(TileEntityBasicMultiMachine machine, BlockPos pos, StructureResult result, int count) {
        World world = machine.getLevel();
        BlockState oldState = world.getBlockState(pos);
        // Already set.
        if (count > 1 || oldState.getBlock().is(Data.PROXY_INSTANCE)) {
            ((TileEntityFakeBlock) world.getBlockEntity(pos)).addController(machine);
            return;
        }
        world.setBlock(pos, Data.PROXY_INSTANCE.defaultBlockState(), 2 | 8);
        TileEntityFakeBlock tile = (TileEntityFakeBlock) world.getBlockEntity(pos);
        tile.setState(oldState).setFacing(machine.getFacing()).setCovers(covers);
        tile.addController(machine);
        super.onBuild(machine, pos, result, count);
    }

    @Override
    public void onInfoTooltip(List<ITextComponent> text, long count, TileEntityBasicMultiMachine<?> machine) {
        super.onInfoTooltip(text, count, machine);
        text.add(new StringTextComponent("Element replaced with a TileEntity to allow input/output."));
    }

    @Override
    public void onRemove(TileEntityBasicMultiMachine machine, BlockPos pos, StructureResult result, int count) {
        World world = machine.getLevel();
        TileEntity tile = world.getBlockEntity(pos);
        if (!(tile instanceof TileEntityFakeBlock))
            return;
        if (count == 0) {
            BlockState state = ((TileEntityFakeBlock) tile).getState();
            world.setBlock(pos, state, 1 | 2 | 8);
            return;
        } else {
            ((TileEntityFakeBlock) tile).removeController(machine);
        }
        super.onRemove(machine, pos, result, count);
    }
}
