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

import static muramasa.gregtech.api.data.Machines.*;
import static muramasa.gregtech.api.enums.CasingType.*;
import static muramasa.gregtech.api.structure.StructureElement.*;

public class StructurePattern {

    public static StructurePattern PATTERN_FUSION_REACTOR = PatternBuilder.start()
        .of(
            "XXXXXXXXXXXXXXX",
            "XXXXXXOOOXXXXXX",
            "XXXXOOXXXOOXXXX",
            "XXXOXXXXXXXOXXX",
            "XXOXXXXXXXXXOXX",
            "XXOXXXXXXXXXOXX",
            "XOXXXXXXXXXXXOX",
            "XOXXXXXXXXXXXOX",
            "XOXXXXXXXXXXXOX",
            "XXOXXXXXXXXXOXX",
            "XXOXXXXXXXXXOXX",
            "XXXOXXXXXXXOXXX",
            "XXXXOOXXXOOXXXX",
            "XXXXXXOOOXXXXXX",
            "XXXXXXXXXXXXXXX"
        )
        .of(
            "XXXXXXOOOXXXXXX",
            "XXXXOOCCCOOXXXX",
            "XXXOCCOOOCCOXXX",
            "XXOCOOXXXOOCOXX",
            "XOCOXXXXXXXOCOX",
            "XOCOXXXXXXXOCOX",
            "OCOXXXXXXXXXOCO",
            "OCMXXXXXXXXXOCO",
            "OCOXXXXXXXXXOCO",
            "XOCOXXXXXXXOCOX",
            "XOCOXXXXXXXOCOX",
            "XXOCOOXXXOOCOXX",
            "XXXOCCOOOCCOXXX",
            "XXXXOOCCCOOXXXX",
            "XXXXXXOOOXXXXXX"
        )
        .of(0)
        .at("O", StructureElement.FUSION_CASING).at("C", FUSION_COIL).at("M", FR_MACHINE).build()
        .offset(2, -1);

    public static StructurePattern PATTERN_PRIMITIVE_BLAST_FURNAVE = PatternBuilder.start()
        .of("CCC", "CCC", "CCC").of("CCC", "CBM", "CCC").of("CCC", "CBC", "CCC").of("CCC", "CAC", "CCC")
        .at("C", PBF_CASING).at("B", BF_AIR_OR_LAVA).at("M", PBF).build()
        .offset(2, -1).exact(PRIMITIVE_BLAST_FURNACE, 1).min(FIRE_BRICK, 32);

    public static StructurePattern PATTERN_BRONZE_BLAST_FURNACE = PatternBuilder.start()
        .of("CCC", "CCC", "CCC").of("CCC", "CBM", "CCC").of("CCC", "CBC", "CCC").of("CCC", "CAC", "CCC")
        .at("C", BBF_CASING).at("B", BF_AIR_OR_LAVA).at("M", BBF).build()
        .offset(2, -1).exact(BRONZE_BLAST_FURNACE, 1).min(BRONZE_PLATED_BRICK, 32);

    public static StructurePattern PATTERN_BLAST_FURNACE = PatternBuilder.start()
        .of("CCC", "CCM", "CCC").of("BBB", "BAB", "BBB").of(1).of("CCC", "CCC", "CCC")
        .at("C", HATCH_OR_CASING_EBF).at("B", ANY_COIL_EBF).at("M", EBF).build()
        .offset(2, 0).exact(ELECTRIC_BLAST_FURNACE, 1).min(HEAT_PROOF, 12).min(HATCH_ITEM_INPUT, 1).min(HATCH_ITEM_OUTPUT, 1);

    public static StructurePattern PATTERN_VACUUM_FREEZER = PatternBuilder.start()
        .of("CCC", "CCC", "CCC").of("CCC", "CAM", "CCC").of(0)
        .at("C", VF_HATCH_OR_CASING).at("M", VF_MACHINE).build()
        .offset(2, -1).exact(VACUUM_FREEZER, 1).min(FROST_PROOF, 22).min(HATCH_ITEM_INPUT, 1).min(HATCH_ITEM_OUTPUT, 1).min(HATCH_ENERGY, 1);

    private ArrayList<Pair<int3, StructureElement>> elements = new ArrayList<>();
    private HashMap<String, Pair<Integer, BiPredicate<Integer, Integer>>> requirements = new HashMap<>();
    private int3 size = new int3();
    private int2 offset = new int2();

    public StructurePattern(int3 size, ArrayList<Pair<int3, StructureElement>> elements) {
        this.size = size;
        this.elements = elements;
    }

    public StructurePattern offset(int x, int y) {
        offset.set(x, y);
        return this;
    }

    public StructurePattern exact(IStringSerializable serializable, int value) {
        return addReq(serializable, value, StructureResult::equal);
    }

    public StructurePattern min(IStringSerializable serializable, int value) {
        return addReq(serializable, value, StructureResult::moreOrEqual);
    }

    public StructurePattern addReq(IStringSerializable serializable, int value, BiPredicate<Integer, Integer> method) {
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
