package muramasa.gtu.api.data;

import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.machines.MachineFlag;
import muramasa.gtu.api.machines.types.*;
import muramasa.gtu.api.tileentities.TileEntitySteamMachine;
import muramasa.gtu.common.tileentities.multi.*;
import net.minecraft.tileentity.TileEntityType;

import java.util.ArrayList;
import java.util.Collection;

import static muramasa.gtu.api.data.RecipeMaps.*;
import static muramasa.gtu.api.machines.MachineFlag.*;
import static muramasa.gtu.api.machines.Tier.*;

public class Machines {

    private static ArrayList<Machine> ID_LOOKUP = new ArrayList<>();

    public static Machine INVALID = new Machine("invalid");

    public static BasicMachine ALLOY_SMELTER = new BasicMachine("alloy_smelter", ALLOY_SMELTING, ITEM);
    public static BasicMachine ASSEMBLER = new BasicMachine("assembler", ASSEMBLING, ITEM);
    public static BasicMachine BENDER = new BasicMachine("bender", BENDING, ITEM);
    public static BasicMachine CANNER = new BasicMachine("canner", CANNING, ITEM);
    public static BasicMachine COMPRESSOR = new BasicMachine("compressor", COMPRESSING, ITEM);
    public static BasicMachine CUTTER = new BasicMachine("cutter", CUTTING, ITEM);
    public static BasicMachine FURNACE = new BasicMachine("furnace", SMELTING, ITEM);
    public static BasicMachine EXTRACTOR = new BasicMachine("extractor", EXTRACTING, ITEM);
    public static BasicMachine EXTRUDER = new BasicMachine("extruder", EXTRUDING, ITEM);
    public static BasicMachine LATHE = new BasicMachine("lathe", LATHING, ITEM);
    public static BasicMachine PULVERIZER = new BasicMachine("pulverizer", PULVERIZING, ITEM);
    public static BasicMachine RECYCLER = new BasicMachine("recycler", RECYCLING, ITEM);
    public static BasicMachine SCANNER = new BasicMachine("scanner", SCANNING, ITEM, FLUID);
    public static BasicMachine WIRE_MILL = new BasicMachine("wire_mill", WIRE_MILLING, ITEM);
    public static BasicMachine CENTRIFUGE = new BasicMachine("centrifuge", CENTRIFUGING, ITEM, FLUID);
    public static BasicMachine ELECTROLYZER = new BasicMachine("electrolyzer", ELECTROLYZING, ITEM, FLUID);
    public static BasicMachine THERMAL_CENTRIFUGE = new BasicMachine("thermal_centrifuge", THERMAL_CENTRIFUGING, ITEM);
    public static BasicMachine ORE_WASHER = new BasicMachine("ore_washer", ORE_WASHING, ITEM, FLUID);
    public static BasicMachine CHEMICAL_REACTOR = new BasicMachine("chemical_reactor", CHEMICAL_REACTING, ITEM, FLUID);
    public static BasicMachine FLUID_CANNER = new BasicMachine("fluid_canner", FLUID_CANNING, ITEM, FLUID);
    public static BasicMachine DISASSEMBLER = new BasicMachine("disassembler", DISASSEMBLING, ITEM);
    public static BasicMachine MASS_FABRICATOR = new BasicMachine("mass_fabricator", MASS_FABRICATING, ITEM, FLUID);
    public static BasicMachine AMP_FABRICATOR = new BasicMachine("amp_fabricator", AMP_FABRICATING, ITEM);
    public static BasicMachine REPLICATOR = new BasicMachine("replicator", REPLICATING, ITEM, FLUID);
    public static BasicMachine FERMENTER = new BasicMachine("fermenter", FERMENTING, ITEM, FLUID);
    public static BasicMachine FLUID_EXTRACTOR = new BasicMachine("fluid_extractor", FLUID_EXTRACTING, ITEM, FLUID);
    public static BasicMachine FLUID_SOLIDIFIER = new BasicMachine("fluid_solidifier", FLUID_SOLIDIFYING, ITEM, FLUID);
    public static BasicMachine DISTILLERY = new BasicMachine("distillery", DISTILLING, ITEM, FLUID);
    public static BasicMachine CHEMICAL_BATH = new BasicMachine("chemical_bath", CHEMICAL_BATHING, ITEM, FLUID);
    public static BasicMachine AUTOCLAVE = new BasicMachine("autoclave", AUTOCLAVING, ITEM, FLUID);
    public static BasicMachine MIXER = new BasicMachine("mixer", MIXING, ITEM, FLUID);
    public static BasicMachine LASER_ENGRAVER = new BasicMachine("laser_engraver", LASER_ENGRAVING, ITEM);
    public static BasicMachine FORMING_PRESS = new BasicMachine("forming_press", PRESSING, ITEM);
    public static BasicMachine FORGE_HAMMER = new BasicMachine("forge_hammer", HAMMERING, ITEM);
    public static BasicMachine SIFTER = new BasicMachine("sifter", SIFTING, ITEM);
    public static BasicMachine ARC_FURNACE = new BasicMachine("arc_furnace", ARC_SMELTING, ITEM, FLUID);
    public static BasicMachine PLASMA_ARC_FURNACE = new BasicMachine("plasma_arc_furnace", PLASMA_ARC_SMELTING, ITEM, FLUID);

