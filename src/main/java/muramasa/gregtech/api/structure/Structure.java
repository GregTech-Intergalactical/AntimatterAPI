package muramasa.gregtech.api.structure;

import muramasa.gregtech.api.tileentities.TileEntityMachine;
import muramasa.gregtech.api.util.int2;
import muramasa.gregtech.api.util.int3;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Tuple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.BiPredicate;

import static muramasa.gregtech.api.util.Dir.*;

public class Structure {

    private ArrayList<Tuple<int3, StructureElement>> elements;
    private HashMap<String, Tuple<Integer, BiPredicate<Integer, Integer>>> requirements = new HashMap<>();
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

    public Structure exact(IStringSerializable serializable, int value) {
        return addReq(serializable, value, StructureResult::equal);
    }

    public Structure min(IStringSerializable serializable, int value) {
        return addReq(serializable, value, StructureResult::moreOrEqual);
    }

    public Structure addReq(IStringSerializable serializable, int value, BiPredicate<Integer, Integer> method) {
        requirements.put(serializable.getName(), new Tuple<>(value, method));
        return this;
    }

    public boolean testRequirement(String componentName, int value) {
        Tuple<Integer, BiPredicate<Integer, Integer>> tuple = requirements.get(componentName);
        return tuple != null && tuple.getSecond().test(value, tuple.getFirst());
    }

    public Collection<String> getRequirements() {
        return requirements.keySet();
    }

    public StructureResult evaluate(TileEntityMachine tile) {
        StructureResult result = new StructureResult(this);
        Tuple<int3, StructureElement> element;
        int3 corner = new int3(tile.getPos(), tile.getEnumFacing()).left(size.x / 2).back(offset.x).up(offset.y);
        int3 working = new int3();
        for (int i = 0; i < elements.size(); i++) {
            element = elements.get(i);
            working.set(corner).offset(element.getFirst(), RIGHT, UP, FORWARD);
            if (!element.getSecond().evaluate(tile, working, result)) {
                return result;
            }
        }
        return result;
    }
}
