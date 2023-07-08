package muramasa.antimatter.structure.impl;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.structure.Structure;
import muramasa.antimatter.structure.StructureElement;
import muramasa.antimatter.structure.StructureResult;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import muramasa.antimatter.util.int2;
import muramasa.antimatter.util.int3;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static muramasa.antimatter.util.Dir.FORWARD;
import static muramasa.antimatter.util.Dir.RIGHT;
import static muramasa.antimatter.util.Dir.UP;

public class SimpleStructure {

    private final int3 size;
    private final int2 offset = new int2();
    private final ImmutableMap<int3, StructureElement> elements;
    private final Set<Direction> allowedFacings;

    public SimpleStructure(int3 size, ImmutableMap<int3, StructureElement> elements, Set<Direction> facings) {
        this.elements = elements;
        this.size = size;
        this.allowedFacings = facings;
    }

    public SimpleStructure offset(int x, int y) {
        offset.set(x,y);
        return this;
    }

    /*@Override
    public List<BlockPos> allShared(StructureElement element, TileEntityBasicMultiMachine<?> tile) {
        List<BlockPos> ret = new ObjectArrayList<>();
        Iterable<Point> iter = () -> this.forAllElements(tile.getBlockPos(), tile.getFacing());
        for (Point point : iter) {
            if (point.el.equals(element)) ret.add(point.pos.immutable());
        }
        return ret;
    }

    @Override
    public StructureResult evaluate(@Nonnull TileEntityBasicMultiMachine<?> tile) {
        StructureResult result = new StructureResult(this);
        if (!allowedFacings.contains(tile.getFacing())) {
            result.withError("Invalid facing in machine");
            return result;
        }
        for (Iterator<Point> it = forAllElements(tile.getBlockPos(), tile.getFacing()); it.hasNext(); ) {
            Point point = it.next();
            if (!point.el.evaluate(tile, point.pos, result)) {
                return result;
            } else {
                result.register(point.pos.immutable(), point.el);
            }
        }
        return result;
    }

    @Override
    public LongList allPositions(TileEntityBasicMultiMachine<?> tile) {
        LongList l = new LongArrayList();
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

    public Iterator<Point> forAllElements(@Nonnull BlockPos source, @Nonnull Direction facing) {
        return new Iterator<>() {
            final int3 corner = new int3(source, facing).left(size().getX() / 2).back(offset().x).above(offset().y);
            final int3 working = new int3(facing);
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
    }*/
}
