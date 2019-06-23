package muramasa.gtu.api.data;

import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.machines.MachineFlag;
import muramasa.gtu.api.machines.MachineStack;
import muramasa.gtu.api.machines.Tier;
import muramasa.gtu.api.machines.types.*;
import muramasa.gtu.api.recipe.RecipeBuilder;
import muramasa.gtu.api.recipe.RecipeBuilders;
import muramasa.gtu.common.tileentities.multi.*;

import java.util.ArrayList;
import java.util.Collection;

import static muramasa.gtu.api.machines.MachineFlag.*;
import static muramasa.gtu.api.machines.Tier.*;

public class Machines {

    private static ArrayList<Machine> ID_LOOKUP = new ArrayList<>();

    public static Machine INVALID = new Machine("invalid");

    public static BasicMachine ALLOY_SMELTER = new BasicMachine<>("alloy_smelter", new RecipeBuilder(), ITEM);
    public static BasicMachine ASSEMBLER = new BasicMachine<>("assembler", new RecipeBuilder(), ITEM);
    public static BasicMachine BENDER = new BasicMachine<>("bender", new RecipeBuilder(), ITEM);
    public static BasicMachine CANNER = new BasicMachine<>("canner", new RecipeBuilder(), ITEM);
    public static BasicMachine COMPRESSOR = new BasicMachine<>("compressor", new RecipeBuilder(), ITEM);
    public static BasicMachine CUTTER = new BasicMachine<>("cutter", new RecipeBuilder(), ITEM);
    public static BasicMachine FURNACE = new BasicMachine<>("furnace", new RecipeBuilder(), ITEM);
    public static BasicMachine EXTRACTOR = new BasicMachine<>("extractor", new RecipeBuilder(), ITEM);
    public static BasicMachine EXTRUDER = new BasicMachine<>("extruder", new RecipeBuilder(), ITEM);
    public static BasicMachine LATHE = new BasicMachine<>("lathe", new RecipeBuilder(), ITEM);
    public static BasicMachine PULVERIZER = new BasicMachine<>("pulverizer", new RecipeBuilder(), ITEM);
    public static BasicMachine RECYCLER = new BasicMachine<>("recycler", new RecipeBuilder(), ITEM);
    public static BasicMachine SCANNER = new BasicMachine<>("scanner", new RecipeBuilder(), ITEM, FLUID);
    public static BasicMachine WIRE_MILL = new BasicMachine<>("wire_mill", new RecipeBuilder(), ITEM);
    public static BasicMachine CENTRIFUGE = new BasicMachine<>("centrifuge", new RecipeBuilder(), ITEM, FLUID);
    public static BasicMachine ELECTROLYZER = new BasicMachine<>("electrolyzer", new RecipeBuilder(), ITEM, FLUID);
    public static BasicMachine THERMAL_CENTRIFUGE = new BasicMachine<>("thermal_centrifuge", new RecipeBuilder(), ITEM);
    public static BasicMachine ORE_WASHER = new BasicMachine<>("ore_washer", new RecipeBuilder(), ITEM, FLUID);
    public static BasicMachine CHEMICAL_REACTOR = new BasicMachine<>("chemical_reactor", new RecipeBuilder(), ITEM, FLUID);
    public static BasicMachine FLUID_CANNER = new BasicMachine<>("fluid_canner", new RecipeBuilder(), ITEM, FLUID);
    public static BasicMachine DISASSEMBLER = new BasicMachine<>("disassembler", new RecipeBuilder(), ITEM);
    public static BasicMachine MASS_FABRICATOR = new BasicMachine<>("mass_fabricator", new RecipeBuilder(), ITEM, FLUID);
    public static BasicMachine AMP_FABRICATOR = new BasicMachine<>("amp_fabricator", new RecipeBuilder(), ITEM);
    public static BasicMachine REPLICATOR = new BasicMachine<>("replicator", new RecipeBuilder(), ITEM, FLUID);
    public static BasicMachine FERMENTER = new BasicMachine<>("fermenter", new RecipeBuilder(), ITEM, FLUID);
    public static BasicMachine FLUID_EXTRACTOR = new BasicMachine<>("fluid_extractor", new RecipeBuilder(), ITEM);
    public static BasicMachine FLUID_SOLIDIFIER = new BasicMachine<>("fluid_solidifier", new RecipeBuilder(), ITEM, FLUID);
    public static BasicMachine DISTILLERY = new BasicMachine<>("distillery", new RecipeBuilder(), ITEM, FLUID);
    public static BasicMachine CHEMICAL_BATH = new BasicMachine<>("chemical_bath", new RecipeBuilder(), ITEM, FLUID);
    public static BasicMachine AUTOCLAVE = new BasicMachine<>("autoclave", new RecipeBuilder(), ITEM, FLUID);
    public static BasicMachine MIXER = new BasicMachine<>("mixer", new RecipeBuilder(), ITEM, FLUID);
    public static BasicMachine LASER_ENGRAVER = new BasicMachine<>("laser_engraver", new RecipeBuilder(), ITEM);
    public static BasicMachine FORMING_PRESS = new BasicMachine<>("forming_press", new RecipeBuilder(), ITEM);
    public static BasicMachine FORGE_HAMMER = new BasicMachine<>("forge_hammer", new RecipeBuilder(), ITEM);
    public static BasicMachine SIFTER = new BasicMachine<>("sifter", new RecipeBuilder(), ITEM);
    public static BasicMachine ARC_FURNACE = new BasicMachine<>("arc_furnace", new RecipeBuilder(), ITEM, FLUID);
    public static BasicMachine PLASMA_ARC_FURNACE = new BasicMachine<>("plasma_arc_furnace", new RecipeBuilder(), ITEM, FLUID);

