package muramasa.antimatter.structure;

import com.google.common.collect.ImmutableMap;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import it.unimi.dsi.fastutil.Pair;
import muramasa.antimatter.blockentity.multi.BlockEntityBasicMultiMachine;
import muramasa.antimatter.util.int2;
import muramasa.antimatter.util.int3;

import java.util.Map;
import java.util.function.BiFunction;

public class Structure<T extends BlockEntityBasicMultiMachine<T>> {
    private final IStructureDefinition<T> structureDefinition;


    private final Map<String, Pair<Integer, Integer>> minMaxMap;

    private final Map<String, Pair<int2, BiFunction<Integer, int3, int3>>> partRequirements;
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

    /*public abstract StructureResult evaluate(@NotNull BlockEntityBasicMultiMachine<?> tile);

    public abstract LongList allPositions(BlockEntityBasicMultiMachine<?> tile);*/

    /*public boolean evaluatePosition(@NotNull StructureResult res, @NotNull BlockEntityBasicMultiMachine<?> tile, @NotNull BlockPos pos) {
        StructureElement el = res.get(pos);
        if (el != null) {
            return el.evaluate(tile, new int3(pos.getX(), pos.getY(), pos.getZ()), res);
        }
        return true;
    }*/

    public interface StructurePartCheckCallback<T extends BlockEntityBasicMultiMachine<T>> {
        boolean check(IStructureDefinition<T> structureDefinition, T tile, String part, int i, int3 newOffset);
    }

    /*public Iterator<Point> forAllElements(@NotNull BlockPos source, @NotNull Direction facing, @Nullable Direction hFacing) {
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
