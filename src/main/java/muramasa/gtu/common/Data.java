package muramasa.gtu.common;

import muramasa.gtu.Configs;
import muramasa.gtu.api.blocks.BlockCasing;
import muramasa.gtu.api.blocks.BlockCoil;
import muramasa.gtu.api.blocks.pipe.BlockCable;
import muramasa.gtu.api.blocks.pipe.BlockFluidPipe;
import muramasa.gtu.api.blocks.pipe.BlockItemPipe;
import muramasa.gtu.api.data.Materials;
import muramasa.gtu.api.items.ItemFluidCell;
import muramasa.gtu.api.items.StandardItem;
import muramasa.gtu.api.machines.Tier;
import muramasa.gtu.api.pipe.PipeSize;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Data {

    private static boolean HC = Configs.GAMEPLAY.HARDCORE_CABLES;

    public static void init() {

    }

    public static StandardItem DebugScanner = new StandardItem("debug_scanner", TextFormatting.AQUA + "" + TextFormatting.ITALIC + "Development Item");
    public static StandardItem StickyResin = new StandardItem("sticky_resin");
    public static StandardItem ItemPort = new StandardItem("item_port", "Can be placed on machines as a cover");
    public static StandardItem FluidPort = new StandardItem("fluid_port", "Can be placed on machines as a cover");
    public static StandardItem EnergyPort = new StandardItem("energy_port", "Can be placed on machines as a cover");
    public static StandardItem ComputerMonitor = new StandardItem("computer_monitor", "Can be placed on machines as a cover");

    public static ItemFluidCell CellTin = new ItemFluidCell(Materials.Tin, 1000);
    public static ItemFluidCell CellSteel = new ItemFluidCell(Materials.Steel, 16000);
    public static ItemFluidCell CellTungstensteel = new ItemFluidCell(Materials.TungstenSteel, 64000);

    public static StandardItem ItemFilter = new StandardItem("item_filter");
    public static StandardItem DiamondSawBlade = new StandardItem("diamond_saw_blade");
    public static StandardItem DiamondGrindHead = new StandardItem("diamond_grind_head");
    public static StandardItem TungstenGrindHead = new StandardItem("tungsten_grind_head");
    public static StandardItem IridiumAlloyIngot = new StandardItem("iridium_alloy_ingot", "Used to make Iridium Plates");
    public static StandardItem IridiumReinforcedPlate = new StandardItem("iridium_reinforced_plate", "GT2s Most Expensive Component");
    public static StandardItem IridiumNeutronReflector = new StandardItem("iridium_neutron_reflector", "Indestructible");
    public static StandardItem QuantumEye = new StandardItem("quantum_eye", "Improved Ender Eye");
    public static StandardItem QuantumStat = new StandardItem("quantum_star", "Improved Nether Star");
    public static StandardItem GraviStar = new StandardItem("gravi_star", "Ultimate Nether Star");

    public static StandardItem MotorLV = new StandardItem("motor_lv");
    public static StandardItem MotorMV = new StandardItem("motor_mv");
    public static StandardItem MotorHV = new StandardItem("motor_hv");
    public static StandardItem MotorEV = new StandardItem("motor_ev");
    public static StandardItem MotorIV = new StandardItem("motor_iv");
    public static StandardItem PumpLV = new StandardItem("pump_lv", "640 L/s (as Cover)");
    public static StandardItem PumpMV = new StandardItem("pump_mv", "2,560 L/s (as Cover)");
    public static StandardItem PumpHV = new StandardItem("pump_hv", "10,240 L/s (as Cover)");
    public static StandardItem PumpEV = new StandardItem("pump_ev", "40,960 L/s (as Cover)");
    public static StandardItem PumpIV = new StandardItem("pump_iv", "163,840 L/s (as Cover)");
    public static StandardItem FluidRegulatorLV = new StandardItem("fluid_regulator_lv", "Configurable up to 640 L/s (as Cover)");
    public static StandardItem FluidRegulatorMV = new StandardItem("fluid_regulator_mv", "Configurable up to 2,560 L/s (as Cover)");
    public static StandardItem FluidRegulatorHV = new StandardItem("fluid_regulator_hv", "Configurable up to 10,240 L/s (as Cover)");
    public static StandardItem FluidRegulatorEV = new StandardItem("fluid_regulator_ev", "Configurable up to 40,960 L/s (as Cover)");
    public static StandardItem FluidRegulatorIV = new StandardItem("fluid_regulator_iv", "Configurable up to 163,840 L/s (as Cover)");
    public static StandardItem ConveyorLV = new StandardItem("conveyor_lv", "1 Stack every 20s (as Cover)");
    public static StandardItem ConveyorMV = new StandardItem("conveyor_mv", "1 Stack every 5s (as Cover)");
    public static StandardItem ConveyorHV = new StandardItem("conveyor_hv", "1 Stack every 1s (as Cover)");
    public static StandardItem ConveyorEV = new StandardItem("conveyor_ev", "1 Stack every 0.5s (as Cover)");
    public static StandardItem ConveyorIV = new StandardItem("conveyor_iv", "1 Stack every 0.05s (as Cover)");
    public static StandardItem PistonLV = new StandardItem("piston_lv");
    public static StandardItem PistonMV = new StandardItem("piston_mv");
    public static StandardItem PistonHV = new StandardItem("piston_hv");
    public static StandardItem PistonEV = new StandardItem("piston_ev");
    public static StandardItem PistonIV = new StandardItem("piston_iv");
    public static StandardItem RobotArmLV = new StandardItem("robot_arm_lv", "Insets into specific Slots (as Cover)");
    public static StandardItem RobotArmMV = new StandardItem("robot_arm_mv", "Insets into specific Slots (as Cover)");
    public static StandardItem RobotArmHV = new StandardItem("robot_arm_hv", "Insets into specific Slots (as Cover)");
    public static StandardItem RobotArmEV = new StandardItem("robot_arm_ev", "Insets into specific Slots (as Cover)");
    public static StandardItem RobotArmIV = new StandardItem("robot_arm_iv", "Insets into specific Slots (as Cover)");
    public static StandardItem FieldGenLV = new StandardItem("field_gen_lv");
    public static StandardItem FieldGenMV = new StandardItem("field_gen_mv");
    public static StandardItem FieldGenHV = new StandardItem("field_gen_hv");
    public static StandardItem FieldGenEV = new StandardItem("field_gen_ev");
    public static StandardItem FieldGenIV = new StandardItem("field_gen_iv");
    public static StandardItem EmitterLV = new StandardItem("emitter_lv");
    public static StandardItem EmitterMV = new StandardItem("emitter_mv");
    public static StandardItem EmitterHV = new StandardItem("emitter_hv");
    public static StandardItem EmitterEV = new StandardItem("emitter_ev");
    public static StandardItem EmitterIV = new StandardItem("emitter_iv");
    public static StandardItem SensorLV = new StandardItem("sensor_lv");
    public static StandardItem SensorMV = new StandardItem("sensor_mv");
    public static StandardItem SensorHV = new StandardItem("sensor_hv");
    public static StandardItem SensorEV = new StandardItem("sensor_ev");
    public static StandardItem SensorIV = new StandardItem("sensor_iv");

    public static StandardItem NandChip = new StandardItem("nand_chip", "A very simple circuit");
    public static StandardItem AdvCircuitParts = new StandardItem("adv_circuit_parts", "Used for making Advanced Circuits");
    public static StandardItem EtchedWiringMV = new StandardItem("etched_wiring_mv", "Circuit board parts");
    public static StandardItem EtchedWiringHV = new StandardItem("etched_wiring_hv", "Circuit board parts");
    public static StandardItem EtchedWiringEV = new StandardItem("etched_wiring_ev", "Circuit board parts");
    public static StandardItem EngravedCrystalChip = new StandardItem("engraved_crystal_chip", "Needed for Circuits");
    public static StandardItem EngravedLapotronChip = new StandardItem("engraved_lapotron_chip", "Needed for Circuits");
    public static StandardItem CircuitBoardEmpty = new StandardItem("circuit_board_empty", "A board Part");
    public static StandardItem CircuitBoardBasic = new StandardItem("circuit_board_basic", "A basic Board");
    public static StandardItem CircuitBoardAdv = new StandardItem("circuit_board_adv", "An advanced Board");
    public static StandardItem CircuitBoardProcessorEmpty = new StandardItem("circuit_board_processor_empty", "A Processor Board Part");
    public static StandardItem CircuitBoardProcessor = new StandardItem("circuit_board_processor", "A Processor Board");
    public static StandardItem CircuitBasic = new StandardItem("circuit_basic", "A basic Circuit");
    public static StandardItem CircuitGood = new StandardItem("circuit_good", "A good Circuit");
    public static StandardItem CircuitAdv = new StandardItem("circuit_adv", "An advanced Circuit");
    public static StandardItem CircuitDataStorage = new StandardItem("circuit_data_storage", "A Data Storage Chip");
    public static StandardItem CircuitDataControl = new StandardItem("circuit_data_control", "A Data Control Processor");
    public static StandardItem CircuitEnergyFlow = new StandardItem("circuit_energy_flow", "A High Voltage Processor");
    public static StandardItem CircuitDataOrb = new StandardItem("circuit_data_orb", "A High Capacity Data Storage");
    public static StandardItem DataStick = new StandardItem("data_stick", "A Low Capacity Data Storage");

    public static StandardItem BatteryTantalum = new StandardItem("battery_tantalum", "Reusable");
    public static StandardItem BatteryHullSmall = new StandardItem("battery_hull_small", "An empty LV Battery Hull");
    public static StandardItem BatteryHullMedium = new StandardItem("battery_hull_medium", "An empty MV Battery Hull");
    public static StandardItem BatteryHullLarge = new StandardItem("battery_hull_large", "An empty HV Battery Hull");
    public static StandardItem BatterySmallAcid = new StandardItem("battery_small_acid", "Single Use");
    public static StandardItem BatterySmallMercury = new StandardItem("battery_small_mercury", "Single Use");
    public static StandardItem BatterySmallCadmium = new StandardItem("battery_small_cadmium", "Reusable");
    public static StandardItem BatterySmallLithium = new StandardItem("battery_small_lithium", "Reusable");
    public static StandardItem BatterySmallSodium = new StandardItem("battery_small_sodium", "Reusable");
    public static StandardItem BatteryMediumAcid = new StandardItem("battery_medium_acid", "Single Use");
    public static StandardItem BatteryMediumMercury = new StandardItem("battery_medium_mercury", "Single Use");
    public static StandardItem BatteryMediumCadmium = new StandardItem("battery_medium_cadmium", "Reusable");
    public static StandardItem BatteryMediumLithium = new StandardItem("battery_medium_lithium", "Reusable");
    public static StandardItem BatteryMediumSodium = new StandardItem("battery_medium_sodium", "Reusable");
    public static StandardItem BatteryLargeAcid = new StandardItem("battery_large_acid", "Single Use");
    public static StandardItem BatteryLargeMercury = new StandardItem("battery_large_mercury", "Single Use");
    public static StandardItem BatteryLargeCadmium = new StandardItem("battery_large_cadmium", "Reusable");
    public static StandardItem BatteryLargeLithium = new StandardItem("battery_large_lithium", "Reusable");
    public static StandardItem BatteryLargeSodium = new StandardItem("battery_large_sodium", "Reusable");
    public static StandardItem BatteryEnergyOrb = new StandardItem("battery_energy_orb");
    public static StandardItem BatteryEnergyOrbCluster = new StandardItem("battery_energy_orb_cluster");

    public static StandardItem EmptyShape = new StandardItem("empty_shape_plate", "Raw plate to make Molds and Extruder Shapes");
    public static StandardItem MoldPlate = new StandardItem("mold_plate", "Mold for making Plates");
    public static StandardItem MoldGear = new StandardItem("mold_gear", "Mold for making Gears");
    public static StandardItem MoldGearSmall = new StandardItem("mold_small_gear", "Mold for making Small Gears");
    public static StandardItem MoldCoinage = new StandardItem("mold_coinage", "Secure Mold for making Coins (Don't lose it!)");
    public static StandardItem MoldBottle = new StandardItem("mold_bottle", "Mold for making Bottles");
    public static StandardItem MoldIngot = new StandardItem("mold_ingot", "Mold for making Ingots");
    public static StandardItem MoldBall = new StandardItem("mold_ball", "Mold for making Balls");
    public static StandardItem MoldBlock = new StandardItem("mold_block", "Mold for making Blocks");
    public static StandardItem MoldNugget = new StandardItem("mold_nugget", "Mold for making Nuggets");
    public static StandardItem MoldAnvil = new StandardItem("mold_anvil", "Mold for making Anvils");
    public static StandardItem ShapePlate = new StandardItem("shape_plate", "Shape for making Plates");
    public static StandardItem ShapeRod = new StandardItem("shape_rod", "Shape for making Rods");
    public static StandardItem ShapeBolt = new StandardItem("shape_bolt", "Shape for making Bolts");
    public static StandardItem ShapeRing = new StandardItem("shape_ring", "Shape for making Rings");
    public static StandardItem ShapeCell = new StandardItem("shape_cell", "Shape for making Cells");
    public static StandardItem ShapeIngot = new StandardItem("shape_ingot", "Shape for making Ingots");
    public static StandardItem ShapeWire = new StandardItem("shape_wire", "Shape for making Wires");
    public static StandardItem ShapePipeTiny = new StandardItem("shape_pipe_tiny", "Shape for making Tiny Pipes");
    public static StandardItem ShapePipeSmall = new StandardItem("shape_pipe_small", "Shape for making Small Pipes");
    public static StandardItem ShapePipeNormal = new StandardItem("shape_pipe_normal", "Shape for making Normal Pipes");
    public static StandardItem ShapePipeLarge = new StandardItem("shape_pipe_large", "Shape for making Large Pipes");
    public static StandardItem ShapePipeHuge = new StandardItem("shape_pipe_huge", "Shape for making Huge Pipes");
    public static StandardItem ShapeBlock = new StandardItem("shape_block", "Shape for making Blocks");
    public static StandardItem ShapeHeadSword = new StandardItem("shape_head_sword", "Shape for making Sword Blades");
    public static StandardItem ShapeHeadPickaxe = new StandardItem("shape_head_pickaxe", "Shape for making Pickaxe Heads");
    public static StandardItem ShapeHeadShovel = new StandardItem("shape_head_shovel", "Shape for making Shovel Heads");
    public static StandardItem ShapeHeadAxe = new StandardItem("shape_head_axe", "Shape for making Axe Heads");
    public static StandardItem ShapeHeadHoe = new StandardItem("shape_head_hoe", "Shape for making Hoe Heads");
    public static StandardItem ShapeHeadHammer = new StandardItem("shape_head_hammer", "Shape for making Hammer Heads");
    public static StandardItem ShapeHeadFile = new StandardItem("shape_head_file", "Shape for making File Heads");
    public static StandardItem ShapeHeadSaw = new StandardItem("shape_head_saw", "Shape for making Saw Heads");
    public static StandardItem ShapeGear = new StandardItem("shape_gear", "Shape for making Gears");
    public static StandardItem ShapeGearSmall = new StandardItem("shape_gear_small", "Shape for making Small Gears");
    public static StandardItem ShapeBottle = new StandardItem("shape_bottle", "Shape for making Bottles"); //TODO needed?

    //TODO optional items (register anyway, but don't show in JEI?)
    //TODO move to IC2+IC2C Registrar
    public static StandardItem DropTin = new StandardItem("drop_tin", "Source of Tin")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static StandardItem DropLead = new StandardItem("drop_lead", "Source of Lead")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static StandardItem DropSilver = new StandardItem("drop_silver", "Source of Silver")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static StandardItem DropIron = new StandardItem("drop_iron", "Source of Iron")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static StandardItem DropGold = new StandardItem("drop_gold", "Source of Gold")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static StandardItem DropAluminium = new StandardItem("drop_aluminium", "Source of Aluminium")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static StandardItem DropTitanium = new StandardItem("drop_titanium", "Source of Titanium")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static StandardItem DropUranium = new StandardItem("drop_uranium", "Source of Uranium")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static StandardItem DropUranite = new StandardItem("drop_uranite", "Source of Uranite")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static StandardItem DropThorium = new StandardItem("drop_thorium", "Source of Thorium")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static StandardItem DropNickel = new StandardItem("drop_nickel", "Source of Nickel")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static StandardItem DropZinc = new StandardItem("drop_zinc", "Source of Zinc")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static StandardItem DropManganese = new StandardItem("drop_manganese", "Source of Manganese")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static StandardItem DropCopper = new StandardItem("drop_copper", "Source of Copper")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static StandardItem DropTungsten = new StandardItem("drop_tungsten", "Source of Tungsten")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static StandardItem DropPlatinum = new StandardItem("drop_platinum", "Source of Platinum")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static StandardItem DropIridium = new StandardItem("drop_iridium", "Source of Iridium")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static StandardItem DropOsmium = new StandardItem("drop_osmium", "Source of Osmium")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static StandardItem DropNaquadah = new StandardItem("drop_naquadah", "Source of Naquadah")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static StandardItem DropEmerald = new StandardItem("drop_emerald", "Source of Emeralds")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static StandardItem DropOil = new StandardItem("drop_oil", "Source of Oil")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static StandardItem DropUUM = new StandardItem("drop_uum", "Source of UU Matter")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static StandardItem DropUUA = new StandardItem("drop_uua", "Source of UU Amplifier")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;

    //TODO move to Forestry Registrar
    public static StandardItem CombLignite = new StandardItem("comb_lignite", "")/*.optional(Ref.MOD_FR)*/;
    public static StandardItem CombCoal = new StandardItem("comb_coal", "")/*.optional(Ref.MOD_FR)*/;
    public static StandardItem CombResin = new StandardItem("comb_resin", "")/*.optional(Ref.MOD_FR)*/;
    public static StandardItem CombOil = new StandardItem("comb_oil", "")/*.optional(Ref.MOD_FR)*/;
    public static StandardItem CombStone = new StandardItem("comb_stone", "")/*.optional(Ref.MOD_FR)*/;
    public static StandardItem CombCertus = new StandardItem("comb_certus", "")/*.required(Ref.MOD_FR, Ref.MOD_AE)*/;
    public static StandardItem CombRedstone = new StandardItem("comb_redstone", "")/*.optional(Ref.MOD_FR)*/;
    public static StandardItem CombLapis = new StandardItem("comb_lapis", "")/*.optional(Ref.MOD_FR)*/;
    public static StandardItem CombRuby = new StandardItem("comb_ruby", "")/*.optional(Ref.MOD_FR)*/;
    public static StandardItem CombSapphire = new StandardItem("comb_sapphire", "")/*.optional(Ref.MOD_FR)*/;
    public static StandardItem CombDiamond = new StandardItem("comb_diamond", "")/*.optional(Ref.MOD_FR)*/;
    public static StandardItem CombOlivine = new StandardItem("comb_olivine", "")/*.optional(Ref.MOD_FR)*/;
    public static StandardItem CombEmerald = new StandardItem("comb_emerald", "")/*.optional(Ref.MOD_FR)*/;
    public static StandardItem CombSlag = new StandardItem("comb_slag", "")/*.optional(Ref.MOD_FR)*/;
    public static StandardItem CombCopper = new StandardItem("comb_copper", "")/*.optional(Ref.MOD_FR)*/;
    public static StandardItem CombTin = new StandardItem("comb_tin", "")/*.optional(Ref.MOD_FR)*/;
    public static StandardItem CombLead = new StandardItem("comb_lead", "")/*.optional(Ref.MOD_FR)*/;
    public static StandardItem CombIron = new StandardItem("comb_iron", "")/*.optional(Ref.MOD_FR)*/;
    public static StandardItem CombSteel = new StandardItem("comb_steel", "")/*.optional(Ref.MOD_FR)*/;
    public static StandardItem CombNickel = new StandardItem("comb_nickel", "")/*.optional(Ref.MOD_FR)*/;
    public static StandardItem CombZinc = new StandardItem("comb_zinc", "")/*.optional(Ref.MOD_FR)*/;
    public static StandardItem CombSilver = new StandardItem("comb_silver", "")/*.optional(Ref.MOD_FR)*/;
    public static StandardItem CombGold = new StandardItem("comb_gold", "")/*.optional(Ref.MOD_FR)*/;
    public static StandardItem CombAluminium = new StandardItem("comb_aluminium", "")/*.optional(Ref.MOD_FR)*/;
    public static StandardItem CombManganese = new StandardItem("comb_manganese", "")/*.optional(Ref.MOD_FR)*/;
    public static StandardItem CombTitanium = new StandardItem("comb_titanium", "")/*.optional(Ref.MOD_FR)*/;
    public static StandardItem CombChrome = new StandardItem("comb_chrome", "")/*.optional(Ref.MOD_FR)*/;
    public static StandardItem CombTungsten = new StandardItem("comb_tungsten", "")/*.optional(Ref.MOD_FR)*/;
    public static StandardItem CombPlatinum = new StandardItem("comb_platinum", "")/*.optional(Ref.MOD_FR)*/;
    public static StandardItem CombIridium = new StandardItem("comb_iridium", "")/*.optional(Ref.MOD_FR)*/;
    public static StandardItem CombUranium = new StandardItem("comb_uranium", "")/*.optional(Ref.MOD_FR)*/;
    public static StandardItem CombPlutonium = new StandardItem("comb_plutonium", "")/*.optional(Ref.MOD_FR)*/;
    public static StandardItem CombNaquadah = new StandardItem("comb_naquadah", "")/*.optional(Ref.MOD_FR)*/;

    public static BlockCoil COIL_CUPRONICKEL = new BlockCoil("cupronickel", 113); //1808
    public static BlockCoil COIL_KANTHAL = new BlockCoil("kanthal", 169); //2704
    public static BlockCoil COIL_NICHROME = new BlockCoil("nichrome", 225); //3600
    public static BlockCoil COIL_TUNGSTENSTEEL = new BlockCoil("tungstensteel", 282); //4512
    public static BlockCoil COIL_HSSG = new BlockCoil("hssg", 338); //5408
    public static BlockCoil COIL_NAQUADAH = new BlockCoil("naquadah", 450); //7200
    public static BlockCoil COIL_NAQUADAH_ALLOY = new BlockCoil("naquadah_alloy", 563); //9008
    public static BlockCoil COIL_FUSION = new BlockCoil("fusion", 563); //9008
    public static BlockCoil COIL_SUPERCONDUCTOR = new BlockCoil("superconductor", 563); //9008

    public static BlockCasing CASING_ULV = new BlockCasing("ulv");
    public static BlockCasing CASING_LV = new BlockCasing("lv");
    public static BlockCasing CASING_MV = new BlockCasing("mv");
    public static BlockCasing CASING_HV = new BlockCasing("hv");
    public static BlockCasing CASING_EV = new BlockCasing("ev");
    public static BlockCasing CASING_IV = new BlockCasing("iv");
    public static BlockCasing CASING_LUV = new BlockCasing("luv");
    public static BlockCasing CASING_ZPM = new BlockCasing("zpm");
    public static BlockCasing CASING_UV = new BlockCasing("uv");
    public static BlockCasing CASING_MAX = new BlockCasing("max");
    public static BlockCasing CASING_BRONZE = new BlockCasing("bronze");
    public static BlockCasing CASING_BRICKED_BRONZE = new BlockCasing("bricked_bronze");
    public static BlockCasing CASING_BRONZE_PLATED_BRICK = new BlockCasing("bronze_plated_brick");
    public static BlockCasing CASING_FIRE_BRICK = new BlockCasing("fire_brick");
    public static BlockCasing CASING_STEEL = new BlockCasing("steel");
    public static BlockCasing CASING_BRICKED_STEEL = new BlockCasing("bricked_steel");
    public static BlockCasing CASING_SOLID_STEEL = new BlockCasing("solid_steel");
    public static BlockCasing CASING_STAINLESS_STEEL = new BlockCasing("stainless_steel");
    public static BlockCasing CASING_TITANIUM = new BlockCasing("titanium");
    public static BlockCasing CASING_TUNGSTENSTEEL = new BlockCasing("tungstensteel");
    public static BlockCasing CASING_HEAT_PROOF = new BlockCasing("heat_proof");
    public static BlockCasing CASING_FROST_PROOF = new BlockCasing("frost_proof");
    public static BlockCasing CASING_RADIATION_PROOF = new BlockCasing("radiation_proof");
    public static BlockCasing CASING_FIREBOX_BRONZE = new BlockCasing("firebox_bronze");
    public static BlockCasing CASING_FIREBOX_STEEL = new BlockCasing("firebox_steel");
    public static BlockCasing CASING_FIREBOX_TITANIUM = new BlockCasing("firebox_titanium");
    public static BlockCasing CASING_FIREBOX_TUNGSTENSTEEL = new BlockCasing("firebox_tungstensteel");
    public static BlockCasing CASING_GEARBOX_BRONZE = new BlockCasing("gearbox_bronze");
    public static BlockCasing CASING_GEARBOX_STEEL = new BlockCasing("gearbox_steel");
    public static BlockCasing CASING_GEARBOX_TITANIUM = new BlockCasing("gearbox_titanium");
    public static BlockCasing CASING_GEARBOX_TUNGSTENSTEEL = new BlockCasing("gearbox_tungstensteel");
    public static BlockCasing CASING_PIPE_BRONZE = new BlockCasing("pipe_bronze");
    public static BlockCasing CASING_PIPE_STEEL = new BlockCasing("pipe_steel");
    public static BlockCasing CASING_PIPE_TITANIUM = new BlockCasing("pipe_titanium");
    public static BlockCasing CASING_PIPE_TUNGSTENSTEEL = new BlockCasing("pipe_tungstensteel");
    public static BlockCasing CASING_ENGINE_INTAKE = new BlockCasing("engine_intake");
    public static BlockCasing CASING_TURBINE_1 = new BlockCasing("turbine_1");
    public static BlockCasing CASING_TURBINE_2 = new BlockCasing("turbine_2");
    public static BlockCasing CASING_TURBINE_3 = new BlockCasing("turbine_3");
    public static BlockCasing CASING_TURBINE_4 = new BlockCasing("turbine_4");
    public static BlockCasing CASING_FUSION_1 = new BlockCasing("fusion_1");
    public static BlockCasing CASING_FUSION_2 = new BlockCasing("fusion_2");
    public static BlockCasing CASING_FUSION_3 = new BlockCasing("fusion_3");

    public static BlockCable CABLE_RED_ALLOY = new BlockCable(Materials.RedAlloy, 0, 1, 1, Tier.ULV); //ULV
    public static BlockCable CABLE_COBALT = new BlockCable(Materials.Cobalt, 2, 4, 2, Tier.LV); //LV
    public static BlockCable CABLE_LEAD = new BlockCable(Materials.Lead, 2, 4, 2, Tier.LV);
    public static BlockCable CABLE_TIN = new BlockCable(Materials.Tin, 1, 2, 1, Tier.LV);
    public static BlockCable CABLE_ZINC = new BlockCable(Materials.Zinc, 1, 2, 1, Tier.LV);
    public static BlockCable CABLE_SOLDERING_ALLOY = new BlockCable(Materials.SolderingAlloy, 1, 2, 1, Tier.LV);
    public static BlockCable CABLE_IRON = new BlockCable(Materials.Iron, HC ? 3 : 4, HC ? 6 : 8, 2, Tier.MV); //MV
    public static BlockCable CABLE_NICKEL = new BlockCable(Materials.Nickel, HC ? 3 : 5, HC ? 6 : 10, 3, Tier.MV);
    public static BlockCable CABLE_CUPRONICKEL = new BlockCable(Materials.Cupronickel, HC ? 3 : 4, HC ? 6 : 8, 2, Tier.MV);
    public static BlockCable CABLE_COPPER = new BlockCable(Materials.Copper, HC ? 2 : 3, HC ? 4 : 6, 1, Tier.MV);
    public static BlockCable CABLE_ANNEALED_COPPER = new BlockCable(Materials.AnnealedCopper, HC ? 1 : 2, HC ? 2 : 4, 1, Tier.MV);
    public static BlockCable CABLE_KANTHAL = new BlockCable(Materials.Kanthal, HC ? 3 : 8, HC ? 6 : 16, 4, Tier.HV); //HV
    public static BlockCable CABLE_GOLD = new BlockCable(Materials.Gold, HC ? 2 : 6, HC ? 4 : 12, 3, Tier.HV);
    public static BlockCable CABLE_ELECTRUM = new BlockCable(Materials.Electrum, HC ? 2 : 5, HC ? 4 : 10, 2, Tier.HV);
    public static BlockCable CABLE_SILVER = new BlockCable(Materials.Silver, HC ? 1 : 4, HC ? 2 : 8, 1, Tier.HV);
    public static BlockCable CABLE_NICHROME = new BlockCable(Materials.Nichrome, HC ? 4 : 32, HC ? 8 : 64, 3, Tier.EV); //EV
    public static BlockCable CABLE_STEEL = new BlockCable(Materials.Steel, HC ? 2 : 16, HC ? 4 : 32, 2, Tier.EV);
    public static BlockCable CABLE_TITANIUM = new BlockCable(Materials.Titanium, HC ? 2 : 12, HC ? 4 : 24, 4, Tier.EV);
    public static BlockCable CABLE_ALUMINIUM = new BlockCable(Materials.Aluminium, HC ? 1 : 8, HC ? 2 : 16, 1, Tier.EV);
    public static BlockCable CABLE_GRAPHENE = new BlockCable(Materials.Graphene, HC ? 1 : 16, HC ? 2 : 32, 1, Tier.IV); //IV
    public static BlockCable CABLE_OSMIUM = new BlockCable(Materials.Osmium, HC ? 2 : 32, HC ? 4 : 64, 4, Tier.IV);
    public static BlockCable CABLE_PLATINUM = new BlockCable(Materials.Platinum, HC ? 1 : 16, HC ? 2 : 32, 2, Tier.IV);
    public static BlockCable CABLE_TUNGSTENSTEEL = new BlockCable(Materials.TungstenSteel, HC ? 1 : 14, HC ? 4 : 28, 3, Tier.IV);
    public static BlockCable CABLE_TUNGSTEN = new BlockCable(Materials.Tungsten, HC ? 2 : 12, HC ? 4 : 24, 1, Tier.IV);
    public static BlockCable CABLE_HSSG = new BlockCable(Materials.HSSG, HC ? 2 : 128, HC ? 4 : 256, 4, Tier.LUV); //LUV
    public static BlockCable CABLE_NIOBIUM_TITANIUM = new BlockCable(Materials.NiobiumTitanium, HC ? 2 : 128, HC ? 4 : 256, 4, Tier.LUV);
    public static BlockCable CABLE_VANADIUM_GALLIUM = new BlockCable(Materials.VanadiumGallium, HC ? 2 : 128, HC ? 4 : 256, 4, Tier.LUV);
    public static BlockCable CABLE_YTTRIUM_BARIUM_CUPRATE = new BlockCable(Materials.YttriumBariumCuprate, HC ? 4 : 256, HC ? 8 : 512, 4, Tier.LUV);
    public static BlockCable CABLE_NAQUADAH = new BlockCable(Materials.Naquadah, HC ? 2 : 64, HC ? 4 : 128, 2, Tier.ZPM); //ZPM
    public static BlockCable CABLE_NAQUADAH_ALLOY = new BlockCable(Materials.NaquadahAlloy, HC ? 4 : 64, HC ? 8 : 128, 2, Tier.ZPM);
    public static BlockCable CABLE_DURANIUM = new BlockCable(Materials.Duranium, HC ? 8 : 64, HC ? 16 : 128, 1, Tier.ZPM);
    public static BlockCable CABLE_SUPERCONDUCTOR = new BlockCable(Materials.Superconductor, 1, 1, 4, Tier.MAX); //MAX

    public static BlockFluidPipe FLUID_PIPE_WOOD = new BlockFluidPipe(Materials.Wood, 30, 350, false).setSizes(PipeSize.SMALL, PipeSize.NORMAL, PipeSize.LARGE).setCapacities(10, 10, 30, 60, 60, 60);
    public static BlockFluidPipe FLUID_PIPE_COPPER = new BlockFluidPipe(Materials.Copper, 10, 1000, true);
    public static BlockFluidPipe FLUID_PIPE_BRONZE = new BlockFluidPipe(Materials.Bronze, 20, 2000, true);
    public static BlockFluidPipe FLUID_PIPE_STEEL = new BlockFluidPipe(Materials.Steel, 40, 2500, true);
    public static BlockFluidPipe FLUID_PIPE_STAINLESS_STEEL = new BlockFluidPipe(Materials.StainlessSteel, 60, 3000, true);
    public static BlockFluidPipe FLUID_PIPE_TITANIUM = new BlockFluidPipe(Materials.Titanium, 80, 5000, true);
    public static BlockFluidPipe FLUID_PIPE_TUNGSTENSTEEL = new BlockFluidPipe(Materials.TungstenSteel, 100, 7500, true);
    public static BlockFluidPipe FLUID_PIPE_PLASTIC = new BlockFluidPipe(Materials.Plastic, 60, 250, true);
    public static BlockFluidPipe FLUID_PIPE_POLYTETRAFLUOROETHYLENE = new BlockFluidPipe(Materials.Polytetrafluoroethylene, 480, 600, true);
    public static BlockFluidPipe FLUID_PIPE_HIGH_PRESSURE = new BlockFluidPipe(Materials.HighPressure, 7200, 1500, true).setSizes(PipeSize.SMALL, PipeSize.NORMAL, PipeSize.LARGE).setCapacities(4800, 4800, 4800, 7200, 9600, 9600);
    public static BlockFluidPipe FLUID_PIPE_PLASMA = new BlockFluidPipe(Materials.PlasmaContainment, 240, 100000, true).setSizes(PipeSize.NORMAL).setCapacities(240, 240, 240, 240, 240, 240);

    public static BlockItemPipe ITEM_PIPE_CUPRONICKEL = new BlockItemPipe(Materials.Cupronickel, 1);
    public static BlockItemPipe ITEM_PIPE_COBALT_BRASS = new BlockItemPipe(Materials.CobaltBrass, 1);
    public static BlockItemPipe ITEM_PIPE_BRASS = new BlockItemPipe(Materials.Brass, 1);
    public static BlockItemPipe ITEM_PIPE_ELECTRUM = new BlockItemPipe(Materials.Electrum, 2);
    public static BlockItemPipe ITEM_PIPE_ROSE_GOLD = new BlockItemPipe(Materials.RoseGold, 2);
    public static BlockItemPipe ITEM_PIPE_STERLING_SILVER = new BlockItemPipe(Materials.SterlingSilver, 2);
    public static BlockItemPipe ITEM_PIPE_PLATINUM = new BlockItemPipe(Materials.Platinum, 4);
    public static BlockItemPipe ITEM_PIPE_ULTIMET = new BlockItemPipe(Materials.Ultimet, 4);
    public static BlockItemPipe ITEM_PIPE_POLYVINYL_CHLORIDE = new BlockItemPipe(Materials.PolyvinylChloride, 4);
    public static BlockItemPipe ITEM_PIPE_OSMIUM = new BlockItemPipe(Materials.Osmium, 8);
}
