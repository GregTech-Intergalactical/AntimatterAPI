package muramasa.itech.api.structure;

import muramasa.itech.api.enums.CasingType;
import muramasa.itech.api.machines.MachineList;
import muramasa.itech.api.util.Dir;
import muramasa.itech.api.util.int2;
import muramasa.itech.api.util.int3;
import muramasa.itech.common.tileentities.multi.TileEntityMultiMachine;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Tuple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.BiPredicate;

import static muramasa.itech.api.structure.StructureElement.*;

public class StructurePattern {

    public static StructurePattern FUSION_REACTOR = new StructurePattern(new StructureElement[][][] {
        {
            {X, X, X, X, X, X, X, X, X, X, X, X, X, X, X},
            {X, X, X, X, X, X, FUSION_CASING, FUSION_CASING, FUSION_CASING, X, X, X, X, X, X},
            {X, X, X, X, FUSION_CASING, FUSION_CASING, X, X, X, FUSION_CASING, FUSION_CASING, X, X, X, X},
            {X, X, X, FUSION_CASING, X, X, X, X, X, X, X, FUSION_CASING, X, X, X},
            {X, X, FUSION_CASING, X, X, X, X, X, X, X, X, X, FUSION_CASING, X, X},
            {X, X, FUSION_CASING, X, X, X, X, X, X, X, X, X, FUSION_CASING, X, X},
            {X, FUSION_CASING, X, X, X, X, X, X, X, X, X, X, X, FUSION_CASING, X},
            {X, FUSION_CASING, X, X, X, X, X, X, X, X, X, X, X, FUSION_CASING, X},
            {X, FUSION_CASING, X, X, X, X, X, X, X, X, X, X, X, FUSION_CASING, X},
            {X, X, FUSION_CASING, X, X, X, X, X, X, X, X, X, FUSION_CASING, X, X},
            {X, X, FUSION_CASING, X, X, X, X, X, X, X, X, X, FUSION_CASING, X, X},
            {X, X, X, FUSION_CASING, X, X, X, X, X, X, X, FUSION_CASING, X, X, X},
            {X, X, X, X, FUSION_CASING, FUSION_CASING, X, X, X, FUSION_CASING, FUSION_CASING, X, X, X, X},
            {X, X, X, X, X, X, FUSION_CASING, FUSION_CASING, FUSION_CASING, X, X, X, X, X, X},
            {X, X, X, X, X, X, X, X, X, X, X, X, X, X, X},
        },
        {
            {X, X, X, X, X, X, FUSION_CASING, FUSION_CASING, FUSION_CASING, X, X, X, X, X, X},
            {X, X, X, X, FUSION_CASING, FUSION_CASING, FUSION_COIL, FUSION_COIL, FUSION_COIL, FUSION_CASING, FUSION_CASING, X, X, X, X},
            {X, X, X, FUSION_CASING, FUSION_COIL, FUSION_COIL, FUSION_CASING, FUSION_CASING, FUSION_CASING, FUSION_COIL, FUSION_COIL, FUSION_CASING, X, X, X},
            {X, X, FUSION_CASING, FUSION_COIL, FUSION_CASING, FUSION_CASING, X, X, X, FUSION_CASING, FUSION_CASING, FUSION_COIL, FUSION_CASING, X, X},
            {X, FUSION_CASING, FUSION_COIL, FUSION_CASING, X, X, X, X, X, X, X, FUSION_CASING, FUSION_COIL, FUSION_CASING, X},
            {X, FUSION_CASING, FUSION_COIL, FUSION_CASING, X, X, X, X, X, X, X, FUSION_CASING, FUSION_COIL, FUSION_CASING, X},
            {FUSION_CASING, FUSION_COIL, FUSION_CASING, X, X, X, X, X, X, X, X, X, FUSION_CASING, FUSION_COIL, FUSION_CASING},
            {FUSION_CASING, FUSION_COIL, FR_MACHINE, X, X, X, X, X, X, X, X, X, FUSION_CASING, FUSION_COIL, FUSION_CASING},
            {FUSION_CASING, FUSION_COIL, FUSION_CASING, X, X, X, X, X, X, X, X, X, FUSION_CASING, FUSION_COIL, FUSION_CASING},
            {X, FUSION_CASING, FUSION_COIL, FUSION_CASING, X, X, X, X, X, X, X, FUSION_CASING, FUSION_COIL, FUSION_CASING, X},
            {X, FUSION_CASING, FUSION_COIL, FUSION_CASING, X, X, X, X, X, X, X, FUSION_CASING, FUSION_COIL, FUSION_CASING, X},
            {X, X, FUSION_CASING, FUSION_COIL, FUSION_CASING, FUSION_CASING, X, X, X, FUSION_CASING, FUSION_CASING, FUSION_COIL, FUSION_CASING, X, X},
            {X, X, X, FUSION_CASING, FUSION_COIL, FUSION_COIL, FUSION_CASING, FUSION_CASING, FUSION_CASING, FUSION_COIL, FUSION_COIL, FUSION_CASING, X, X, X},
            {X, X, X, X, FUSION_CASING, FUSION_CASING, FUSION_COIL, FUSION_COIL, FUSION_COIL, FUSION_CASING, FUSION_CASING, X, X, X, X},
            {X, X, X, X, X, X, FUSION_CASING, FUSION_CASING, FUSION_CASING, X, X, X, X, X, X},
        },
        {
            {X, X, X, X, X, X, X, X, X, X, X, X, X, X, X},
            {X, X, X, X, X, X, FUSION_CASING, FUSION_CASING, FUSION_CASING, X, X, X, X, X, X},
            {X, X, X, X, FUSION_CASING, FUSION_CASING, X, X, X, FUSION_CASING, FUSION_CASING, X, X, X, X},
            {X, X, X, FUSION_CASING, X, X, X, X, X, X, X, FUSION_CASING, X, X, X},
            {X, X, FUSION_CASING, X, X, X, X, X, X, X, X, X, FUSION_CASING, X, X},
            {X, X, FUSION_CASING, X, X, X, X, X, X, X, X, X, FUSION_CASING, X, X},
            {X, FUSION_CASING, X, X, X, X, X, X, X, X, X, X, X, FUSION_CASING, X},
            {X, FUSION_CASING, X, X, X, X, X, X, X, X, X, X, X, FUSION_CASING, X},
            {X, FUSION_CASING, X, X, X, X, X, X, X, X, X, X, X, FUSION_CASING, X},
            {X, X, FUSION_CASING, X, X, X, X, X, X, X, X, X, FUSION_CASING, X, X},
            {X, X, FUSION_CASING, X, X, X, X, X, X, X, X, X, FUSION_CASING, X, X},
            {X, X, X, FUSION_CASING, X, X, X, X, X, X, X, FUSION_CASING, X, X, X},
            {X, X, X, X, FUSION_CASING, FUSION_CASING, X, X, X, FUSION_CASING, FUSION_CASING, X, X, X, X},
            {X, X, X, X, X, X, FUSION_CASING, FUSION_CASING, FUSION_CASING, X, X, X, X, X, X},
            {X, X, X, X, X, X, X, X, X, X, X, X, X, X, X},
        }
    }).addOffset(2, -1);