    public static BasicMachine COAL_BOILER = new BasicMachine<>("coal_boiler", new RecipeBuilder(), BRONZE, STEEL, ITEM, FLUID);
    public static BasicMachine LAVA_BOILER = new BasicMachine<>("lava_boiler", new RecipeBuilder(), STEEL, ITEM, FLUID); //TODO
    public static BasicMachine SOLAR_BOILER = new BasicMachine<>("solar_boiler", new RecipeBuilder(), BRONZE, ITEM, FLUID); //TODO
    public static BasicMachine STEAM_FURNACE = new BasicMachine<>("steam_furnace", new RecipeBuilder(), BRONZE, STEEL, ITEM, FLUID);
    public static BasicMachine STEAM_PULVERIZER = new BasicMachine<>("steam_pulverizer", new RecipeBuilder(), BRONZE, STEEL, ITEM, FLUID);
    public static BasicMachine STEAM_EXTRACTOR = new BasicMachine<>("steam_extractor", new RecipeBuilder(), BRONZE, STEEL, ITEM, FLUID);
    public static BasicMachine STEAM_FORGE_HAMMER = new BasicMachine<>("steam_forge_hammer", new RecipeBuilder(), BRONZE, STEEL, ITEM, FLUID);
    public static BasicMachine STEAM_COMPRESSOR = new BasicMachine<>("steam_compressor", new RecipeBuilder(), BRONZE, STEEL, ITEM, FLUID);
    public static BasicMachine STEAM_ALLOY_SMELTER = new BasicMachine<>("steam_alloy_smelter", new RecipeBuilder(), BRONZE, STEEL, ITEM, FLUID);

