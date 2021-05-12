package muramasa.antimatter.structure;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.int2;
import muramasa.antimatter.util.int3;
import net.minecraft.util.Tuple;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static muramasa.antimatter.util.Dir.*;

public class Structure {

    private List<Tuple<int3, StructureElement>> elements;
    private Object2ObjectMap<String, IRequirement> requirements = new Object2ObjectOpenHashMap<>();
    private int3 size;
    private int2 offset = new int2();

    public Structure(int3 size, List<Tuple<int3, StructureElement>> elements) {
        this.size = size;
        this.elements = elements;
    }

    public Structure offset(int x, int y) {
        offset.set(x, y);
        return this;
    }

    public Structure exact(int i, Object... objects) {
        Arrays.stream(StructureBuilder.getAntiObjects(objects)).forEach(o -> addReq(o.getId(), (c, s) -> (c.containsKey(o.getId()) && c.get(o.getId()).size() == i) || (s.containsKey(o.getId()) && s.get(o.getId()).size() == i)));
        return this;
    }

    public Structure min(int i, Object... objects) {
        Arrays.stream(StructureBuilder.getAntiObjects(objects)).forEach(o -> addReq(o.getId(), (c, s) -> (c.containsKey(o.getId()) && c.get(o.getId()).size() >= i) || (s.containsKey(o.getId()) && s.get(o.getId()).size() >= i)));
        return this;
    }

    public Structure addReq(String id, IRequirement req) {
        requirements.put(id, req);
        return this;
    }

    public List<Tuple<int3, StructureElement>> getElements() {
        return elements;
    }

    public Map<String, IRequirement> getRequirements() {
        return requirements;
    }

    public StructureResult evaluate(TileEntityMachine tile) {
        StructureResult result = new StructureResult(this);
        Tuple<int3, StructureElement> element;
        int3 corner = new int3(tile.getPos(), tile.getFacing()).left(size.getX() / 2).back(offset.x).up(offset.y);
        int3 working = new int3(tile.getFacing());
        for (Tuple<int3, StructureElement> int3StructureElementTuple : elements) {
            element = int3StructureElementTuple;
            working.set(corner).offset(element.getA(), RIGHT, UP, FORWARD);
            if (!element.getB().evaluate(tile, working, result)) {
                return result;
            }
        }
        return result;
    }

    public LongList getStructure(TileEntityMachine tile) {
        LongList l = new LongArrayList();
        Tuple<int3, StructureElement> element;
        int3 corner = new int3(tile.getPos(), tile.getFacing()).left(size.getX() / 2).back(offset.x).up(offset.y);
        int3 working = new int3(tile.getFacing());
        for (Tuple<int3, StructureElement> int3StructureElementTuple : elements) {
            element = int3StructureElementTuple;
            working.set(corner).offset(element.getA(), RIGHT, UP, FORWARD);
            l.add(working.toLong());
        }
        return l;
    }

    public int3 size() {
        return size;
    }

    public int2 getOffset() {
        return offset;
    }
}
