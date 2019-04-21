package muramasa.gtu.api.data;

import muramasa.gtu.api.machines.Tier;
import muramasa.gtu.api.structure.StructureBuilder;
import muramasa.gtu.api.structure.StructureElement;
import muramasa.gtu.api.structure.StructureResult;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.api.util.int3;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

import static muramasa.gtu.api.data.Casing.*;
import static muramasa.gtu.api.data.Coil.FUSION;
import static muramasa.gtu.api.data.Machines.*;

public class Structures {

    static {

    }

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
            .at("C", FIRE_BRICK).at("B", AIR_OR_LAVA).at("M", PRIMITIVE_BLAST_FURNACE)
            .build().offset(2, -1).exact(PRIMITIVE_BLAST_FURNACE, 1).min(FIRE_BRICK, 32)
        );
        BRONZE_BLAST_FURNACE.setStructure(new StructureBuilder()
            .of("CCC", "CCC", "CCC").of("CCC", "CBM", "CCC").of("CCC", "CBC", "CCC").of("CCC", "CAC", "CCC")
            .at("C", BRONZE_PLATED_BRICK).at("B", AIR_OR_LAVA).at("M", BRONZE_BLAST_FURNACE)
            .build().offset(2, -1).exact(BRONZE_BLAST_FURNACE, 1).min(BRONZE_PLATED_BRICK, 32)
        );
        BLAST_FURNACE.setStructure(new StructureBuilder()
            .of("CCC", "CCM", "CCC").of("BBB", "BAB", "BBB").of(1).of("CCC", "CCC", "CCC")
            .at("M", BLAST_FURNACE).at("B", "any_coil", Coil.getAll()).at("C", HEAT_PROOF, HATCH_ITEM_I, HATCH_ITEM_O, HATCH_FLUID_I, HATCH_FLUID_O, HATCH_ENERGY)
            .build().offset(2, 0).exact(BLAST_FURNACE, 1).min(HEAT_PROOF, 12).min(HATCH_ENERGY, 1).min(HATCH_ITEM_I, 1).min(HATCH_ITEM_O, 1)
        );
        VACUUM_FREEZER.setStructure(new StructureBuilder()
            .of("CCC", "CCC", "CCC").of("CCC", "CAM", "CCC").of(0)
            .at("M", VACUUM_FREEZER).at("C", FROST_PROOF, HATCH_ITEM_I, HATCH_ITEM_O, HATCH_FLUID_I, HATCH_ENERGY)
            .build().offset(2, -1).exact(VACUUM_FREEZER, 1).min(FROST_PROOF, 22).min(HATCH_ITEM_I, 1).min(HATCH_ITEM_O, 1).min(HATCH_ENERGY, 1)
        );
        LARGE_TURBINE.setStructure(new StructureBuilder()
            .of("CCCC", "CCCC", "CCCC").of("CHHC", "EAAM", "CHHC").of(0)
            .at("M", LARGE_TURBINE).at("C", TURBINE_4).at("H", TURBINE_4, HATCH_FLUID_I, HATCH_FLUID_O).at("E", HATCH_DYNAMO)
            .build().offset(3, -1).exact(LARGE_TURBINE, 1).min(TURBINE_4, 28).min(HATCH_FLUID_I, 1).min(HATCH_FLUID_O, 1)
        );
        COMBUSTION_ENGINE.setStructure(new StructureBuilder()
            .of("CCCV", "CCCV", "CCCV").of("CHHV", "EAAM", "CHHV").of(0)
            .at("M", COMBUSTION_ENGINE).at("C", TITANIUM).at("V", ENGINE_INTAKE).at("H", TITANIUM, HATCH_FLUID_I, HATCH_FLUID_O).at("E", HATCH_DYNAMO)
            .build().offset(3, -1).exact(COMBUSTION_ENGINE, 1).min(TITANIUM, 19).min(HATCH_FLUID_I, 1).min(HATCH_FLUID_O, 1)
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
            .at("O", FUSION_3).at("C", FUSION).at("M", FUSION_REACTOR).at("B", FUSION_3, HATCH_FLUID_I).at("H", FUSION_3, HATCH_FLUID_O).at("E", FUSION_3, HATCH_ENERGY)
            .build().offset(2, -1).min(HATCH_FLUID_I, 2).min(HATCH_FLUID_O, 1).min(HATCH_ENERGY, 1));
    }
}
