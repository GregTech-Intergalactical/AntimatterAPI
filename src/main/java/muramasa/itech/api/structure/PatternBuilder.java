package muramasa.itech.api.structure;

import muramasa.itech.api.util.int3;
import net.minecraft.util.Tuple;

import java.util.ArrayList;
import java.util.HashMap;

public class PatternBuilder {

    private ArrayList<String[]> slices = new ArrayList<>();
    private HashMap<String, StructureElement> elementLookup = new HashMap<>();

    public static PatternBuilder start() {
        return new PatternBuilder();
    }

    public PatternBuilder layer(String... slices) {
        this.slices.add(slices);
        return this;
    }

    public PatternBuilder where(String key, StructureElement element) {
        elementLookup.put(key, element);
        return this;
    }

    public Tuple<ArrayList<int3>, int3> build() {
        int3 size = new int3(slices.get(0).length, slices.size(), slices.get(0)[0].length());
        ArrayList<int3> positions = new ArrayList<>();
        String[] slice;
        for (int y = 0; y < slices.size(); y++) {
            slice = slices.get(y);
            for (int x = 0; x < slice.length; x++) {
                String s = slice[x];
                for (int z = 0; z < slice[0].length(); z++) {
                    String c = s.substring(z);
                    StructureElement element = elementLookup.get(c);
                    if (element.shouldAddToList()) {
                        positions.add(new int3(x, y, z));
                    }
                }
            }
        }
        return new Tuple<>(positions, size);
    }

//    public static StructurePattern TEST = new StructurePattern(PatternBuilder.start()
//        .layer("XXX", "XXS", "XXX")
//        .layer("CCC", "CAC", "CCC")
//        .layer("CCC", "CAC", "CCC")
//        .layer("XXX", "XXX", "XXX")
//        .where("X", HATCH_OR_CASING_EBF).where("C", ANY_COIL_EBF).where("A", AIR).where("S", EBF)
//    );
}
