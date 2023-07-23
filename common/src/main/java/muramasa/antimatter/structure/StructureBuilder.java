package muramasa.antimatter.structure;

import com.google.common.collect.ImmutableMap;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.Ref;
import muramasa.antimatter.machine.types.HatchMachine;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import muramasa.antimatter.util.int2;
import muramasa.antimatter.util.int3;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.*;
import java.util.function.BiFunction;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;

public class StructureBuilder<T extends TileEntityBasicMultiMachine<T>> {
    public StructureDefinition.Builder<T> STRUCTURE_BUILDER = StructureDefinition.builder();

    private Map<String, StructurePartBuilder> parts = new Object2ObjectLinkedOpenHashMap<>();
    private final Object2ObjectMap<Character, IStructureElement<T>> elementLookup = new Object2ObjectOpenHashMap<>();

    private final Object2ObjectMap<String, Pair<Integer, Integer>> minMaxMap = new Object2ObjectOpenHashMap<>();

    private Structure.StructurePartCheckCallback<T> callback = (structureDefinition, tile, part, i, newOffset) -> structureDefinition.check(tile, part, tile.getLevel(), tile.getExtendedFacing(), tile.getBlockPos().getX(), tile.getBlockPos().getY(), tile.getBlockPos().getZ(), newOffset.getX(), newOffset.getY(), newOffset.getZ(), !tile.isStructureValid());

    private int3 offset = new int3(0, 0, 0);
    private Set<Direction> allowedFacings = Set.of(Ref.DIRS);

    public StructurePartBuilder part(String name){
        return new StructurePartBuilder(name);
    }

    public StructureBuilder<T> atElement(char key, IStructureElement<T> element){
        elementLookup.put(key, element);
        return this;
    }

    public StructureBuilder<T> at(char key, Object... objects) {
        List<IStructureElement<T>> elements = new ArrayList<>();
        for (Object object : objects) {
            if (object instanceof HatchMachine machine){
                elements.add(AntimatterStructureUtility.ofHatch(machine));
            } else if (object instanceof Block block){
                elements.add(StructureUtility.ofBlock(block));
            } else if (object instanceof TagKey<?> tag && tag.isFor(Registry.BLOCK_REGISTRY)){
                elements.add(StructureUtility.ofBlock(tag.cast(Registry.BLOCK_REGISTRY).get()));
            } else if (object instanceof IStructureElement element){
                elements.add(element);
            }
        }
        elementLookup.put(key, StructureUtility.ofChain(elements));
        return this;
    }

    public StructureBuilder<T> at(char key, Collection<?> objects) {
        return at(key, objects.toArray(new Object[0]));
    }

    public StructureBuilder<T> facings(Direction... faces) {
        allowedFacings = Set.of(faces);
        return this;
    }

    public StructureBuilder<T> min(int min, HatchMachine... machines){
        for (HatchMachine machine : machines) {
            minMaxMap.put(machine.getComponentId(), Pair.of(min, Integer.MAX_VALUE));
        }
        return this;
    }

    public StructureBuilder<T> exact(int exact, HatchMachine... machines){
        for (HatchMachine machine : machines) {
            minMaxMap.put(machine.getComponentId(), Pair.of(exact, exact));
        }
        return this;
    }

    public StructureBuilder<T> minMax(int min, int max, HatchMachine... machines){
        for (HatchMachine machine : machines) {
            minMaxMap.put(machine.getComponentId(), Pair.of(min, max));
        }
        return this;
    }

    public StructureBuilder<T> max(int max, HatchMachine... machines){
        for (HatchMachine machine : machines) {
            minMaxMap.put(machine.getComponentId(), Pair.of(0, max));
        }
        return this;
    }

    public StructureBuilder<T> offset(int x, int y, int z){
        this.offset = new int3(x, y, z);
        return this;
    }

    public StructureBuilder<T> setStructurePartCheckCallback(Structure.StructurePartCheckCallback<T> callback){
        this.callback = callback;
        return this;
    }


    public Structure<T> build() {
        ImmutableMap.Builder<String, Pair<Integer, Integer>> minMaxMap = ImmutableMap.builder();
        minMaxMap.putAll(this.minMaxMap);
        ImmutableMap.Builder<String, Pair<int2, BiFunction<Integer, int3, int3>>> structureParts = ImmutableMap.builder();
        this.parts.forEach((k, v) -> {
            structureParts.put(k, Pair.of(new int2(v.min, v.max), v.offset));
        });
        elementLookup.forEach((c, e) -> {
            STRUCTURE_BUILDER.addElement(c, StructureUtility.onElementPass((el, t, w, x, y, z) -> {
                t.structurePositions.put(new BlockPos(x, y, z).asLong(), e);
            }, e));
        });
        return new Structure<>(STRUCTURE_BUILDER.build(), structureParts.build(), minMaxMap.build(), offset, callback);
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

        private String partToCheckOnFail;
        BiFunction<Integer, int3, int3> offset = (i, p) -> p;

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
            if (i <= 0) throw new IllegalArgumentException("i must be > 0!");
            min = i;
            return this;
        }

        public StructurePartBuilder max(int i){
            if (i <= 0) throw new IllegalArgumentException("i must be > 0!");
            max = i;
            return this;
        }

        public StructurePartBuilder offsetFunction(BiFunction<Integer, int3, int3> function){
            this.offset = function;
            return this;
        }

        public StructurePartBuilder checkOnFail(String partName){
            partToCheckOnFail = partName;
            return this;
        }

        public StructureBuilder<T> build(){
            STRUCTURE_BUILDER.addShape(name, transpose(slices.toArray(String[][]::new)));
            parts.put(name, this);
            return StructureBuilder.this;
        }
    }
}
