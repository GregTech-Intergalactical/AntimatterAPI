package muramasa.gregtech.api.data;

import muramasa.gregtech.api.machines.*;
import muramasa.gregtech.api.structure.StructurePattern;
import muramasa.gregtech.common.tileentities.base.TileEntityMachine;
import muramasa.gregtech.common.tileentities.overrides.multi.TileEntityElectricBlastFurnace;
import muramasa.gregtech.common.tileentities.overrides.multi.TileEntityFusionReactor;
import muramasa.gregtech.loaders.ContentLoader;

import java.util.HashMap;

import static muramasa.gregtech.api.enums.MachineFlag.*;

public class Machines {

    public static HashMap<String, Machine> machineTypeLookup = new HashMap<>();

    public static Machine INVALID = new Machine("invalid", ContentLoader.blockMachines, TileEntityMachine.class).setTiers(Tier.LV).setBlock(ContentLoader.blockMachines);

    public static Machine ALLOY_SMELTER = new BasicMachine("alloy_smelter", ITEM).addSlots(new SlotData(0, 35, 25), new SlotData(0, 53, 25), new SlotData(1, 107, 25));
    public static Machine ASSEMBLER = new BasicMachine("assembler", ITEM).addSlots(new SlotData(0, 17, 16), new SlotData(0, 35, 16), new SlotData(0, 53, 16), new SlotData(0, 17, 34), new SlotData(0, 35, 34), new SlotData(0, 53, 34), new SlotData(1, 107, 25));
    public static Machine BENDER = new BasicMachine("bender", ITEM).addSlots(ALLOY_SMELTER);
    public static Machine CANNER = new BasicMachine("canner", ITEM).addSlots(new SlotData(0, 35, 25), new SlotData(0, 53, 25), new SlotData(1, 107, 25), new SlotData(1, 125, 25));
    public static Machine COMPRESSOR = new BasicMachine("compressor", ITEM).addSlots(new SlotData(0, 53, 25), new SlotData(1, 107, 25));
    public static Machine CUTTER = new BasicMachine("cutter", ITEM).addSlots(new SlotData(0, 53, 25), new SlotData(1, 107, 25), new SlotData(1, 125, 25));
    public static Machine EXTRACTOR = new BasicMachine("extractor", ITEM).addSlots(COMPRESSOR);
    public static Machine EXTRUDER = new BasicMachine("extruder", ITEM).addSlots(ALLOY_SMELTER);
    public static Machine LATHE = new BasicMachine("lathe", ITEM).addSlots(CUTTER);
    public static Machine PULVERIZER = new BasicMachine("pulverizer", ITEM).addSlots(COMPRESSOR);
    public static Machine RECYCLER = new BasicMachine("recycler", ITEM).addSlots(COMPRESSOR);
    public static Machine SCANNER = new BasicMachine("scanner", ITEM, FLUID).addSlots(COMPRESSOR);
    public static Machine WIRE_MILL = new BasicMachine("wire_mill", ITEM).addSlots(COMPRESSOR);
    public static Machine CENTRIFUGE = new BasicMachine("centrifuge", ITEM, FLUID).addSlots(new SlotData(0, 35, 25), new SlotData(0, 53, 25), new SlotData(1, 107, 16), new SlotData(1, 125, 16), new SlotData(1, 143, 16), new SlotData(1, 107, 34), new SlotData(1, 125, 34), new SlotData(1, 143, 34));
    public static Machine ELECTROLYZER = new BasicMachine("electrolyzer", ITEM, FLUID).addSlots(CENTRIFUGE);
    public static Machine THERMAL_CENTRIFUGE = new BasicMachine("thermal_centrifuge", ITEM).addSlots(new SlotData(0, 53, 25), new SlotData(1, 107, 25), new SlotData(1, 125, 25), new SlotData(1, 143, 25));
    public static Machine ORE_WASHER = new BasicMachine("ore_washer", ITEM, FLUID).addSlots(THERMAL_CENTRIFUGE);
    public static Machine CHEMICAL_REACTOR = new BasicMachine("chemical_reactor", ITEM, FLUID).addSlots(CANNER);
    public static Machine FLUID_CANNER = new BasicMachine("fluid_canner", ITEM, FLUID).addSlots(COMPRESSOR);
    public static Machine DISASSEMBLER = new BasicMachine("disassembler", ITEM).addSlots(ALLOY_SMELTER); //TODO
    public static Machine MASS_FABRICATOR = new BasicMachine("mass_fabricator", ITEM, FLUID).addSlots(COMPRESSOR);
    public static Machine AMP_FABRICATOR = new BasicMachine("amp_fabricator", ITEM, FLUID).addSlots(COMPRESSOR);
    public static Machine REPLICATOR = new BasicMachine("replicator", ITEM, FLUID).addSlots(COMPRESSOR);
    public static Machine FERMENTER = new BasicMachine("fermenter", ITEM, FLUID).addSlots(COMPRESSOR);
    public static Machine FLUID_EXTRACTOR = new BasicMachine("fluid_extractor", ITEM, FLUID).addSlots(COMPRESSOR);
    public static Machine FLUID_SOLIDIFIER = new BasicMachine("fluid_solidifier", ITEM, FLUID).addSlots(COMPRESSOR);
    public static Machine DISTILLERY = new BasicMachine("distillery", FLUID).addSlots(COMPRESSOR);
    public static Machine CHEMICAL_BATH = new BasicMachine("chemical_bath", ITEM, FLUID).addSlots(THERMAL_CENTRIFUGE);
    public static Machine AUTOCLAVE = new BasicMachine("autoclave", ITEM, FLUID).addSlots(COMPRESSOR);
    public static Machine MIXER = new BasicMachine("mixer", ITEM, FLUID).addSlots(new SlotData(0, 35, 16), new SlotData(0, 53, 16), new SlotData(0, 35, 34), new SlotData(0, 53, 34), new SlotData(1, 107, 25));
    public static Machine LASER_ENGRAVER = new BasicMachine("laser_engraver", ITEM).addSlots(ALLOY_SMELTER);
    public static Machine FORMING_PRESS = new BasicMachine("forming_press", ITEM).addSlots(ALLOY_SMELTER);
    public static Machine SIFTER = new BasicMachine("sifter", ITEM).addSlots(DISASSEMBLER);
    public static Machine ARC_FURNACE = new BasicMachine("arc_furnace", ITEM, FLUID).addSlots(ALLOY_SMELTER); //TODO
    public static Machine PLASMA_ARC_FURNACE = new BasicMachine("plasma_arc_furnace", ITEM, FLUID).addSlots(ARC_FURNACE);

    public static Machine STEAM_PULVERIZER = new SteamMachine("steam_pulverizer").addSlots(PULVERIZER);

    public static Machine BLAST_FURNACE = new MultiMachine("blast_furnace", TileEntityElectricBlastFurnace.class, ITEM, FLUID);
    public static Machine FUSION_REACTOR = new MultiMachine("fusion_reactor", TileEntityFusionReactor.class, FLUID);

    public static Machine HATCH_ITEM_INPUT = new HatchMachine("item_hatch_input").addSlots(new SlotData(0, 35, 25));
    public static Machine HATCH_ITEM_OUTPUT = new HatchMachine("item_hatch_output").addSlots(new SlotData(1, 35, 25));

    public static void finish() {
        BLAST_FURNACE.addPattern(StructurePattern.BLAST_FURNACE);
        FUSION_REACTOR.addPattern(StructurePattern.FUSION_REACTOR);
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