    public static BasicMachine COAL_BOILER = new BasicMachine("coal_boiler", SMALL_BOILERS, BRONZE, STEEL, STEAM, ITEM, FLUID, Textures.BOILER_HANDLER);
    public static BasicMachine LAVA_BOILER = new BasicMachine("lava_boiler", SMALL_BOILERS, STEEL, STEAM, ITEM, FLUID); //TODO
    public static BasicMachine SOLAR_BOILER = new BasicMachine("solar_boiler", SMALL_BOILERS, BRONZE, STEAM, ITEM, FLUID); //TODO
    public static BasicMachine STEAM_FURNACE = new BasicMachine("steam_furnace", TileEntitySteamMachine.class, BRONZE, STEEL, STEAM, ITEM, FLUID, STEAM_SMELTING);
    public static BasicMachine STEAM_PULVERIZER = new BasicMachine("steam_pulverizer", TileEntitySteamMachine.class, BRONZE, STEEL, STEAM, ITEM, FLUID, STEAM_PULVERIZING);
    public static BasicMachine STEAM_EXTRACTOR = new BasicMachine("steam_extractor", TileEntitySteamMachine.class, BRONZE, STEEL, STEAM, ITEM, FLUID, STEAM_EXTRACTING);
    public static BasicMachine STEAM_FORGE_HAMMER = new BasicMachine("steam_forge_hammer", TileEntitySteamMachine.class, BRONZE, STEEL, STEAM, ITEM, FLUID, STEAM_HAMMERING);
    public static BasicMachine STEAM_COMPRESSOR = new BasicMachine("steam_compressor", TileEntitySteamMachine.class, BRONZE, STEEL, STEAM, ITEM, FLUID, STEAM_COMPRESSING);
    public static BasicMachine STEAM_ALLOY_SMELTER = new BasicMachine("steam_alloy_smelter", TileEntitySteamMachine.class, BRONZE, STEEL, STEAM, ITEM, FLUID, STEAM_ALLOY_SMELTING);

    public static MultiMachine COKE_OVEN = new MultiMachine("coke_oven", TileEntityCokeOven::new, COKING, LV, ITEM);
    public static MultiMachine PRIMITIVE_BLAST_FURNACE = new MultiMachine("primitive_blast_furnace", TileEntityPrimitiveBlastFurnace::new, BASIC_BLASTING, LV, ITEM);
    public static MultiMachine BRONZE_BLAST_FURNACE = new MultiMachine("bronze_blast_furnace", TileEntityBronzeBlastFurnace::new, BASIC_BLASTING, LV, ITEM);
    public static MultiMachine CHARCOAL_PIT = new MultiMachine("charcoal_pit", TileEntityCharcoalPit::new, LV);
    public static MultiMachine BLAST_FURNACE = new MultiMachine("electric_blast_furnace", TileEntityElectricBlastFurnace::new, BLASTING, LV, ITEM, FLUID);
    public static MultiMachine IMPLOSION_COMPRESSOR = new MultiMachine("implosion_compressor", TileEntityImplosionCompressor::new, IMPLOSION_COMPRESSING, HV, ITEM);
    public static MultiMachine VACUUM_FREEZER = new MultiMachine("vacuum_freezer", TileEntityVacuumFreezer::new, VACUUM_FREEZING, HV, ITEM, FLUID);
    public static MultiMachine MULTI_SMELTER = new MultiMachine("multi_smelter", TileEntityMultiSmelter::new, HV, ITEM);
    public static MultiMachine LARGE_BOILER = new MultiMachine("large_boiler", TileEntityLargeBoiler::new, LV, MV, HV, EV, ITEM, FLUID);
    public static MultiMachine LARGE_TURBINE = new MultiMachine("large_turbine", TileEntityLargeTurbine::new, HV, EV, IV, UV, FLUID);
    public static MultiMachine HEAT_EXCHANGER = new MultiMachine("heat_exchanger", TileEntityHeatExchanger::new, EV, FLUID);
    public static MultiMachine OIL_DRILLING_RIG = new MultiMachine("oil_drilling_rig", TileEntityOilDrillingRig::new, EV, IV, LUV, ZPM, FLUID);
    public static MultiMachine OIL_CRACKING_UNIT = new MultiMachine("oil_cracking_unit", TileEntityOilCrackingUnit::new, HV, FLUID);
    public static MultiMachine ADVANCED_MINER = new MultiMachine("advanced_miner", TileEntityAdvancedMiner::new, LV, ITEM);
    public static MultiMachine PYROLYSIS_OVEN = new MultiMachine("pyrolysis_oven", TileEntityPyrolysisOven::new, MV, ITEM, FLUID);
    public static MultiMachine COMBUSTION_ENGINE = new MultiMachine("combustion_engine", TileEntityCombustionEngine::new, COMBUSTION_FUELS, EV, FLUID);
    public static MultiMachine FUSION_REACTOR = new MultiMachine("fusion_reactor", TileEntityFusionReactor::new, FUSION, LUV, ZPM, UV, FLUID);

