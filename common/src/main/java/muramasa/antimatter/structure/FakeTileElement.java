package muramasa.antimatter.structure;

import com.gtnewhorizon.structurelib.structure.IStructureElement;
import muramasa.antimatter.cover.CoverFactory;
import muramasa.antimatter.tile.TileEntityFakeBlock;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumMap;
import java.util.List;

/**
 * FakeTileElement represents a fake block for this multiblock. It takes on the
 * appearance of another block
 * as well as rendering possible covers on it. It also forwards capability calls
 * to the master controller.
 */
public class FakeTileElement<T extends TileEntityBasicMultiMachine<T>> implements IStructureElement<T> {

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

    public FakeTileElement cover(Direction side, CoverFactory cover) {
        this.covers.put(side, cover);
        return this;
    }

    @Override
    public void onStructureSuccess(T machine, Level world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof TileEntityFakeBlock fakeBlock && StructureCache.refCount(world, pos) == 0){
            fakeBlock.setFacing(machine.getFacing()).setCovers(covers);
            fakeBlock.setController(machine);
        }
    }

    @Override
    public void onStructureFail(T basicMultiMachine, Level world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        if (world.isLoaded(pos)){
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof TileEntityFakeBlock fakeBlock){
                fakeBlock.setController(null);
            }
        }
    }

    public void onInfoTooltip(List<Component> text, long count, TileEntityBasicMultiMachine<?> machine) {
        //super.onInfoTooltip(text, count, machine);
        text.add(new TextComponent("Element replaced with a TileEntity to allow input/output."));
    }

    @Override
    public boolean check(T machine, Level world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        BlockState state = world.getBlockState(pos);
        if (pred.evaluate(machine.getLevel(), pos, state)) {
            BlockEntity tile = world.getBlockEntity(pos);
            if (tile instanceof TileEntityFakeBlock) {
                return StructureCache.refCount(world, pos) == 0;
            }
            return false;
        }
        return false;
    }

    @Override
    public boolean spawnHint(T basicMultiMachine, Level world, int x, int y, int z, ItemStack trigger) {
        return false;
    }

    @Override
    public boolean placeBlock(T basicMultiMachine, Level world, int x, int y, int z, ItemStack trigger) {
        if (state != null){
            world.setBlock(new BlockPos(x, y, z), state, 3);
            return true;
        }
        return false;
    }
}
