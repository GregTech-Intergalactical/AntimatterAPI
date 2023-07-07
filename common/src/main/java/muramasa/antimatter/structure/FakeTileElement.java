package muramasa.antimatter.structure;

import com.gtnewhorizon.structurelib.structure.IStructureElement;
import muramasa.antimatter.cover.CoverFactory;
import muramasa.antimatter.tile.TileEntityFakeBlock;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import muramasa.antimatter.util.int3;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

/**
 * FakeTileElement represents a fake block for this multiblock. It takes on the
 * appearance of another block
 * as well as rendering possible covers on it. It also forwards capability calls
 * to the master controller.
 */
public class FakeTileElement implements IStructureElement<TileEntityBasicMultiMachine<?>> {

    private IBlockStatePredicate pred;
    private BlockState state;
    private final EnumMap<Direction, CoverFactory> covers = new EnumMap<>(Direction.class);

    public FakeTileElement(IBlockStatePredicate pred) {
        this.pred = pred;
    }

    public FakeTileElement(Block pred) {
        this(pred.defaultBlockState());
    }

    public FakeTileElement(BlockState pred) {
        this.state = pred;
        this.pred = (reader, pos, state) -> state == this.state;
    }

    @Override
    public boolean evaluate(TileEntityBasicMultiMachine<?> machine, int3 pos, StructureResult result) {
        BlockState state = machine.getLevel().getBlockState(pos);
        if (pred.evaluate(machine.getLevel(), pos, state)) {
            BlockEntity tile = machine.getLevel().getBlockEntity(pos);
            if (tile instanceof TileEntityFakeBlock fake) {
                if (fake.controller != null && !fake.controller.getBlockPos().equals(machine.getBlockPos())){
                    result.withError("Fake Tile already has controller");
                    return false;
                }
                result.addState("fake", pos, state);
                return true;
            }
            result.withError("Invalid FakeTile state.");
            return false;
        } else if (StructureCache.refCount(machine.getLevel(), pos) > 0) {
            result.withError("FakeTile sharing a block that is not of proxy type.");
            return false;
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
        Level world = machine.getLevel();
        BlockState oldState = world.getBlockState(pos);
        // Already set.
        if (count > 1) {
            return;
        }
        TileEntityFakeBlock tile = (TileEntityFakeBlock) world.getBlockEntity(pos);
        tile.setFacing(machine.getFacing()).setCovers(covers);
        tile.setController(machine);
        super.onBuild(machine, pos, result, count);
    }

    public void onInfoTooltip(List<Component> text, long count, TileEntityBasicMultiMachine<?> machine) {
        //super.onInfoTooltip(text, count, machine);
        text.add(new TextComponent("Element replaced with a TileEntity to allow input/output."));
    }

    @Override
    public void onRemove(TileEntityBasicMultiMachine machine, BlockPos pos, StructureResult result, int count) {
        Level world = machine.getLevel();
        BlockEntity tile = world.getBlockEntity(pos);
        if (!(tile instanceof TileEntityFakeBlock))
            return;
        if (count == 0) {
            return;
        } else {
            ((TileEntityFakeBlock) tile).removeController(machine);
        }
        super.onRemove(machine, pos, result, count);
    }

    @Override
    public boolean check(TileEntityBasicMultiMachine<?> machine, Level world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        BlockState state = world.getBlockState(pos);
        if (pred.evaluate(machine.getLevel(), pos, state)) {
            BlockEntity tile = world.getBlockEntity(pos);
            if (tile instanceof TileEntityFakeBlock fake) {
                return fake.controller == null || fake.controller.getBlockPos().equals(machine.getBlockPos());
            }
            return false;
        } else if (StructureCache.refCount(world, pos) > 0) {
            return false;
        }
        return false;
    }

    @Override
    public boolean spawnHint(TileEntityBasicMultiMachine<?> basicMultiMachine, Level world, int x, int y, int z, ItemStack trigger) {
        return false;
    }

    @Override
    public boolean placeBlock(TileEntityBasicMultiMachine<?> basicMultiMachine, Level world, int x, int y, int z, ItemStack trigger) {
        if (state != null){
            world.setBlock(new BlockPos(x, y, z), state, 3);
            return true;
        }
        return false;
    }
}
