package muramasa.itech.api.machines;

import muramasa.itech.api.enums.MachineFlag;
import muramasa.itech.api.structure.StructurePattern;
import muramasa.itech.common.tileentities.base.TileEntityMachine;
import muramasa.itech.common.tileentities.base.multi.TileEntityHatch;
import muramasa.itech.common.tileentities.overrides.TileEntityBasicMachine;
import muramasa.itech.common.tileentities.overrides.TileEntitySteamMachine;
import muramasa.itech.common.tileentities.overrides.multi.TileEntityElectricBlastFurnace;
import muramasa.itech.common.tileentities.overrides.multi.TileEntityFusionReactor;
import muramasa.itech.common.utils.Ref;
import muramasa.itech.loaders.ContentLoader;

import java.util.HashMap;

import static muramasa.itech.api.enums.MachineFlag.*;

public class MachineList {

    static HashMap<String, Machine> machineTypeLookup = new HashMap<>();

    public static Machine INVALID = new Machine("invalid", ContentLoader.blockMachines, TileEntityMachine.class).setTiers(Tier.LV).setBlock(ContentLoader.blockMachines);

    public static Machine ALLOY_SMELTER = asBasic("alloy_smelter", ITEM).addSlots(new SlotData(0, 35, 25), new SlotData(0, 53, 25), new SlotData(1, 107, 25));
    public static Machine ASSEMBLER = asBasic("assembler", ITEM).addSlots(new SlotData(0, 17, 16), new SlotData(0, 35, 16), new SlotData(0, 53, 16), new SlotData(0, 17, 34), new SlotData(0, 35, 34), new SlotData(0, 53, 34), new SlotData(1, 107, 25));
    public static Machine BENDER = asBasic("bender", ITEM).addSlots(ALLOY_SMELTER);
    public static Machine CANNER = asBasic("canner", ITEM).addSlots(new SlotData(0, 35, 25), new SlotData(0, 53, 25), new SlotData(1, 107, 25), new SlotData(1, 125, 25));
    public static Machine COMPRESSOR = asBasic("compressor", ITEM).addSlots(new SlotData(0, 53, 25), new SlotData(1, 107, 25));
    public static Machine CUTTER = asBasic("cutter", ITEM).addSlots(new SlotData(0, 53, 25), new SlotData(1, 107, 25), new SlotData(1, 125, 25));
    public static Machine EXTRACTOR = asBasic("extractor", ITEM).addSlots(COMPRESSOR);
    public static Machine EXTRUDER = asBasic("extruder", ITEM).addSlots(ALLOY_SMELTER);
    public static Machine LATHE = asBasic("lathe", ITEM).addSlots(CUTTER);
    public static Machine PULVERIZER = asBasic("pulverizer", ITEM).addSlots(COMPRESSOR);
    public static Machine RECYCLER = asBasic("recycler", ITEM).addSlots(COMPRESSOR);
    public static Machine SCANNER = asBasic("scanner", ITEM, FLUID).addSlots(COMPRESSOR);
    public static Machine WIRE_MILL = asBasic("wire_mill", ITEM).addSlots(COMPRESSOR);
    public static Machine CENTRIFUGE = asBasic("centrifuge", ITEM, FLUID).addSlots(new SlotData(0, 35, 25), new SlotData(0, 53, 25), new SlotData(1, 107, 16), new SlotData(1, 125, 16), new SlotData(1, 143, 16), new SlotData(1, 107, 34), new SlotData(1, 125, 34), new SlotData(1, 143, 34));
    public static Machine ELECTROLYZER = asBasic("electrolyzer", ITEM, FLUID).addSlots(CENTRIFUGE);
    public static Machine THERMAL_CENTRIFUGE = asBasic("thermal_centrifuge", ITEM).addSlots(new SlotData(0, 53, 25), new SlotData(1, 107, 25), new SlotData(1, 125, 25), new SlotData(1, 143, 25));
    public static Machine ORE_WASHER = asBasic("ore_washer", ITEM, FLUID).addSlots(THERMAL_CENTRIFUGE);
    public static Machine CHEMICAL_REACTOR = asBasic("chemical_reactor", ITEM, FLUID).addSlots(CANNER);
    public static Machine FLUID_CANNER = asBasic("fluid_canner", ITEM, FLUID).addSlots(COMPRESSOR);
    public static Machine DISASSEMBLER = asBasic("disassembler", ITEM).addSlots(ALLOY_SMELTER); //TODO
    public static Machine MASS_FABRICATOR = asBasic("mass_fabricator", ITEM, FLUID).addSlots(COMPRESSOR);
    public static Machine AMPLI_FABRICATOR = asBasic("ampli_fabricator", ITEM, FLUID).addSlots(COMPRESSOR);
    public static Machine REPLICATOR = asBasic("replicator", ITEM, FLUID).addSlots(COMPRESSOR);
    public static Machine FERMENTER = asBasic("fermenter", ITEM, FLUID).addSlots(COMPRESSOR);
    public static Machine FLUID_EXTRACTOR = asBasic("fluid_extractor", ITEM, FLUID).addSlots(COMPRESSOR);
    public static Machine FLUID_SOLIDIFIER = asBasic("fluid_solidifier", ITEM, FLUID).addSlots(COMPRESSOR);
    public static Machine DISTILLERY = asBasic("distillery", FLUID).addSlots(COMPRESSOR);
    public static Machine CHEMICAL_BATH = asBasic("chemical_bath", ITEM, FLUID).addSlots(THERMAL_CENTRIFUGE);
    public static Machine AUTOCLAVE = asBasic("autoclave", ITEM, FLUID).addSlots(COMPRESSOR);
    public static Machine MIXER = asBasic("mixer", ITEM, FLUID).addSlots(new SlotData(0, 35, 16), new SlotData(0, 53, 16), new SlotData(0, 35, 34), new SlotData(0, 53, 34), new SlotData(1, 107, 25));
    public static Machine LASER_ENGRAVER = asBasic("laser_engraver", ITEM).addSlots(ALLOY_SMELTER);
    public static Machine FORMING_PRESS = asBasic("forming_press", ITEM).addSlots(ALLOY_SMELTER);
    public static Machine SIFTER = asBasic("sifter", ITEM).addSlots(DISASSEMBLER);
    public static Machine ARC_FURNACE = asBasic("arc_furnace", ITEM, FLUID).addSlots(ALLOY_SMELTER); //TODO
    public static Machine PLASMA_ARC_FURNACE = asBasic("plasma_arc_furnace", ITEM, FLUID).addSlots(ARC_FURNACE);

