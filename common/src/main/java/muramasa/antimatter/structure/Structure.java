package muramasa.antimatter.structure;

import com.google.common.collect.ImmutableMap;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import muramasa.antimatter.util.int2;
import muramasa.antimatter.util.int3;
import net.minecraft.core.BlockPos;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class Structure<T extends TileEntityBasicMultiMachine<T>> {
    private final IStructureDefinition<T> structureDefinition;


    private final Map<String, Pair<Integer, Integer>> minMaxMap;

    private final Map<String, Pair<int2, BiFunction<Integer, int3, int3>>> partRequirements;

    private final Map<String, IRequirement> requirements = new Object2ObjectOpenHashMap<>();
    private final int3 offset;
    StructurePartCheckCallback<T> callback;

    protected Structure(IStructureDefinition<T> structureDefinition, ImmutableMap<String, Pair<int2, BiFunction<Integer, int3, int3>>> partRequirements, ImmutableMap<String, Pair<Integer, Integer>> minMaxMap, int3 offset, StructurePartCheckCallback<T> callback) {
        this.structureDefinition = structureDefinition;
        this.partRequirements = partRequirements;
        this.minMaxMap = minMaxMap;
        this.offset = offset;
        this.callback = callback;
    }

    public IStructureDefinition<T> getStructureDefinition() {
        return structureDefinition;
    }

    public Map<String, Pair<Integer, Integer>> getMinMaxMap() {
        return minMaxMap;
    }

    public boolean check(T tile){
        int i = 0;
        int successful = 0;
        for (Map.Entry<String, Pair<int2, BiFunction<Integer, int3, int3>>> entry : partRequirements.entrySet()) {
            String s = entry.getKey();
            Pair<int2, BiFunction<Integer, int3, int3>> v = entry.getValue();
            for (int j = 0; j < v.left().y; j++) {
                int3 newOffset = v.right().apply(i, offset.copy());
                boolean success = callback.check(structureDefinition, tile, s, i, newOffset);
                if (success){
                    successful++;
                }
                if (j >= v.left().x && !success) {
                    break;
                }
                i++;
            }
        }
        return i == successful;
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

    public interface StructurePartCheckCallback<T extends TileEntityBasicMultiMachine<T>> {
        boolean check(IStructureDefinition<T> structureDefinition, T tile, String part, int i, int3 newOffset);
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
