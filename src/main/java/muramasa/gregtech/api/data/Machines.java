package muramasa.gregtech.api.data;

import muramasa.gregtech.api.machines.MachineFlag;
import muramasa.gregtech.api.machines.MachineStack;
import muramasa.gregtech.api.machines.Slot;
import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.api.machines.types.*;
import muramasa.gregtech.common.tileentities.overrides.multi.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

import static muramasa.gregtech.api.machines.MachineFlag.*;

public class Machines {

    private static LinkedHashMap<String, Machine> TYPE_LOOKUP = new LinkedHashMap<>();
    private static ArrayList<Machine> ID_LOOKUP = new ArrayList<>();

    public static Machine INVALID = new Machine("invalid");

    public static Machine ALLOY_SMELTER = new BasicMachine("alloy_smelter", new Slot(0, 35, 25), new Slot(0, 53, 25), new Slot(1, 107, 25));
    public static Machine ASSEMBLER = new BasicMachine("assembler", new Slot(0, 17, 16), new Slot(0, 35, 16), new Slot(0, 53, 16), new Slot(0, 17, 34), new Slot(0, 35, 34), new Slot(0, 53, 34), new Slot(1, 107, 25));
    public static Machine BENDER = new BasicMachine("bender", ALLOY_SMELTER);
    public static Machine CANNER = new BasicMachine("canner", new Slot(0, 35, 25), new Slot(0, 53, 25), new Slot(1, 107, 25), new Slot(1, 125, 25));
    public static Machine COMPRESSOR = new BasicMachine("compressor", new Slot(0, 53, 25), new Slot(1, 107, 25));
    public static Machine CUTTER = new BasicMachine("cutter", new Slot(0, 53, 25), new Slot(1, 107, 25), new Slot(1, 125, 25));
    public static Machine FURNACE = new BasicMachine("furnace", ALLOY_SMELTER); //TODO
    public static Machine EXTRACTOR = new BasicMachine("extractor", COMPRESSOR);
    public static Machine EXTRUDER = new BasicMachine("extruder", ALLOY_SMELTER);
    public static Machine LATHE = new BasicMachine("lathe", CUTTER);
    public static Machine PULVERIZER = new BasicMachine("pulverizer", COMPRESSOR);
    public static Machine RECYCLER = new BasicMachine("recycler", COMPRESSOR);
    public static Machine SCANNER = new ItemFluidMachine("scanner", COMPRESSOR);
    public static Machine WIRE_MILL = new BasicMachine("wire_mill", COMPRESSOR);
    public static Machine CENTRIFUGE = new ItemFluidMachine("centrifuge", new Slot(0, 35, 25), new Slot(0, 53, 25), new Slot(1, 107, 16), new Slot(1, 125, 16), new Slot(1, 143, 16), new Slot(1, 107, 34), new Slot(1, 125, 34), new Slot(1, 143, 34));
    public static Machine ELECTROLYZER = new ItemFluidMachine("electrolyzer", CENTRIFUGE);
    public static Machine THERMAL_CENTRIFUGE = new BasicMachine("thermal_centrifuge", new Slot(0, 53, 25), new Slot(1, 107, 25), new Slot(1, 125, 25), new Slot(1, 143, 25));
    public static Machine ORE_WASHER = new ItemFluidMachine("ore_washer", THERMAL_CENTRIFUGE, new Slot(2, 53, 64));
    public static Machine CHEMICAL_REACTOR = new ItemFluidMachine("chemical_reactor", CANNER);
    public static Machine FLUID_CANNER = new ItemFluidMachine("fluid_canner", COMPRESSOR);
    public static Machine DISASSEMBLER = new BasicMachine("disassembler", ALLOY_SMELTER); //TODO
    public static Machine MASS_FABRICATOR = new ItemFluidMachine("mass_fabricator", COMPRESSOR);
    public static Machine AMP_FABRICATOR = new BasicMachine("amp_fabricator", COMPRESSOR);
    public static Machine REPLICATOR = new ItemFluidMachine("replicator", COMPRESSOR);
    public static Machine FERMENTER = new ItemFluidMachine("fermenter", COMPRESSOR);
    public static Machine FLUID_EXTRACTOR = new BasicMachine("fluid_extractor", COMPRESSOR);
    public static Machine FLUID_SOLIDIFIER = new ItemFluidMachine("fluid_solidifier", COMPRESSOR);
    public static Machine DISTILLERY = new ItemFluidMachine("distillery", COMPRESSOR);
    public static Machine CHEMICAL_BATH = new ItemFluidMachine("chemical_bath", THERMAL_CENTRIFUGE);
    public static Machine AUTOCLAVE = new ItemFluidMachine("autoclave", COMPRESSOR);
    public static Machine MIXER = new ItemFluidMachine("mixer", new Slot(0, 35, 16), new Slot(0, 53, 16), new Slot(0, 35, 34), new Slot(0, 53, 34), new Slot(1, 107, 25));
    public static Machine LASER_ENGRAVER = new BasicMachine("laser_engraver", ALLOY_SMELTER);
    public static Machine FORMING_PRESS = new BasicMachine("forming_press", ALLOY_SMELTER);
    public static Machine FORGE_HAMMER = new BasicMachine("forge_hammer", ALLOY_SMELTER); //TODO
    public static Machine SIFTER = new BasicMachine("sifter", DISASSEMBLER);
    public static Machine ARC_FURNACE = new ItemFluidMachine("arc_furnace", ALLOY_SMELTER); //TODO
    public static Machine PLASMA_ARC_FURNACE = new ItemFluidMachine("plasma_arc_furnace", ARC_FURNACE);

