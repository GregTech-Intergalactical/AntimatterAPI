package muramasa.gregtech.api.structure;

import muramasa.gregtech.api.util.Pair;
import muramasa.gregtech.api.util.int3;

import java.util.ArrayList;
import java.util.HashMap;

public class PatternBuilder {

    private ArrayList<String[]> slices = new ArrayList<>();
    private HashMap<String, StructureElement> elementLookup = new HashMap<>();

    public static PatternBuilder start() {
        return new PatternBuilder();
    }

    public PatternBuilder of(String... slices) {
        this.slices.add(slices);
        return this;
    }

    public PatternBuilder of(int i) {
        slices.add(slices.get(i));
        return this;
    }

    public PatternBuilder rep(String s, int count) {
        String[] slices = new String[count];
        for (int i = 0; i < count; i++) {
            slices[i] = s;
        }
        this.slices.add(slices);
        return this;
    }

    public PatternBuilder at(String key, StructureElement element) {
        elementLookup.put(key, element);
        return this;
    }

    public StructurePattern build() {
        elementLookup.put("A", StructureElement.AIR);
        elementLookup.put("X", StructureElement.X);
        ArrayList<Pair<int3, StructureElement>> elements = new ArrayList<>();
        int3 size = new int3(slices.get(0).length, slices.size(), slices.get(0)[0].length());
        StructureElement e;
        for (int y = 0; y < size.y; y++) {
            for (int x = 0; x < size.x; x++) {
                for (int z = 0; z < size.z; z++) {
                    e = elementLookup.get(slices.get(y)[x].substring(z, z + 1));
                    if (e != null) {
                        if (!e.shouldAddToList()) continue;
                        elements.add(new Pair<>(new int3(x, y, z), e));
                    } else {
                        throw new NullPointerException();
                    }
                }
            }
        }
        return new StructurePattern(size, elements);
    }
}
