package muramasa.antimatter.structure;

import com.google.common.collect.ImmutableMap;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Ref;
import muramasa.antimatter.machine.types.HatchMachine;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.structure.impl.SimpleStructure;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import muramasa.antimatter.util.int3;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;

import java.util.*;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;

public class StructureBuilder<T extends TileEntityBasicMultiMachine<T>> {
    public StructureDefinition.Builder<TileEntityBasicMultiMachine<T>> STRUCTURE_BUILDER = StructureDefinition.builder();

    private Map<String, StructurePartBuilder> parts = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectMap<String, IStructureElement<?>> elementLookup = new Object2ObjectOpenHashMap<>();
    private Set<Direction> allowedFacings = Set.of(Ref.DIRS);

    public StructurePartBuilder part(String name){
        return new StructurePartBuilder(name);
    }

    public StructureBuilder<T> at(String key, IStructureElement<?> element) {
        elementLookup.put(key, element);
        return this;
    }

    public StructureBuilder<T> at(String key, IAntimatterObject... objects) {
        List<IStructureElement<T>> elements = new ArrayList<>();
        for (IAntimatterObject object : objects) {
            if (object instanceof HatchMachine machine){
                elements.add(new HatchElement<>(machine));
            } else if (object instanceof Block block){
                elements.add(StructureUtility.ofBlock(block));
            }
        }
        elementLookup.put(key, StructureUtility.ofChain(elements));
        return this;
    }

    public StructureBuilder<T> at(String key, Collection<? extends IAntimatterObject> objects) {
        return at(key, objects.toArray(new IAntimatterObject[0]));
    }

    public StructureBuilder<T> facings(Direction... faces) {
        allowedFacings = Set.of(faces);
        return this;
    }

    public SimpleStructure build() {
        return new SimpleStructure(new int3(), ImmutableMap.of(), allowedFacings);
        /*ImmutableMap.Builder<int3, StructureElement> elements = ImmutableMap.builder();
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
        return new SimpleStructure(size, elements.build(), allowedFacings);*/
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

    public class StructurePartBuilder{
        private final String name;
        private final List<String[]> slices = new ObjectArrayList<>();
        private int min = 1;
        private int max = 1;
        Direction.Axis offset = Direction.Axis.Y;

        public StructurePartBuilder(String name) {
            this.name = name;
        }

        public StructurePartBuilder of(String... slices) {
            this.slices.add(slices);
            return this;
        }

        public StructurePartBuilder of(int i) {
            slices.add(slices.get(i));
            return this;
        }

        public StructurePartBuilder min(int i){
            min = i;
            return this;
        }

        public StructurePartBuilder max(int i){
            max = i;
            return this;
        }

        public StructurePartBuilder offsetAxis(Direction.Axis axis){
            this.offset = axis;
            return this;
        }

        public StructureBuilder<T> build(){
            STRUCTURE_BUILDER.addShape(name, transpose(slices.toArray(String[][]::new)));
            parts.put(name, this);
            return StructureBuilder.this;
        }
    }
}
