package muramasa.antimatter.structure;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.util.int3;
import net.minecraftforge.fml.RegistryObject;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class StructureBuilder {
    public static void addGlobalElement(String key, StructureElement element) {
        globalElementLookup.put(key, element);
    }

    private static final Object2ObjectMap<String, StructureElement> globalElementLookup = new Object2ObjectOpenHashMap<>();

    private final List<String[]> slices = new ObjectArrayList<>();
    private final Object2ObjectMap<String, StructureElement> elementLookup = new Object2ObjectOpenHashMap<>();


    public StructureBuilder of(String... slices) {
        this.slices.add(slices);
        return this;
    }

    public StructureBuilder of(int i) {
        slices.add(slices.get(i));
        return this;
    }

    public StructureBuilder at(String key, StructureElement element) {
        elementLookup.put(key, element);
        return this;
    }

    public StructureBuilder at(String key, IAntimatterObject... objects) {
        elementLookup.put(key, new ComponentElement(objects));
        return this;
    }

    public StructureBuilder at(String key, String name, IAntimatterObject... objects) {
        elementLookup.put(key, new ComponentElement(name, objects));
        return this;
    }

    public StructureBuilder at(String key, Collection<? extends IAntimatterObject> objects) {
        elementLookup.put(key, new ComponentElement(objects.toArray(new IAntimatterObject[0])));
        return this;
    }

    public StructureBuilder at(String key, String name, Collection<? extends IAntimatterObject> objects) {
        elementLookup.put(key, new ComponentElement(name, objects.toArray(new IAntimatterObject[0])));
        return this;
    }

    public StructureBuilder at(String key, RegistryObject<?>... objects) {
        Arrays.stream(objects).forEach(o -> {
            if (o.get() instanceof IAntimatterObject) at(key, o);
        });
        return this;
    }

    public Structure build() {
        ImmutableMap.Builder<int3, StructureElement> elements = ImmutableMap.builder();
        int3 size = new int3(slices.get(0).length, slices.size(), slices.get(0)[0].length());
        StructureElement e;
        for (int y = 0; y < size.getY(); y++) {
            for (int x = 0; x < size.getX(); x++) {
                for (int z = 0; z < size.getZ(); z++) {
                    e = elementLookup.get(slices.get(y)[x].substring(z, z + 1));
                    if (e == null) e = globalElementLookup.get(slices.get(y)[x].substring(z, z + 1));
                    //TODO log this and return null;
                    if (e == null)
                        throw new NullPointerException("StructureBuilder failed to parse slice: " + slices.get(y)[x]);
                    if (e.excludes()) continue;
                    elements.put(new int3(x, y, z), e);
                }
            }
        }
        return new Structure(size, elements.build());
    }

    /*public static IAntimatterObject[] getAntiObjects(Object... objects) {
        List<IAntimatterObject> antiObjects = new ObjectArrayList<>();
        Arrays.stream(objects).forEach(o -> {
            if (o instanceof RegistryObject && ((RegistryObject<?>) o).get() instanceof IAntimatterObject)
                antiObjects.add((IAntimatterObject) ((RegistryObject<?>) o).get());
            if (o instanceof IAntimatterObject) antiObjects.add((IAntimatterObject) o);
        });
        return antiObjects.toArray(new IAntimatterObject[0]);
    }*/
}
