package muramasa.gregtech.api.data;

import muramasa.gregtech.api.machines.MachineFlag;
import muramasa.gregtech.api.machines.MachineStack;
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

    public static Machine ALLOY_SMELTER = new ItemMachine("alloy_smelter");
    public static Machine ASSEMBLER = new ItemMachine("assembler");
    public static Machine BENDER = new ItemMachine("bender");
    public static Machine CANNER = new ItemMachine("canner");
    public static Machine COMPRESSOR = new ItemMachine("compressor");
    public static Machine CUTTER = new ItemMachine("cutter");
    public static Machine FURNACE = new ItemMachine("furnace");
    public static Machine EXTRACTOR = new ItemMachine("extractor");
    public static Machine EXTRUDER = new ItemMachine("extruder");
    public static Machine LATHE = new ItemMachine("lathe");
    public static Machine PULVERIZER = new ItemMachine("pulverizer");
    public static Machine RECYCLER = new ItemMachine("recycler");
    public static Machine SCANNER = new ItemFluidMachine("scanner");
    public static Machine WIRE_MILL = new ItemMachine("wire_mill");
    public static Machine CENTRIFUGE = new ItemFluidMachine("centrifuge");
    public static Machine ELECTROLYZER = new ItemFluidMachine("electrolyzer");
    public static Machine THERMAL_CENTRIFUGE = new ItemMachine("thermal_centrifuge");
    public static Machine ORE_WASHER = new ItemFluidMachine("ore_washer");
    public static Machine CHEMICAL_REACTOR = new ItemFluidMachine("chemical_reactor");
    public static Machine FLUID_CANNER = new ItemFluidMachine("fluid_canner");
    public static Machine DISASSEMBLER = new ItemMachine("disassembler");
    public static Machine MASS_FABRICATOR = new ItemFluidMachine("mass_fabricator");
    public static Machine AMP_FABRICATOR = new ItemMachine("amp_fabricator");
    public static Machine REPLICATOR = new ItemFluidMachine("replicator");
    public static Machine FERMENTER = new ItemFluidMachine("fermenter");
    public static Machine FLUID_EXTRACTOR = new ItemMachine("fluid_extractor");
    public static Machine FLUID_SOLIDIFIER = new ItemFluidMachine("fluid_solidifier");
    public static Machine DISTILLERY = new ItemFluidMachine("distillery");
    public static Machine CHEMICAL_BATH = new ItemFluidMachine("chemical_bath");
    public static Machine AUTOCLAVE = new ItemFluidMachine("autoclave");
    public static Machine MIXER = new ItemFluidMachine("mixer");
    public static Machine LASER_ENGRAVER = new ItemMachine("laser_engraver");
    public static Machine FORMING_PRESS = new ItemMachine("forming_press");
    public static Machine FORGE_HAMMER = new ItemMachine("forge_hammer");
    public static Machine SIFTER = new ItemMachine("sifter");
    public static Machine ARC_FURNACE = new ItemFluidMachine("arc_furnace");
    public static Machine PLASMA_ARC_FURNACE = new ItemFluidMachine("plasma_arc_furnace");

    public static Machine COAL_BOILER = new SteamMachine("coal_boiler"); //TODO
    public static Machine LAVA_BOILER = new SteamMachine("lava_boiler", Tier.STEEL); //TODO
    public static Machine SOLAR_BOILER = new SteamMachine("solar_boiler", Tier.BRONZE); //TODO
    public static Machine STEAM_FURNACE = new SteamMachine("steam_furnace");
    public static Machine STEAM_PULVERIZER = new SteamMachine("steam_pulverizer");
    public static Machine STEAM_EXTRACTOR = new SteamMachine("steam_extractor");
    public static Machine STEAM_FORGE_HAMMER = new SteamMachine("steam_forge_hammer");
    public static Machine STEAM_COMPRESSOR = new SteamMachine("steam_compressor");
    public static Machine STEAM_ALLOY_SMELTER = new SteamMachine("steam_alloy_smelter");

    public static Machine CHARCOAL_PIT = new MultiMachine("charcoal_pit", TileEntityCharcoalPit.class);
    public static Machine PRIMITIVE_BLAST_FURNACE = new MultiMachine("primitive_blast_furnace", TileEntityPrimitiveBlastFurnace.class, ITEM);
    public static Machine BRONZE_BLAST_FURNACE = new MultiMachine("bronze_blast_furnace", TileEntityBronzeBlastFurnace.class, ITEM);
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

    public static Machine HATCH_ITEM_INPUT = new HatchMachine("hatch_item_input", GUI, ITEM);
    public static Machine HATCH_ITEM_OUTPUT = new HatchMachine("hatch_item_output", GUI, ITEM);
    public static Machine HATCH_FLUID_INPUT = new HatchMachine("hatch_fluid_input", GUI, FLUID);
    public static Machine HATCH_FLUID_OUTPUT = new HatchMachine("hatch_fluid_output", GUI, FLUID);
    public static Machine HATCH_MUFFLER = new HatchMachine("hatch_muffler", GUI, ITEM);
    public static Machine HATCH_DYNAMO = new HatchMachine("hatch_dynamo", ENERGY);
    public static Machine HATCH_ENERGY = new HatchMachine("hatch_energy", ENERGY);

    public static void init() {

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
