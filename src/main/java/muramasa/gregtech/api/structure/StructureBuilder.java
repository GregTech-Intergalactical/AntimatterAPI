package muramasa.gregtech.api.structure;

import muramasa.gregtech.api.data.Structures;
import muramasa.gregtech.api.util.int3;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Tuple;

import java.util.ArrayList;
import java.util.HashMap;

public class StructureBuilder {

    private static HashMap<String, StructureElement> globalElementLookup = new HashMap<>();

    private ArrayList<String[]> slices = new ArrayList<>();
    private HashMap<String, StructureElement> elementLookup = new HashMap<>();

    static {
        globalElementLookup.put("A", Structures.AIR);
        globalElementLookup.put("X", Structures.X);
    }

    public static StructureBuilder start() {
        return new StructureBuilder();
    }

    public static void addGlobalElement(String key, StructureElement element) {
        globalElementLookup.put(key, element);
    }

    public StructureBuilder of(String... slices) {
        this.slices.add(slices);
        return this;
    }

    public StructureBuilder of(int i) {
        slices.add(slices.get(i));
        return this;
    }

    public StructureBuilder rep(String s, int count) {
        String[] slices = new String[count];
        for (int i = 0; i < count; i++) {
            slices[i] = s;
        }
        this.slices.add(slices);
        return this;
    }

    public StructureBuilder at(String key, StructureElement element) {
        elementLookup.put(key, element);
        return this;
    }

    public StructureBuilder at(String key, IStringSerializable serializable) {
        elementLookup.put(key, new StructureElement(serializable));
        return this;
    }

    public Structure build() {
        ArrayList<Tuple<int3, StructureElement>> elements = new ArrayList<>();
        int3 size = new int3(slices.get(0).length, slices.size(), slices.get(0)[0].length());
        StructureElement e;
        for (int y = 0; y < size.y; y++) {
            for (int x = 0; x < size.x; x++) {
                for (int z = 0; z < size.z; z++) {
                    e = elementLookup.get(slices.get(y)[x].substring(z, z + 1));
                    if (e == null) e = globalElementLookup.get(slices.get(y)[x].substring(z, z + 1));
                    if (e != null) {
                        if (e.excludeFromList) continue;
                        elements.add(new Tuple<>(new int3(x, y, z), e));
                    } else {
                        throw new NullPointerException();
                    }
                }
            }
        }
        return new Structure(size, elements);
    }
}
