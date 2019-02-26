package muramasa.gregtech.api.data;

import static muramasa.gregtech.api.data.Machines.*;
import static muramasa.gregtech.api.gui.SlotType.*;

public class Guis {

    public static void init() {

        ALLOY_SMELTER.getGui().add(IT_IN, 35, 25).add(IT_IN, 53, 25).add(IT_OUT, 107, 25);
        ASSEMBLER.getGui().add(IT_IN, 17, 16).add(IT_IN, 35, 16).add(IT_IN, 53, 16).add(IT_IN, 17, 34).add(IT_IN, 35, 34).add(IT_IN, 53, 34).add(IT_OUT, 107, 25);
        BENDER.getGui().add(ALLOY_SMELTER);
        CANNER.getGui().add(IT_IN, 35, 25).add(IT_IN, 53, 25).add(IT_OUT, 107, 25);
        COMPRESSOR.getGui().add(IT_IN, 53, 25).add(IT_OUT, 107, 25);
        CUTTER.getGui().add(IT_IN, 53, 25).add(IT_OUT, 107, 25).add(IT_OUT, 125, 25);
        FURNACE.getGui().add(ALLOY_SMELTER); //TODO
        EXTRACTOR.getGui().add(COMPRESSOR);
        EXTRUDER.getGui().add(ALLOY_SMELTER);
        LATHE.getGui().add(CUTTER);
        PULVERIZER.getGui().add(COMPRESSOR);
        RECYCLER.getGui().add(COMPRESSOR);
        SCANNER.getGui().add(COMPRESSOR);
        WIRE_MILL.getGui().add(COMPRESSOR);
        CENTRIFUGE.getGui().add(IT_IN, 35, 25).add(IT_IN, 53, 25).add(IT_OUT, 107, 16).add(IT_OUT, 125, 16).add(IT_OUT, 142, 16).add(IT_OUT, 107, 34).add(IT_OUT, 125, 34).add(IT_OUT, 143, 34);
        ELECTROLYZER.getGui().add(CENTRIFUGE);
        THERMAL_CENTRIFUGE.getGui().add(IT_IN, 53, 25).add(IT_OUT, 107, 25).add(IT_OUT, 125, 25).add(IT_OUT, 143, 25);
        ORE_WASHER.getGui().add(THERMAL_CENTRIFUGE).add(FL_IN, 53, 64);
        CHEMICAL_REACTOR.getGui().add(CANNER);
        FLUID_CANNER.getGui().add(COMPRESSOR);
        DISASSEMBLER.getGui().add(ALLOY_SMELTER); //TODO
        MASS_FABRICATOR.getGui().add(COMPRESSOR);
        AMP_FABRICATOR.getGui().add(COMPRESSOR);
        REPLICATOR.getGui().add(COMPRESSOR);
        FERMENTER.getGui().add(COMPRESSOR);
        FLUID_EXTRACTOR.getGui().add(COMPRESSOR);
        FLUID_SOLIDIFIER.getGui().add(COMPRESSOR);
        DISTILLERY.getGui().add(COMPRESSOR);
        CHEMICAL_BATH.getGui().add(THERMAL_CENTRIFUGE);
        AUTOCLAVE.getGui().add(COMPRESSOR);
        MIXER.getGui().add(IT_IN, 35, 16).add(IT_IN, 53, 16).add(IT_IN, 35, 34).add(IT_IN, 53, 34).add(IT_OUT, 107, 25);
        LASER_ENGRAVER.getGui().add(ALLOY_SMELTER);
        FORMING_PRESS.getGui().add(ALLOY_SMELTER);
        FORGE_HAMMER.getGui().add(ALLOY_SMELTER); //TODO
        SIFTER.getGui().add(DISASSEMBLER);
        ARC_FURNACE.getGui().add(ALLOY_SMELTER); //TODO
        PLASMA_ARC_FURNACE.getGui().add(ARC_FURNACE);

        COAL_BOILER.getGui().add(ALLOY_SMELTER); //TODO
        LAVA_BOILER.getGui().add(ALLOY_SMELTER);
        SOLAR_BOILER.getGui().add(ALLOY_SMELTER);
        STEAM_FURNACE.getGui().add(FURNACE);
        STEAM_PULVERIZER.getGui().add(PULVERIZER);
        STEAM_EXTRACTOR.getGui().add(EXTRACTOR);
        STEAM_FORGE_HAMMER.getGui().add(FORGE_HAMMER);
        STEAM_COMPRESSOR.getGui().add(COMPRESSOR);
        STEAM_ALLOY_SMELTER.getGui().add(ALLOY_SMELTER);

        PRIMITIVE_BLAST_FURNACE.getGui().add(ALLOY_SMELTER); //TODO
        BRONZE_BLAST_FURNACE.getGui().add(PRIMITIVE_BLAST_FURNACE);

        HATCH_ITEM_INPUT.getGui().add(IT_IN, 79, 34);
        HATCH_ITEM_OUTPUT.getGui().add(IT_OUT, 79, 34);
        HATCH_FLUID_INPUT.getGui().add(FL_IN, 79, 34);
        HATCH_FLUID_OUTPUT.getGui().add(FL_OUT, 79, 34);
        HATCH_MUFFLER.getGui().add(IT_IN, 79, 34);
    }
}
