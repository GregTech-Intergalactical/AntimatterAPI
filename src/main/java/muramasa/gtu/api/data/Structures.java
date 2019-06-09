package muramasa.gtu.api.data;

import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.blocks.BlockCoil;
import muramasa.gtu.api.machines.Tier;
import muramasa.gtu.api.structure.StructureBuilder;
import muramasa.gtu.api.structure.StructureElement;
import muramasa.gtu.api.structure.StructureResult;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.api.util.int3;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

import static muramasa.gtu.common.Data.*;
import static muramasa.gtu.api.data.Machines.*;

public class Structures {

    /** Global Elements **/
    public static StructureElement X = new StructureElement("x").exclude(); //Used to skip positions for non-cubic structures
    public static StructureElement AIR = new StructureElement("air") { //Air Block Check
        @Override
        public boolean evaluate(TileEntityMachine machine, int3 pos, StructureResult result) {
            IBlockState state = machine.getWorld().getBlockState(pos.asBP());
            return state.getBlock().isAir(state, machine.getWorld(), pos.asBP());
        }
    };

    /** Special Case Elements **/
    public static StructureElement AIR_OR_LAVA = new StructureElement("air_or_lave") {
        @Override
        public boolean evaluate(TileEntityMachine machine, int3 pos, StructureResult result) {
            IBlockState state = machine.getWorld().getBlockState(pos.asBP());
            return AIR.evaluate(machine, pos, result) || state.getBlock() == Blocks.LAVA || state.getBlock() == Blocks.FLOWING_LAVA;
        }
    };

