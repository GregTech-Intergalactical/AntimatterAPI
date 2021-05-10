package muramasa.antimatter.structure;

import muramasa.antimatter.Data;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.tile.TileEntityFakeBlock;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import muramasa.antimatter.util.int3;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.EnumMap;

public class FakeTileElement extends StructureElement {

    private final IBlockStatePredicate[] preds;
    private EnumMap<Direction, ICover> covers = new EnumMap<>(Direction.class);

    public FakeTileElement(IBlockStatePredicate... pred) {
        this.preds = pred;
    }

    public FakeTileElement(Block... pred) {
        this.preds = Arrays.stream(pred).map(t -> (IBlockStatePredicate) (reader, pos, state) -> reader.getBlockState(pos).getBlock().matchesBlock(t)).toArray(IBlockStatePredicate[]::new);
    }

    public FakeTileElement(BlockState... pred) {
        this.preds = Arrays.stream(pred).map(t -> (IBlockStatePredicate) (reader, pos, state) -> reader.getBlockState(pos).equals(t)).toArray(IBlockStatePredicate[]::new);
    }


    public FakeTileElement() {
        this.preds = new IBlockStatePredicate[0];
    }

    @Override
    public boolean evaluate(TileEntityMachine machine, int3 pos, StructureResult result) {
        BlockState state = machine.getWorld().getBlockState(pos);
        if (state.getBlock().matchesBlock(Data.PROXY_INSTANCE)) {
            result.addState("fake", pos, state);
            return true;
        }
        if (state.hasTileEntity()) return false;
        if (preds.length == 0) {
            result.addState("fake", pos, state);
            return true;
        }
        for (IBlockStatePredicate pred : preds) {
            if (pred.evaluate((IWorldReader)machine.getWorld(), (BlockPos) pos, state)) {
                result.addState("fake", pos, state);
                return true;
            }
        }
        return false;
    }

    public FakeTileElement cover(Direction side, ICover cover) {
        this.covers.put(side, cover);
        return this;
    }

    @Override
    public void onBuild(TileEntityBasicMultiMachine machine, BlockPos pos, StructureResult result, int count) {
        World world = machine.getWorld();
        BlockState oldState = world.getBlockState(pos);
        //Already set.
        if (count > 1 || oldState.getBlock().matchesBlock(Data.PROXY_INSTANCE)) {
            ((TileEntityFakeBlock) world.getTileEntity(pos)).addController(machine);
            return;
        }
        world.setBlockState(pos, Data.PROXY_INSTANCE.getDefaultState(), 2 | 8);
        TileEntityFakeBlock tile = (TileEntityFakeBlock) world.getTileEntity(pos);
        tile.setState(oldState).setFacing(machine.getFacing()).setCovers(covers);
        tile.addController(machine);
        super.onBuild(machine, pos, result, count);
    }

    @Override
    public void onRemove(TileEntityBasicMultiMachine machine, BlockPos pos, StructureResult result, int count) {
        World world = machine.getWorld();
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileEntityFakeBlock)) return;
        if (count == 0) {
            BlockState state = ((TileEntityFakeBlock)tile).getState();
            world.setBlockState(pos, state, 1 | 2 | 8);
            return;
        } else {
            ((TileEntityFakeBlock)tile).removeController(machine);
        }
        super.onRemove(machine, pos, result, count);
    }
}