    public static StructurePattern BLAST_FURNACE = new StructurePattern(new StructureElement[][][] {
        {
            {HATCH_OR_CASING_EBF, HATCH_OR_CASING_EBF, HATCH_OR_CASING_EBF},
            {HATCH_OR_CASING_EBF, HATCH_OR_CASING_EBF, EBF},
            {HATCH_OR_CASING_EBF, HATCH_OR_CASING_EBF, HATCH_OR_CASING_EBF},
        },
        {
            {ANY_COIL_EBF, ANY_COIL_EBF, ANY_COIL_EBF},
            {ANY_COIL_EBF, AIR, ANY_COIL_EBF},
            {ANY_COIL_EBF, ANY_COIL_EBF, ANY_COIL_EBF}
        },
        {
            {ANY_COIL_EBF, ANY_COIL_EBF, ANY_COIL_EBF},
            {ANY_COIL_EBF, AIR, ANY_COIL_EBF},
            {ANY_COIL_EBF, ANY_COIL_EBF, ANY_COIL_EBF}
        },
        {
            {HATCH_OR_CASING_EBF, HATCH_OR_CASING_EBF, HATCH_OR_CASING_EBF},
            {HATCH_OR_CASING_EBF, HATCH_OR_CASING_EBF, HATCH_OR_CASING_EBF},
            {HATCH_OR_CASING_EBF, HATCH_OR_CASING_EBF, HATCH_OR_CASING_EBF},
        }
    }).addOffset(2, 0).addExact(MachineList.BLASTFURNACE, 1).addMin(CasingType.HEAT_PROOF, 12);

    private ArrayList<Tuple<int3, StructureElement>> elements = new ArrayList<>();
    private HashMap<String, Tuple<Integer, BiPredicate<Integer, Integer>>> requirements = new HashMap<>();
    private int3 size = new int3();
    private int2 offset = new int2();

    public StructurePattern(StructureElement[][][] pattern) {
        StructureElement element;
        size = new int3(pattern[0].length, pattern.length, pattern[0][0].length);
        for (int y = 0; y < size.y; y++) {
            for (int x = 0; x < size.x; x++) {
                for (int z = 0; z < size.z; z++) {
                    element = pattern[y][x][z];
                    if (element.shouldAddToList()) {
                        elements.add(new Tuple<>(new int3(x, y, z), element));
                    }
                }
            }
        }
    }

    public StructurePattern addOffset(int x, int y) {
        offset.set(x, y);
        return this;
    }

    public StructurePattern addExact(IStringSerializable serializable, int value) {
        return addReq(serializable, value, StructureResult::equal);
    }

    public StructurePattern addMin(IStringSerializable serializable, int value) {
        return addReq(serializable, value, StructureResult::moreOrEqual);
    }

    public StructurePattern addReq(IStringSerializable serializable, int value, BiPredicate<Integer, Integer> method) {
        requirements.put(serializable.getName(), new Tuple<>(value, method));
        return this;
    }

    public boolean testRequirement(String name, int value) {
        Tuple<Integer, BiPredicate<Integer, Integer>> tuple = requirements.get(name);
        return tuple != null && tuple.getSecond().test(value, tuple.getFirst());
    }

    public Collection<String> getRequirements() {
        return requirements.keySet();
    }

    public StructureResult evaluate(TileEntityMultiMachine tile) {
        StructureResult result = new StructureResult(this);
        Tuple<int3, StructureElement> element;
        int3 corner = new int3(tile.getPos(), tile.getFacing()).left(size.x / 2).back(offset.x).up(offset.y);
        int3 working = new int3();
        for (int i = 0; i < elements.size(); i++) {
            element = elements.get(i);
            working.set(corner).offset(element.getFirst(), Dir.RIGHT, Dir.UP, Dir.FORWARD);
            if (!element.getSecond().evaluate(tile, working, result)) {
                return result;
            }
        }
        return result;
    }
}
