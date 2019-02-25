package muramasa.gregtech.api.data;

import muramasa.gregtech.api.enums.CasingType;
import muramasa.gregtech.api.enums.CoilType;
import muramasa.gregtech.api.structure.PatternBuilder;
import muramasa.gregtech.api.structure.StructureElement;
import muramasa.gregtech.api.structure.StructurePattern;
import muramasa.gregtech.api.structure.StructureResult;
import muramasa.gregtech.api.util.int3;
import muramasa.gregtech.common.tileentities.base.multi.TileEntityMultiMachine;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

import static muramasa.gregtech.api.data.Machines.*;
import static muramasa.gregtech.api.enums.CasingType.*;

public class Structures {

    /** Air Type Elements **/
    public static StructureElement X = new StructureElement("x", false); //Used to skip positions for non-cubic structures
    public static StructureElement AIR = new StructureElement("air") { //Air Block Check
        @Override
        public boolean evaluate(TileEntityMultiMachine machine, int3 pos, StructureResult result) {
            IBlockState state = machine.getWorld().getBlockState(pos.asBlockPos());
            return state.getBlock().isAir(state, machine.getWorld(), pos.asBlockPos());
        }
    };

    /** Charcoal Pit Elements **/
    public static StructureElement CP_LOG_OR_ELSE = new StructureElement("logorelse") {
        @Override
        public boolean evaluate(TileEntityMultiMachine machine, int3 pos, StructureResult result) {
            IBlockState state = machine.getWorld().getBlockState(pos.asBlockPos());
            String tool = state.getBlock().getHarvestTool(state);
            return state.getBlock().getMaterial(state) == Material.WOOD && (tool != null && tool.equals("axe"));
        }
    };

    /** Primitive/Bronze Furnace Elements **/
    public static StructureElement PBF = new StructureElement(Machines.PRIMITIVE_BLAST_FURNACE);
    public static StructureElement PBF_CASING = new StructureElement(CasingType.FIRE_BRICK);
    public static StructureElement BBF = new StructureElement(Machines.BRONZE_BLAST_FURNACE);
    public static StructureElement BBF_CASING = new StructureElement(CasingType.BRONZE_PLATED_BRICK);
    public static StructureElement BF_AIR_OR_LAVA = new StructureElement("airorlava") {
        @Override
        public boolean evaluate(TileEntityMultiMachine machine, int3 pos, StructureResult result) {
            IBlockState state = machine.getWorld().getBlockState(pos.asBlockPos());
            return AIR.evaluate(machine, pos, result) || state.getBlock() == Blocks.LAVA || state.getBlock() == Blocks.FLOWING_LAVA;
        }
    };

    /** Electric Blast Furnace Elements **/
    public static StructureElement EBF = new StructureElement(Machines.ELECTRIC_BLAST_FURNACE);
    public static StructureElement HATCH_OR_CASING_EBF = new StructureElement("hatchorcasingebf", CasingType.HEAT_PROOF, Machines.HATCH_ITEM_INPUT, Machines.HATCH_ITEM_OUTPUT);
    public static StructureElement ANY_COIL_EBF = new StructureElement("anycoilebf", CoilType.values());

    /** Vacuum Freezer Elements **/
    public static StructureElement VF_MACHINE = new StructureElement(Machines.VACUUM_FREEZER);
    public static StructureElement VF_HATCH_OR_CASING = new StructureElement("hatchorcasingvf", CasingType.FROST_PROOF, Machines.HATCH_ITEM_INPUT, Machines.HATCH_ITEM_OUTPUT, Machines.HATCH_ENERGY);

    /** Fusion Reactor Elements **/
    public static StructureElement FR_MACHINE = new StructureElement(Machines.FUSION_REACTOR_1);
    public static StructureElement FUSION_CASING = new StructureElement(FUSION_3);
    public static StructureElement FUSION_COIL = new StructureElement(CoilType.FUSION);

    /** Structure Patterns **/
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
        .at("O", FUSION_CASING).at("C", FUSION_COIL).at("M", FR_MACHINE).build()
        .offset(2, -1);
}