    public static Machine COAL_BOILER = new SteamMachine("coal_boiler", ALLOY_SMELTER); //TODO
    public static Machine LAVA_BOILER = new SteamMachine("lava_boiler", ALLOY_SMELTER).setTiers(Tier.STEEL); //TODO
    public static Machine SOLAR_BOILER = new SteamMachine("solar_boiler", ALLOY_SMELTER).setTiers(Tier.BRONZE); //TODO
    public static Machine STEAM_FURNACE = new SteamMachine("steam_furnace", FURNACE);
    public static Machine STEAM_PULVERIZER = new SteamMachine("steam_pulverizer", PULVERIZER);
    public static Machine STEAM_EXTRACTOR = new SteamMachine("steam_extractor", EXTRACTOR);
    public static Machine STEAM_FORGE_HAMMER = new SteamMachine("steam_forge_hammer", FORGE_HAMMER);
    public static Machine STEAM_COMPRESSOR = new SteamMachine("steam_compressor", COMPRESSOR);
    public static Machine STEAM_ALLOY_SMELTER = new SteamMachine("steam_alloy_smelter", ALLOY_SMELTER);

    public static Machine CHARCOAL_PIT = new MultiMachine("charcoal_pit", TileEntityCharcoalPit.class);
    public static Machine PRIMITIVE_BLAST_FURNACE = new MultiMachine("primitive_blast_furnace", TileEntityPrimitiveBlastFurnace.class, ITEM).addSlots(new Slot(0, 35, 25), new Slot(0, 53, 25), new Slot(1, 107, 25), new Slot(1, 125, 25));
    public static Machine BRONZE_BLAST_FURNACE = new MultiMachine("bronze_blast_furnace", TileEntityBronzeBlastFurnace.class, ITEM).addSlots(new Slot(0, 35, 25), new Slot(0, 53, 25), new Slot(1, 107, 25), new Slot(1, 125, 25));
    public static Machine ELECTRIC_BLAST_FURNACE = new MultiMachine("electric_blast_furnace", TileEntityElectricBlastFurnace.class);
    public static Machine IMPLOSION_COMPRESSOR = new MultiMachine("implosion_compressor", TileEntityImplosionCompressor.class);
    public static Machine VACUUM_FREEZER = new MultiMachine("vacuum_freezer", TileEntityVacuumFreezer.class);
    public static Machine MULTI_SMELTER = new MultiMachine("multi_smelter", TileEntityMultiSmelter.class);
    public static Machine LARGE_BOILER_1 = new MultiMachine("large_boiler_1", TileEntityLargeBoiler.class);
    public static Machine LARGE_BOILER_2 = new MultiMachine("large_boiler_2", TileEntityLargeBoiler.class);
    public static Machine LARGE_BOILER_3 = new MultiMachine("large_boiler_3", TileEntityLargeBoiler.class);
    public static Machine LARGE_BOILER_4 = new MultiMachine("large_boiler_4", TileEntityLargeBoiler.class);
    public static Machine LARGE_TURBINE_1 = new MultiMachine("large_turbine_1", TileEntityLargeTurbine.class);
    public static Machine LARGE_TURBINE_2 = new MultiMachine("large_turbine_2", TileEntityLargeTurbine.class);
    public static Machine LARGE_TURBINE_3 = new MultiMachine("large_turbine_3", TileEntityLargeTurbine.class);
    public static Machine LARGE_TURBINE_4 = new MultiMachine("large_turbine_4", TileEntityLargeTurbine.class);
    public static Machine HEAT_EXCHANGER = new MultiMachine("heat_exchanger", TileEntityHeatExchanger.class);
    public static Machine OIL_DRILLING_RIG = new MultiMachine("oil_drilling_rig", TileEntityOilDrillingRig.class);
    public static Machine OIL_CRACKING_UNIT = new MultiMachine("oil_cracking_unit", TileEntityOilCrackingUnit.class);
    public static Machine ADVANCED_MINER = new MultiMachine("advanced_miner", TileEntityAdvancedMiner.class);
    public static Machine PYROLYSIS_OVEN = new MultiMachine("pyrolysis_oven", TileEntityPyrolysisOven.class);
    public static Machine COMBUSTION_ENGINE = new MultiMachine("combustion_engine", TileEntityCombustionEngine.class);
    public static Machine FUSION_REACTOR_1 = new MultiMachine("fusion_reactor_1", TileEntityFusionReactor.class);
    public static Machine FUSION_REACTOR_2 = new MultiMachine("fusion_reactor_2", TileEntityFusionReactor.class);
    public static Machine FUSION_REACTOR_3 = new MultiMachine("fusion_reactor_3", TileEntityFusionReactor.class);

