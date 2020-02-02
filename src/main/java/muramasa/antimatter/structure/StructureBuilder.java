package muramasa.antimatter.structure;

import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.util.int3;
import net.minecraft.util.Tuple;

import java.util.*;

public class StructureBuilder {

    private static HashMap<Character, StructureElement> globalElementLookup = new HashMap<>();

    private List<String[]> slices = new ArrayList<>();
    private Map<Character, StructureElement> elementLookup = new HashMap<>();

    //TODO, Proper size handling, either predetermine it with setSize or resize to setSize
    //TODO, probably move to StructureElement?
    public static void addGlobalElement(char key, StructureElement element) {
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

    public StructureBuilder at(char key, StructureElement element) {
        elementLookup.put(key, element);
        return this;
    }

    public StructureBuilder at(char key, IAntimatterObject... objects) {
        elementLookup.put(key, new ComponentElement(objects));
        return this;
    }

    public StructureBuilder at(char key, String name, IAntimatterObject... objects) {
        elementLookup.put(key, new ComponentElement(name, objects));
        return this;
    }

    public StructureBuilder at(char key, Collection<? extends IAntimatterObject> objects) {
        elementLookup.put(key, new ComponentElement(objects.toArray(new IAntimatterObject[0])));
        return this;
    }

    public StructureBuilder at(char key, String name, Collection<? extends IAntimatterObject> objects) {
        elementLookup.put(key, new ComponentElement(name, objects.toArray(new IAntimatterObject[0])));
        return this;
    }

    public Structure build() {
        ArrayList<Tuple<int3, StructureElement>> elements = new ArrayList<>();
        int3 size = new int3(slices.get(0).length, slices.size(), slices.get(0)[0].length());
        StructureElement e;
        for (int y = 0; y < size.y; y++) {
            for (int x = 0; x < size.x; x++) {
                for (int z = 0; z < size.z; z++) {
                    e = elementLookup.get(slices.get(y)[x].charAt(z));
                    if (e == null) e = globalElementLookup.get(slices.get(y)[x].charAt(z));
                    //TODO log this and return null;
                    if (e == null) throw new NullPointerException("StructureBuilder failed to parse slice: " + slices.get(y)[x]);
                    if (e.exclude) continue;
                    elements.add(new Tuple<>(new int3(x, y, z), e));
                }
            }
        }
        return new Structure(size, elements);
    }

    public Structure buildAligned() {
        List<Tuple<int3, StructureElement>> elements = new ArrayList<>();
        //made to conform to L-->R U-->D F-->B facing relative axises ABC
        int3 size = new int3(slices.get(0)[0].length(),slices.get(0).length, slices.size());
        StructureElement e;
        for (int a = 0; a < size.x; a++) {
            for (int b = 0; b < size.y; b++) {
                for (int c = 0; c < size.z; c++) {
                    e = elementLookup.get(slices.get(a)[b].charAt(c));
                    if (e == null) e = globalElementLookup.get(slices.get(a)[b].charAt(c));
                    //TODO log this and return null;
                    if (e == null) throw new NullPointerException("StructureBuilder failed to parse aligned slice: " + slices.get(a)[b]);
                    if (e.exclude) continue;
                    elements.add(new Tuple<>(new int3(a, b, c), e));
                }
            }
        }
        return new Structure(size, elements);
    }
}