    public static MultiMachine COKE_OVEN = new MultiMachine<>("coke_oven", new RecipeBuilder(), TileEntityCokeOven.class, LV, ITEM);
    public static MultiMachine<RecipeBuilders.BasicBlasting> PRIMITIVE_BLAST_FURNACE = new MultiMachine<>("primitive_blast_furnace", new RecipeBuilders.BasicBlasting(), TileEntityPrimitiveBlastFurnace.class, LV, ITEM);
    public static MultiMachine<RecipeBuilders.BasicBlasting> BRONZE_BLAST_FURNACE = new MultiMachine<>("bronze_blast_furnace", new RecipeBuilders.BasicBlasting(), TileEntityBronzeBlastFurnace.class, LV, ITEM);
    public static MultiMachine CHARCOAL_PIT = new MultiMachine<>("charcoal_pit", new RecipeBuilder(), TileEntityCharcoalPit.class, LV);
    public static MultiMachine BLAST_FURNACE = new MultiMachine<>("electric_blast_furnace", new RecipeBuilder(), TileEntityElectricBlastFurnace.class, LV, ITEM, FLUID);
    public static MultiMachine IMPLOSION_COMPRESSOR = new MultiMachine<>("implosion_compressor", new RecipeBuilder(), TileEntityImplosionCompressor.class, HV, ITEM);
    public static MultiMachine VACUUM_FREEZER = new MultiMachine<>("vacuum_freezer", new RecipeBuilder(), TileEntityVacuumFreezer.class, HV, ITEM, FLUID);
    public static MultiMachine MULTI_SMELTER = new MultiMachine<>("multi_smelter", new RecipeBuilder(), TileEntityMultiSmelter.class, HV, ITEM);
    public static MultiMachine LARGE_BOILER = new MultiMachine<>("large_boiler", new RecipeBuilder(), TileEntityLargeBoiler.class, LV, MV, HV, EV, ITEM, FLUID);
    public static MultiMachine LARGE_TURBINE = new MultiMachine<>("large_turbine", new RecipeBuilder(), TileEntityLargeTurbine.class, HV, EV, IV, UV, FLUID);
    public static MultiMachine HEAT_EXCHANGER = new MultiMachine<>("heat_exchanger", new RecipeBuilder(), TileEntityHeatExchanger.class, EV, FLUID);
    public static MultiMachine OIL_DRILLING_RIG = new MultiMachine<>("oil_drilling_rig", new RecipeBuilder(), TileEntityOilDrillingRig.class, EV, IV, LUV, ZPM, FLUID);
    public static MultiMachine OIL_CRACKING_UNIT = new MultiMachine<>("oil_cracking_unit", new RecipeBuilder(), TileEntityOilCrackingUnit.class, HV, FLUID);
    public static MultiMachine ADVANCED_MINER = new MultiMachine<>("advanced_miner", new RecipeBuilder(), TileEntityAdvancedMiner.class, LV, ITEM);
    public static MultiMachine PYROLYSIS_OVEN = new MultiMachine<>("pyrolysis_oven", new RecipeBuilder(), TileEntityPyrolysisOven.class, MV, ITEM, FLUID);
    public static MultiMachine COMBUSTION_ENGINE = new MultiMachine<>("combustion_engine", new RecipeBuilder(), TileEntityCombustionEngine.class, EV, FLUID);
    public static MultiMachine FUSION_REACTOR = new MultiMachine<>("fusion_reactor", new RecipeBuilder(), TileEntityFusionReactor.class, LUV, ZPM, UV, FLUID);

    public static HatchMachine HATCH_ITEM_I = new HatchMachine("hatch_item_input", GUI, ITEM);
    public static HatchMachine HATCH_ITEM_O = new HatchMachine("hatch_item_output", GUI, ITEM);
    public static HatchMachine HATCH_FLUID_I = new HatchMachine("hatch_fluid_input", GUI, FLUID);
    public static HatchMachine HATCH_FLUID_O = new HatchMachine("hatch_fluid_output", GUI, FLUID);
    public static HatchMachine HATCH_MUFFLER = new HatchMachine("hatch_muffler", GUI, ITEM);
    public static HatchMachine HATCH_DYNAMO = new HatchMachine("hatch_dynamo", ENERGY);
    public static HatchMachine HATCH_ENERGY = new HatchMachine("hatch_energy", ENERGY);

    public static TankMachine QUANTUM_TANK = new TankMachine("quantum_tank");

    public static BasicMachine STEAM_GENERATOR = new BasicMachine<>("steam_generator", new RecipeBuilder(), LV, MV, HV, ITEM, FLUID);
    public static BasicMachine GAS_GENERATOR = new BasicMachine<>("gas_generator", new RecipeBuilder(), LV, MV, HV, ITEM, FLUID);
    public static BasicMachine COMBUSTION_GENERATOR = new BasicMachine<>("combustion_generator", new RecipeBuilder(), LV, MV, HV, ITEM, FLUID);
    public static BasicMachine NAQUADAH_GENERATOR = new BasicMachine<>("naquadah_generator", new RecipeBuilder(), EV, IV, LUV, ITEM, FLUID);
    public static BasicMachine PLASMA_GENERATOR = new BasicMachine<>("plasma_generator", new RecipeBuilder(), IV, LUV, ZPM, ITEM, FLUID);

    public static void init() {
        STEAM_GENERATOR.setRecipeMap(GregTechAPI.STEAM_FUELS);
        GAS_GENERATOR.setRecipeMap(GregTechAPI.GAS_FUELS);
        COMBUSTION_GENERATOR.setRecipeMap(GregTechAPI.COMBUSTION_FUELS);
        NAQUADAH_GENERATOR.setRecipeMap(GregTechAPI.NAQUADAH_FUELS);
        PLASMA_GENERATOR.setRecipeMap(GregTechAPI.PLASMA_FUELS);
    }

    public static void add(Machine machine) {
        GregTechAPI.register(Machine.class, machine);
        GregTechAPI.register(machine.getBlock());
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

    public static MachineStack get(Machine type, Tier tier) {
        return new MachineStack(type, tier);
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