    public static void init() {
        StructureBuilder.addGlobalElement("A", AIR);
        StructureBuilder.addGlobalElement("X", X);
        PRIMITIVE_BLAST_FURNACE.setStructure(new StructureBuilder()
            .of("CCC", "CCC", "CCC").of("CCC", "CBM", "CCC").of("CCC", "CBC", "CCC").of("CCC", "CAC", "CCC")
            .at("C", CASING_FIRE_BRICK).at("B", AIR_OR_LAVA).at("M", PRIMITIVE_BLAST_FURNACE)
            .build().offset(2, -1).min(32, CASING_FIRE_BRICK)
        );
        BRONZE_BLAST_FURNACE.setStructure(new StructureBuilder()
            .of("CCC", "CCC", "CCC").of("CCC", "CBM", "CCC").of("CCC", "CBC", "CCC").of("CCC", "CAC", "CCC")
            .at("C", CASING_BRONZE_PLATED_BRICK).at("B", AIR_OR_LAVA).at("M", BRONZE_BLAST_FURNACE)
            .build().offset(2, -1).min(32, CASING_BRONZE_PLATED_BRICK)
        );
        BLAST_FURNACE.setStructure(new StructureBuilder()
            .of("CCC", "CCM", "CCC").of("BBB", "BAB", "BBB").of(1).of("CCC", "CCC", "CCC")
            .at("M", BLAST_FURNACE).at("B", "coil", GregTechAPI.all(BlockCoil.class)).at("C", CASING_HEAT_PROOF, HATCH_ITEM_I, HATCH_ITEM_O, HATCH_FLUID_I, HATCH_FLUID_O, HATCH_ENERGY)
            .build().offset(2, 0).min(12, CASING_HEAT_PROOF).min(1, HATCH_ITEM_I, HATCH_ITEM_O, HATCH_ENERGY)
        );
        MULTI_SMELTER.setStructure(new StructureBuilder()
            .of("CCC", "CCM", "CCC").of("BBB", "BAB", "BBB").of("CCC", "CCC", "CCC")
            .at("M", MULTI_SMELTER).at("B", "coil", GregTechAPI.all(BlockCoil.class)).at("C", CASING_HEAT_PROOF, HATCH_ITEM_I, HATCH_ITEM_O, HATCH_ENERGY)
            .build().offset(2, 0).min(12, CASING_HEAT_PROOF).min(1, HATCH_ITEM_I, HATCH_ITEM_O, HATCH_ENERGY)
        );
        VACUUM_FREEZER.setStructure(new StructureBuilder()
            .of("CCC", "CCC", "CCC").of("CCC", "CAM", "CCC").of(0)
            .at("M", VACUUM_FREEZER).at("C", CASING_FROST_PROOF, HATCH_ITEM_I, HATCH_ITEM_O, HATCH_FLUID_I, HATCH_ENERGY)
            .build().offset(2, -1).min(22, CASING_FROST_PROOF).min(1, HATCH_ITEM_I, HATCH_ITEM_O, HATCH_ENERGY)
        );
        IMPLOSION_COMPRESSOR.setStructure(new StructureBuilder()
            .of("CCC", "CCC", "CCC").of("CCC", "CAM", "CCC").of(0)
            .at("M", IMPLOSION_COMPRESSOR).at("C", CASING_SOLID_STEEL, HATCH_ITEM_I, HATCH_ITEM_O, HATCH_ENERGY)
            .build().offset(2, -1).min(16, CASING_SOLID_STEEL).min(1, HATCH_ITEM_I, HATCH_ITEM_O, HATCH_ENERGY)
        );
        PYROLYSIS_OVEN.setStructure(new StructureBuilder()
            .of("BBBBB", "BCCCB", "BCCCM", "BCCCB", "BBBBB").of("SSSSS", "SAAAS", "SAAAS", "SAAAS", "SSSSS").of(1).of("TTTTT", "TTTTT", "TTYTT", "TTTTT", "TTTTT")
            .at("M", PYROLYSIS_OVEN).at("S", CASING_ULV).at("C", GregTechAPI.all(BlockCoil.class)).at("B", CASING_ULV, HATCH_ITEM_O, HATCH_ENERGY).at("T", CASING_ULV, HATCH_ITEM_I).at("Y", HATCH_MUFFLER)
            .build().offset(4, 0).min(60, CASING_ULV).min(1, HATCH_ITEM_I, HATCH_ITEM_O, HATCH_ENERGY, HATCH_MUFFLER)
        );
        LARGE_TURBINE.setStructure(new StructureBuilder()
            .of("CCCC", "CCCC", "CCCC").of("CHHC", "EAAM", "CHHC").of(0)
            .at("M", LARGE_TURBINE).at("C", CASING_TURBINE_4).at("H", CASING_TURBINE_4, HATCH_FLUID_I, HATCH_FLUID_O).at("E", HATCH_DYNAMO)
            .build().offset(3, -1).min(28, CASING_TURBINE_4).min(1, HATCH_FLUID_I, HATCH_FLUID_O)
        );
        COMBUSTION_ENGINE.setStructure(new StructureBuilder()
            .of("CCCV", "CCCV", "CCCV").of("CHHV", "EAAM", "CHHV").of(0)
            .at("M", COMBUSTION_ENGINE).at("C", CASING_TITANIUM).at("V", CASING_ENGINE_INTAKE).at("H", CASING_TITANIUM, HATCH_FLUID_I, HATCH_FLUID_O).at("E", HATCH_DYNAMO)
            .build().offset(3, -1).min(19, CASING_TITANIUM).min(1, HATCH_FLUID_I, HATCH_FLUID_O)
        );
        //TODO Tier sensitive...
        FUSION_REACTOR.setStructure(Tier.LUV, new StructureBuilder()
            .of(
                "XXXXXXXXXXXXXXX",
                "XXXXXXBOBXXXXXX",
                "XXXXOOXXXOOXXXX",
                "XXXOXXXXXXXOXXX",
                "XXOXXXXXXXXXOXX",
                "XXOXXXXXXXXXOXX",
                "XBXXXXXXXXXXXBX",
                "XOXXXXXXXXXXXOX",
                "XBXXXXXXXXXXXBX",
                "XXOXXXXXXXXXOXX",
                "XXOXXXXXXXXXOXX",
                "XXXOXXXXXXXOXXX",
                "XXXXOOXXXOOXXXX",
                "XXXXXXBOBXXXXXX",
                "XXXXXXXXXXXXXXX"
            ).of(
                "XXXXXXOOOXXXXXX",
                "XXXXOOCCCOOXXXX",
                "XXXOCCHOHCCOXXX",
                "XXOCEOXXXOECOXX",
                "XOCEXXXXXXXECOX",
                "XOCOXXXXXXXOCOX",
                "OCHXXXXXXXXXHCO",
                "OCMXXXXXXXXXHCO",
                "OCHXXXXXXXXXHCO",
                "XOCOXXXXXXXOCOX",
                "XOCEXXXXXXXECOX",
                "XXOCEOXXXOECOXX",
                "XXXOCCHOHCCOXXX",
                "XXXXOOCCCOOXXXX",
                "XXXXXXOOOXXXXXX"
            ).of(0)
            .at("O", CASING_FUSION_3).at("C", COIL_FUSION).at("M", FUSION_REACTOR).at("B", CASING_FUSION_3, HATCH_FLUID_I).at("H", CASING_FUSION_3, HATCH_FLUID_O).at("E", CASING_FUSION_3, HATCH_ENERGY)
            .build().offset(2, -1).min(2, HATCH_FLUID_I).min(1, HATCH_FLUID_O, HATCH_ENERGY));
    }
}