    public static Machine STEAM_PULVERIZER = asSteam("steam_pulverizer").addSlots(PULVERIZER);

    public static Machine BLAST_FURNACE = asMulti("blast_furnace", TileEntityElectricBlastFurnace.class, ITEM, FLUID);
    public static Machine FUSION_REACTOR = asMulti("fusion_reactor", TileEntityFusionReactor.class, FLUID);

    public static Machine HATCH_ITEM_INPUT = asHatch("item_hatch_input").addSlots(new SlotData(0, 35, 25));
    public static Machine HATCH_ITEM_OUTPUT = asHatch("item_hatch_output").addSlots(new SlotData(1, 35, 25));

    public static void finish() {
        BLAST_FURNACE.addPattern(StructurePattern.BLAST_FURNACE);
        FUSION_REACTOR.addPattern(StructurePattern.FUSION_REACTOR);
    }
    
    public static Machine asBasic(String name, MachineFlag... extraFlags) {
        Machine basic = new Machine(name, ContentLoader.blockMachines, TileEntityBasicMachine.class);
        basic.setTiers(Tier.getStandard());
        basic.addFlags(BASIC, ENERGY, COVERABLE, CONFIGURABLE);
        basic.addFlags(extraFlags);
        basic.addRecipeMap();
        basic.addGUI(Ref.MACHINE_ID, false);
        return basic;
    }
    
    public static Machine asSteam(String name, MachineFlag... extraFlags) {
        Machine steam = asBasic(name, STEAM, FLUID);
        steam.setTileClass(TileEntitySteamMachine.class);
        steam.setTiers(Tier.getSteam());
        steam.addFlags(extraFlags);
        steam.setGuiTierSensitive();
        return steam;
    }

    public static Machine asMulti(String name, Class tileClass, MachineFlag... extraFlags) {
        Machine multi = new Machine(name, ContentLoader.blockMultiMachine, tileClass);
        multi.setTiers(Tier.getMulti());
        multi.addFlags(MULTI);
        multi.addFlags(extraFlags);
//        structurePattern = pattern;
        multi.addRecipeMap();
        multi.addGUI(Ref.MULTI_MACHINE_ID, false);
        return multi;
    }

    public static Machine asHatch(String name, MachineFlag... extraFlags) {
        Machine hatch = new Machine(name, ContentLoader.blockHatch, TileEntityHatch.class);
        hatch.setTiers(Tier.getStandard());
        hatch.addFlags(HATCH);
        hatch.addFlags(extraFlags);
        hatch.addGUI(Ref.HATCH_ID, true);
//        hatch.addStateTextures(ITechProperties.hatchTextures.toArray(new String[0]));
        return hatch;
    }

    public static Machine get(String name) {
        Machine machine = machineTypeLookup.get(name);
        return machine != null ? machine : INVALID;
    }

    public static MachineStack get(String name, String tier) {
        return new MachineStack(get(name), Tier.get(tier));
    }

    public static int getCount() {
        return machineTypeLookup.size();
    }
}
