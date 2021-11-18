package muramasa.antimatter.structure;

import muramasa.antimatter.Antimatter;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import muramasa.antimatter.util.Dir;
import muramasa.antimatter.util.int3;
import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class StructureHandle<T extends TileEntityBasicMultiMachine<T>> {
    private final boolean debug = false;
    private final TileEntityBasicMultiMachine<?> source;
    private final List<int3> offsets;
    private final Consumer<T> onRemoval;
    private final Consumer<T> onAdd;
    private final Class<T> clazz;
    @Nullable
    private T object;

    public StructureHandle(Class<T> clazz, TileEntityBasicMultiMachine<?> tile, int3 off, @Nullable Consumer<T> onRemoval, @Nullable Consumer<T> onAdd) {
        this(clazz, tile, Collections.singletonList(off), onRemoval, onAdd);
    }

    public StructureHandle(Class<T> clazz, TileEntityBasicMultiMachine<?> tile, List<int3> off, @Nullable Consumer<T> onRemoval, @Nullable Consumer<T> onAdd) {
        this.source = tile;
        this.offsets = off;
        this.onRemoval = onRemoval;
        this.onAdd = onAdd;
        this.clazz = clazz;
        tile.addStructureHandle(this);
    }

    public TileEntityBasicMultiMachine<?> getSource() {
        return source;
    }

    public void register() {
        BlockState state = source.getBlockState();
        boolean vertical = source.getMachineType().allowVerticalFacing();
        Direction facing = vertical ? state.getValue(BlockStateProperties.FACING) : state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        Direction hFacing = vertical ? state.getValue(BlockMachine.HORIZONTAL_FACING) : null;
        int3 newOff = new int3(facing, hFacing);
        for (int3 offset : offsets) {
            newOff.set(source.getBlockPos()).offset(offset, Dir.RIGHT, Dir.UP, Dir.FORWARD);
            StructureCache.addListener(this, source.getLevel(), newOff);
        }
    }

    public void deregister() {
        BlockState state = source.getBlockState();
        boolean vertical = source.getMachineType().allowVerticalFacing();
        Direction facing = vertical ? state.getValue(BlockStateProperties.FACING) : state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        Direction hFacing = vertical ? state.getValue(BlockMachine.HORIZONTAL_FACING) : null;
        int3 newOff = new int3(facing, hFacing);
        for (int3 offset : offsets) {
            newOff.set(source.getBlockPos()).offset(offset, Dir.RIGHT, Dir.UP, Dir.FORWARD);
            StructureCache.removeListener(this, source.getLevel(), newOff);
        }
    }

    public void structureCacheRemoval() {
        if (debug) Antimatter.LOGGER.debug("removed structure handle");
        T obj = this.object;
        this.object = null;
        if (onRemoval != null) onRemoval.accept(obj);
    }

    public void structureCacheAddition(TileEntity t) {
        if (!clazz.isInstance(t)) return;
        if (debug) Antimatter.LOGGER.debug("added to structure handle");
        this.object = (T) t;
        if (onAdd != null) onAdd.accept(this.object);
    }

    @Nullable
    public T get() {
        return object;
    }
}
