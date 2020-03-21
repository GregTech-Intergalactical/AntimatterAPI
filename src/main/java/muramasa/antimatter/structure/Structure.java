package muramasa.antimatter.structure;

import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.int2;
import muramasa.antimatter.util.int3;
import net.minecraft.util.Tuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static muramasa.antimatter.util.Dir.*;

public class Structure {

    private ArrayList<Tuple<int3, StructureElement>> elements;
    private HashMap<String, IRequirement> requirements = new HashMap<>();
    private int3 size;
    private int2 offset = new int2();

    public Structure(int3 size, ArrayList<Tuple<int3, StructureElement>> elements) {
        this.size = size;
        this.elements = elements;
    }

    public Structure offset(int x, int y) {
        offset.set(x, y);
        return this;
    }

    public Structure exact(int i, IAntimatterObject... objects) {
        Arrays.stream(objects).forEach(o -> addReq(o.getId(), (c, s) -> (c.containsKey(o.getId()) && c.get(o.getId()).size() == i) || (s.containsKey(o.getId()) && s.get(o.getId()).size() == i)));
        return this;
    }

    public Structure min(int i, IAntimatterObject... objects) {
        Arrays.stream(objects).forEach(o -> addReq(o.getId(), (c, s) -> (c.containsKey(o.getId()) && c.get(o.getId()).size() >= i) || (s.containsKey(o.getId()) && s.get(o.getId()).size() >= i)));
        return this;
    }

    public Structure addReq(String id, IRequirement req) {
        requirements.put(id, req);
        return this;
    }

    public ArrayList<Tuple<int3, StructureElement>> getElements() {
        return elements;
    }

    public HashMap<String, IRequirement> getRequirements() {
        return requirements;
    }

    public StructureResult evaluate(TileEntityMachine tile) {
        StructureResult result = new StructureResult(this);
        Tuple<int3, StructureElement> element;
        int3 corner = new int3(tile.getPos(), tile.getFacing()).left(size.getX() / 2).back(offset.x).up(offset.y);
        int3 working = new int3();
        int elementSize = elements.size();
        for (int i = 0; i < elementSize; i++) {
            element = elements.get(i);
            working.set(corner).offset(element.getA(), RIGHT, UP, FORWARD);
            if (!element.getB().evaluate(tile, working, result)) {
                return result;
            }
        }
        return result;
    }
}