    public static HatchMachine HATCH_ITEM_I = new HatchMachine("hatch_item_input", GUI, ITEM);
    public static HatchMachine HATCH_ITEM_O = new HatchMachine("hatch_item_output", GUI, ITEM);
    public static HatchMachine HATCH_FLUID_I = new HatchMachine("hatch_fluid_input", GUI, FLUID);
    public static HatchMachine HATCH_FLUID_O = new HatchMachine("hatch_fluid_output", GUI, FLUID);
    public static HatchMachine HATCH_MUFFLER = new HatchMachine("hatch_muffler", GUI, ITEM);
    public static HatchMachine HATCH_DYNAMO = new HatchMachine("hatch_dynamo", ENERGY);
    public static HatchMachine HATCH_ENERGY = new HatchMachine("hatch_energy", ENERGY);

    public static TankMachine QUANTUM_TANK = new TankMachine("quantum_tank");

    public static BasicMachine STEAM_GENERATOR = new BasicMachine("steam_generator", STEAM_FUELS, LV, MV, HV, ITEM, FLUID);
    public static BasicMachine GAS_GENERATOR = new BasicMachine("gas_generator", GAS_FUELS, LV, MV, HV, ITEM, FLUID);
    public static BasicMachine COMBUSTION_GENERATOR = new BasicMachine("combustion_generator", COMBUSTION_FUELS, LV, MV, HV, ITEM, FLUID);
    public static BasicMachine NAQUADAH_GENERATOR = new BasicMachine("naquadah_generator", NAQUADAH_FUELS, EV, IV, LUV, ITEM, FLUID);
    public static BasicMachine PLASMA_GENERATOR = new BasicMachine("plasma_generator", PLASMA_FUELS, IV, LUV, ZPM, ITEM, FLUID);

    public static void init() {

    }

    public static void add(Machine machine) {
        GregTechAPI.register(Machine.class, machine);
        GregTechAPI.register(TileEntityType.class, machine.getId(), machine.getTileType());
        ID_LOOKUP.add(machine.getInternalId(), machine);
    }

    public static Machine get(String name) {
        Machine machine = GregTechAPI.get(Machine.class, name);
        return machine != null ? machine : INVALID;
    }

    public static Machine get(int id) {
        Machine machine = ID_LOOKUP.get(id);
        return machine != null ? machine : INVALID;
    }

//    public static MachineStack get(Machine type, Tier tier) {
//        return new MachineStack(type, tier);
//    }

    public static Collection<Machine> getTypes(MachineFlag... flags) {
        ArrayList<Machine> types = new ArrayList<>();
        for (MachineFlag flag : flags) {
            types.addAll(flag.getTypes());
        }
        return types;
    }

//    public static Collection<MachineStack> getStacks(MachineFlag... flags) {
//        ArrayList<MachineStack> stacks = new ArrayList<>();
//        for (MachineFlag flag : flags) {
//            stacks.addAll(flag.getStacks());
//        }
//        return stacks;
//    }
}
