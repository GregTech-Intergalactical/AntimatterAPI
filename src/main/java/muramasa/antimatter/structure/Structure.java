package muramasa.antimatter.structure;

import muramasa.antimatter.alignment.IAlignmentProvider;
import muramasa.antimatter.alignment.enumerable.ExtendedFacing;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.tileentities.TileEntityMachine;
import muramasa.antimatter.util.IntegerAxisSwap;
import muramasa.antimatter.util.int3;
import net.minecraft.util.Tuple;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static muramasa.antimatter.util.Dir.*;

public class Structure {

    private List<Tuple<int3, StructureElement>> elements;
    private Map<String, IRequirement> requirements = new HashMap<>();
    private int3 size;
    private int3 offset = new int3();

    public Structure(int3 size, List<Tuple<int3, StructureElement>> elements) {
        this.size = size;
        this.elements = elements;
    }

    public Structure offset(int x, int y) {
        offset.set(x, y,size.x / 2);
        return this;
    }

    public Structure offset(int x, int y,int z) {
        offset.set(x, y, z);
        return this;
    }

    public Structure exact(int i, IAntimatterObject... objects) {
        Arrays.stream(objects).forEach(o ->
                addReq(o.getId(), (c, s) ->
                        c.containsKey(o.getId()) && c.get(o.getId()).size() == i ||
                        s.containsKey(o.getId()) && s.get(o.getId()).size() == i
                ));
        return this;
    }

    public Structure min(int i, IAntimatterObject... objects) {
        Arrays.stream(objects).forEach(o ->
                addReq(o.getId(), (c, s) ->
                        c.containsKey(o.getId()) && c.get(o.getId()).size() >= i ||
                        s.containsKey(o.getId()) && s.get(o.getId()).size() >= i
                ));
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
        int3 corner = new int3(tile.getPos(), tile.getFacing()).left(offset.z).back(offset.x).up(offset.y);
        int3 worldPos = new int3();
        for (Tuple<int3, StructureElement> element : elements) {
            worldPos.set(corner).offset(element.getA(), RIGHT, UP, FORWARD);
            if (!element.getB().evaluate(tile, worldPos, result)) {
                return result;
            }
        }
        return result;
    }

    //todo
    public StructureResult evaluateAligned(TileEntityMachine tile) {
        StructureResult result = new StructureResult(this);
        //made to conform to L-->R U-->D F-->B facing relative axises ABC
        int3 abcPos = new int3();
        //todo consider IAlignmentProvider as implemented by all TEMshops https://www.youtube.com/watch?v=Gu8A8uWAJaA
        IntegerAxisSwap integerAxisSwap = (tile instanceof IAlignmentProvider?
                ((IAlignmentProvider) tile).getAlignment().getExtendedFacing():
                ExtendedFacing.of(tile.getFacing())).getIntegerAxisSwap();
        for (Tuple<int3, StructureElement> element : elements) {
            abcPos.set(element.getA()).sub(offset);
            if (!element.getB().evaluate(tile, integerAxisSwap.translate(abcPos).add(tile.getPos()), result)) {
                return result;
            }
        }
        return result;
    }
}
