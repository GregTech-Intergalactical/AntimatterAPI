package muramasa.antimatter.structure;

import com.google.common.collect.ImmutableMap;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import muramasa.antimatter.util.int2;
import muramasa.antimatter.util.int3;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Structure<T extends TileEntityBasicMultiMachine<T>> {
    private final IStructureDefinition<T> structureDefinition;


    private final Map<String, Pair<Integer, Integer>> minMaxMap;

    private final Map<String, Triple<Integer, Integer, Direction.Axis>> partRequirements;

    private final Map<String, IRequirement> requirements = new Object2ObjectOpenHashMap<>();

    protected Structure(IStructureDefinition<T> structureDefinition, ImmutableMap<String, Triple<Integer, Integer, Direction.Axis>> partRequirements, ImmutableMap<String, Pair<Integer, Integer>> minMaxMap) {
        this.structureDefinition = structureDefinition;
        this.partRequirements = partRequirements;
        this.minMaxMap = minMaxMap;
    }

    public IStructureDefinition<T> getStructureDefinition() {
        return structureDefinition;
    }

    public Map<String, Pair<Integer, Integer>> getMinMaxMap() {
        return minMaxMap;
    }

    public Map<String, Triple<Integer, Integer, Direction.Axis>> getPartRequirements() {
        return partRequirements;
    }

    public boolean check(T tile){

        return false;
    }

    /*public abstract StructureResult evaluate(@Nonnull TileEntityBasicMultiMachine<?> tile);

    public abstract LongList allPositions(TileEntityBasicMultiMachine<?> tile);*/

    public List<BlockPos> allShared(StructureElement element, TileEntityBasicMultiMachine<?> tile) {
        return Collections.emptyList();
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
