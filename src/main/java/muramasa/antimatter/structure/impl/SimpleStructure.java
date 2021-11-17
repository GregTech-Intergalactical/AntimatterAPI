package muramasa.antimatter.structure.impl;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.structure.Structure;
import muramasa.antimatter.structure.StructureElement;
import muramasa.antimatter.structure.StructureResult;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import muramasa.antimatter.util.int2;
import muramasa.antimatter.util.int3;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static muramasa.antimatter.util.Dir.*;

public class SimpleStructure extends Structure {

    private final int3 size;
    private final int2 offset = new int2();
    private final ImmutableMap<int3, StructureElement> elements;
    public SimpleStructure(int3 size, ImmutableMap<int3, StructureElement> elements) {
        this.elements = elements;
        this.size = size;
    }

    public SimpleStructure offset(int x, int y) {
        offset.set(x,y);
        return this;
    }

    @Override
    public List<BlockPos> allShared(StructureElement element, TileEntityBasicMultiMachine<?> tile) {
        Direction h = null;
        List<BlockPos> ret = new ObjectArrayList<>();
        if (tile.getMachineType().allowVerticalFacing() && tile.getFacing().getAxis() == Direction.Axis.Y) {
            h = tile.getBlockState().get(BlockMachine.HORIZONTAL_FACING);
        }
        Direction finalH = h;
        Iterable<Point> iter = () -> this.forAllElements(tile.getPos(), tile.getFacing(), finalH);
        for (Point point : iter) {
            if (point.el.equals(element)) ret.add(point.pos.toImmutable());
        }
        return ret;
    }

    @Override
    public StructureResult evaluate(@Nonnull TileEntityBasicMultiMachine<?> tile) {
        StructureResult result = new StructureResult(this);
        Direction h = null;
        if (tile.getMachineType().allowVerticalFacing() && tile.getFacing().getAxis() == Direction.Axis.Y) {
            h = tile.getBlockState().get(BlockMachine.HORIZONTAL_FACING);
        }
        for (Iterator<Point> it = forAllElements(tile.getPos(), tile.getFacing(), h); it.hasNext(); ) {
            Point point = it.next();
            if (!point.el.evaluate(tile, point.pos, result)) {
                return result;
            } else {
                result.register(point.pos.toImmutable(), point.el);
            }
        }
        return result;
    }

    @Override
    public LongList allPositions(TileEntityBasicMultiMachine<?> tile) {
        LongList l = new LongArrayList();
        Direction h = null;
        if (tile.getMachineType().allowVerticalFacing() && tile.getFacing().getAxis() == Direction.Axis.Y) {
            h = tile.getBlockState().get(BlockMachine.HORIZONTAL_FACING);
        }
        for (Iterator<Point> it = forAllElements(tile.getPos(), tile.getFacing(), h); it.hasNext(); ) {
            l.add(it.next().pos.toLong());
        }
        return l;
    }

    @Override
    public int3 size() {
        return size;
    }

    @Override
    public int2 offset() {
        return offset;
    }

    public Iterator<Point> forAllElements(@Nonnull BlockPos source, @Nonnull Direction facing, @Nullable Direction hFacing) {
        return new Iterator<Point>() {
            final int3 corner = hFacing == null ? new int3(source, facing).left(size().getX() / 2).back(offset().x).up(offset().y) : new int3(source, facing, hFacing).left(size().getX() / 2).back(offset().x).up(offset().y);
            final int3 working = new int3(facing, hFacing);
            final Point point = new Point();
            final Iterator<Map.Entry<int3, StructureElement>> it = elements.entrySet().iterator();

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public Point next() {
                Map.Entry<int3, StructureElement> next = it.next();
                working.set(corner).offset(next.getKey(), RIGHT, UP, FORWARD);
                point.el = next.getValue();
                point.pos = working;
                return point;
            }
        };
    }
}
