package muramasa.gregtech.api.structure;

import muramasa.gregtech.api.util.Dir;
import muramasa.gregtech.api.util.Pair;
import muramasa.gregtech.api.util.int2;
import muramasa.gregtech.api.util.int3;
import muramasa.gregtech.common.tileentities.base.multi.TileEntityMultiMachine;
import net.minecraft.util.IStringSerializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.BiPredicate;

public class Structure {

    private ArrayList<Pair<int3, StructureElement>> elements = new ArrayList<>();
    private HashMap<String, Pair<Integer, BiPredicate<Integer, Integer>>> requirements = new HashMap<>();
    private int3 size = new int3();
    private int2 offset = new int2();

    public Structure(int3 size, ArrayList<Pair<int3, StructureElement>> elements) {
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
        requirements.put(serializable.getName(), new Pair<>(value, method));
        return this;
    }

    public boolean testRequirement(String name, int value) {
        Pair<Integer, BiPredicate<Integer, Integer>> tuple = requirements.get(name);
        return tuple != null && tuple.getB().test(value, tuple.getA());
    }

    public Collection<String> getRequirements() {
        return requirements.keySet();
    }

    public StructureResult evaluate(TileEntityMultiMachine tile) {
        StructureResult result = new StructureResult(this);
        Pair<int3, StructureElement> element;
        int3 corner = new int3(tile.getPos(), tile.getEnumFacing()).left(size.x / 2).back(offset.x).up(offset.y);
        int3 working = new int3();
        for (int i = 0; i < elements.size(); i++) {
            element = elements.get(i);
            working.set(corner).offset(element.getA(), Dir.RIGHT, Dir.UP, Dir.FORWARD);
            if (!element.getB().evaluate(tile, working, result)) {
                return result;
            }
        }
        return result;
    }
}