    public static Machine HATCH_ITEM_INPUT = new HatchMachine("hatch_item_input", ITEM, new Slot(0, 35, 25));
    public static Machine HATCH_ITEM_OUTPUT = new HatchMachine("hatch_item_output", ITEM, new Slot(1, 35, 25));
    public static Machine HATCH_FLUID_INPUT = new HatchMachine("hatch_fluid_input", FLUID, new Slot(2, 35, 25));
    public static Machine HATCH_FLUID_OUTPUT = new HatchMachine("hatch_fluid_output", FLUID, new Slot(2, 35, 25));
    public static Machine HATCH_DYNAMO = new HatchMachine("hatch_dynamo", ENERGY);
    public static Machine HATCH_ENERGY = new HatchMachine("hatch_energy", ENERGY);
    public static Machine HATCH_MUFFLER = new HatchMachine("hatch_muffler", ITEM, new Slot(0, 35, 25));

    public static void init() {
        PRIMITIVE_BLAST_FURNACE.addPattern(Structures.STRUCTURE_PRIMITIVE_BLAST_FURNAVE);
        BRONZE_BLAST_FURNACE.addPattern(Structures.STRUCTURE_BRONZE_BLAST_FURNACE);
        ELECTRIC_BLAST_FURNACE.addPattern(Structures.STRUCTURE_BLAST_FURNACE);
        VACUUM_FREEZER.addPattern(Structures.STRUCTURE_VACUUM_FREEZER);
        FUSION_REACTOR_1.addPattern(Structures.PATTERN_FUSION_REACTOR);
    }

    public static void add(Machine machine) {
        TYPE_LOOKUP.put(machine.getName(), machine);
        ID_LOOKUP.add(machine.getInternalId(), machine);
    }

    public static Machine get(String name) {
        Machine machine = TYPE_LOOKUP.get(name);
        return machine != null ? machine : INVALID;
    }

    public static Machine get(int id) {
        Machine machine = ID_LOOKUP.get(id);
        return machine != null ? machine : INVALID;
    }

    public static MachineStack get(Machine type, Tier tier) {
        return new MachineStack(type, tier);
    }

    public static Collection<Machine> getAll() {
        return TYPE_LOOKUP.values();
    }

    public static int getCount() {
        return TYPE_LOOKUP.size();
    }

    public static Collection<Machine> getTypes(MachineFlag... flags) {
        ArrayList<Machine> types = new ArrayList<>();
        for (MachineFlag flag : flags) {
            types.addAll(flag.getTypes());
        }
        return types;
    }

    public static Collection<MachineStack> getStacks(MachineFlag... flags) {
        ArrayList<MachineStack> stacks = new ArrayList<>();
        for (MachineFlag flag : flags) {
            stacks.addAll(flag.getStacks());
        }
        return stacks;
    }
}
