package muramasa.antimatter.structure;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import muramasa.antimatter.util.int2;
import muramasa.antimatter.util.int3;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static muramasa.antimatter.util.Dir.*;

public abstract class Structure {

    private final Map<String, IRequirement> requirements = new Object2ObjectOpenHashMap<>();

    public abstract StructureResult evaluate(@Nonnull TileEntityBasicMultiMachine<?> tile);

    public abstract LongList allPositions(TileEntityBasicMultiMachine<?> tile);

    public abstract int3 size();

    public abstract int2 offset();

    public Structure exact(int i, IAntimatterObject... objects) {
        Arrays.stream(objects).forEach(o -> addReq(o.getId(), r -> (r.components.containsKey(o.getId()) && r.components.get(o.getId()).size() == i) || (r.states.containsKey(o.getId()) && r.states.get(o.getId()).size() == i)));
        return this;
    }

    public Structure min(int i, IAntimatterObject... objects) {
        Arrays.stream(objects).forEach(o -> addReq(o.getId(), r -> (r.components.containsKey(o.getId()) && r.components.get(o.getId()).size() >= i) || (r.states.containsKey(o.getId()) && r.states.get(o.getId()).size() >= i)));
        return this;
    }

    public Structure addReq(String id, IRequirement req) {
        requirements.put(id, req);
        return this;
    }

    public Map<String, IRequirement> getRequirements() {
        return requirements;
    }

    public boolean evaluatePosition(@Nonnull StructureResult res, @Nonnull TileEntityBasicMultiMachine<?> tile, @Nonnull BlockPos pos) {
        StructureElement el = res.get(pos);
        if (el != null) {
            return el.evaluate(tile, new int3(pos.getX(), pos.getY(), pos.getZ()), res);
        }
        return true;
    }

    public static class Point {
        public int3 pos = new int3();
        public StructureElement el;
    }

    /*public Iterator<Point> forAllElements(@Nonnull BlockPos source, @Nonnull Direction facing, @Nullable Direction hFacing) {
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
                point.offset = next.getKey();
                point.pos = working;
                return point;
            }
        };
    }*/
}
