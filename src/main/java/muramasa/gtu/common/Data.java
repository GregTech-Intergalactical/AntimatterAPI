package muramasa.gtu.common;

import muramasa.gtu.Configs;
import muramasa.gtu.api.blocks.*;
import muramasa.gtu.api.data.Textures;
import muramasa.gtu.api.items.BasicItem;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Data {

    private static boolean HC = Configs.GAMEPLAY.HARDCORE_CABLES;

    public static void init() {

    }

    public static BasicItem DebugScanner = new BasicItem("debug_scanner", TextFormatting.AQUA + "" + TextFormatting.ITALIC + "Development Item");
    public static BasicItem StickyResin = new BasicItem("sticky_resin");
    public static BasicItem ComputerMonitor = new BasicItem("computer_monitor", "Can be placed on machines as a cover");

    //public static ItemFluidCell CellTin = new ItemFluidCell(Tin, 1000);
    //public static ItemFluidCell CellSteel = new ItemFluidCell(Steel, 16000);
    //public static ItemFluidCell CellTungstensteel = new ItemFluidCell(TungstenSteel, 64000);

    public static BasicItem ItemFilter = new BasicItem("item_filter");
    public static BasicItem DiamondSawBlade = new BasicItem("diamond_saw_blade");
    public static BasicItem DiamondGrindHead = new BasicItem("diamond_grind_head");
    public static BasicItem TungstenGrindHead = new BasicItem("tungsten_grind_head");
    public static BasicItem IridiumAlloyIngot = new BasicItem("iridium_alloy_ingot", "Used to make Iridium Plates");
    public static BasicItem IridiumReinforcedPlate = new BasicItem("iridium_reinforced_plate", "GT2s Most Expensive Component");
    public static BasicItem IridiumNeutronReflector = new BasicItem("iridium_neutron_reflector", "Indestructible");
    public static BasicItem QuantumEye = new BasicItem("quantum_eye", "Improved Ender Eye");
    public static BasicItem QuantumStat = new BasicItem("quantum_star", "Improved Nether Star");
    public static BasicItem GraviStar = new BasicItem("gravi_star", "Ultimate Nether Star");

    public static BasicItem MotorLV = new BasicItem("motor_lv");
    public static BasicItem MotorMV = new BasicItem("motor_mv");
    public static BasicItem MotorHV = new BasicItem("motor_hv");
    public static BasicItem MotorEV = new BasicItem("motor_ev");
    public static BasicItem MotorIV = new BasicItem("motor_iv");
    public static BasicItem PumpLV = new BasicItem("pump_lv", "640 L/s (as Cover)");
    public static BasicItem PumpMV = new BasicItem("pump_mv", "2,560 L/s (as Cover)");
    public static BasicItem PumpHV = new BasicItem("pump_hv", "10,240 L/s (as Cover)");
    public static BasicItem PumpEV = new BasicItem("pump_ev", "40,960 L/s (as Cover)");
    public static BasicItem PumpIV = new BasicItem("pump_iv", "163,840 L/s (as Cover)");
    public static BasicItem FluidRegulatorLV = new BasicItem("fluid_regulator_lv", "Configurable up to 640 L/s (as Cover)");
    public static BasicItem FluidRegulatorMV = new BasicItem("fluid_regulator_mv", "Configurable up to 2,560 L/s (as Cover)");
    public static BasicItem FluidRegulatorHV = new BasicItem("fluid_regulator_hv", "Configurable up to 10,240 L/s (as Cover)");
    public static BasicItem FluidRegulatorEV = new BasicItem("fluid_regulator_ev", "Configurable up to 40,960 L/s (as Cover)");
    public static BasicItem FluidRegulatorIV = new BasicItem("fluid_regulator_iv", "Configurable up to 163,840 L/s (as Cover)");
    public static BasicItem ConveyorLV = new BasicItem("conveyor_lv", "1 Stack every 20s (as Cover)");
    public static BasicItem ConveyorMV = new BasicItem("conveyor_mv", "1 Stack every 5s (as Cover)");
    public static BasicItem ConveyorHV = new BasicItem("conveyor_hv", "1 Stack every 1s (as Cover)");
    public static BasicItem ConveyorEV = new BasicItem("conveyor_ev", "1 Stack every 0.5s (as Cover)");
    public static BasicItem ConveyorIV = new BasicItem("conveyor_iv", "1 Stack every 0.05s (as Cover)");
    public static BasicItem PistonLV = new BasicItem("piston_lv");
    public static BasicItem PistonMV = new BasicItem("piston_mv");
    public static BasicItem PistonHV = new BasicItem("piston_hv");
    public static BasicItem PistonEV = new BasicItem("piston_ev");
    public static BasicItem PistonIV = new BasicItem("piston_iv");
    public static BasicItem RobotArmLV = new BasicItem("robot_arm_lv", "Insets into specific Slots (as Cover)");
    public static BasicItem RobotArmMV = new BasicItem("robot_arm_mv", "Insets into specific Slots (as Cover)");
    public static BasicItem RobotArmHV = new BasicItem("robot_arm_hv", "Insets into specific Slots (as Cover)");
    public static BasicItem RobotArmEV = new BasicItem("robot_arm_ev", "Insets into specific Slots (as Cover)");
    public static BasicItem RobotArmIV = new BasicItem("robot_arm_iv", "Insets into specific Slots (as Cover)");
    public static BasicItem FieldGenLV = new BasicItem("field_gen_lv");
    public static BasicItem FieldGenMV = new BasicItem("field_gen_mv");
    public static BasicItem FieldGenHV = new BasicItem("field_gen_hv");
    public static BasicItem FieldGenEV = new BasicItem("field_gen_ev");
    public static BasicItem FieldGenIV = new BasicItem("field_gen_iv");
    public static BasicItem EmitterLV = new BasicItem("emitter_lv");
    public static BasicItem EmitterMV = new BasicItem("emitter_mv");
    public static BasicItem EmitterHV = new BasicItem("emitter_hv");
    public static BasicItem EmitterEV = new BasicItem("emitter_ev");
    public static BasicItem EmitterIV = new BasicItem("emitter_iv");
    public static BasicItem SensorLV = new BasicItem("sensor_lv");
    public static BasicItem SensorMV = new BasicItem("sensor_mv");
    public static BasicItem SensorHV = new BasicItem("sensor_hv");
    public static BasicItem SensorEV = new BasicItem("sensor_ev");
    public static BasicItem SensorIV = new BasicItem("sensor_iv");

    public static BasicItem NandChip = new BasicItem("nand_chip", "A very simple circuit");
    public static BasicItem AdvCircuitParts = new BasicItem("adv_circuit_parts", "Used for making Advanced Circuits");
    public static BasicItem EtchedWiringMV = new BasicItem("etched_wiring_mv", "Circuit board parts");
    public static BasicItem EtchedWiringHV = new BasicItem("etched_wiring_hv", "Circuit board parts");
    public static BasicItem EtchedWiringEV = new BasicItem("etched_wiring_ev", "Circuit board parts");
    public static BasicItem EngravedCrystalChip = new BasicItem("engraved_crystal_chip", "Needed for Circuits");
    public static BasicItem EngravedLapotronChip = new BasicItem("engraved_lapotron_chip", "Needed for Circuits");
    public static BasicItem CircuitBoardEmpty = new BasicItem("circuit_board_empty", "A board Part");
    public static BasicItem CircuitBoardBasic = new BasicItem("circuit_board_basic", "A basic Board");
    public static BasicItem CircuitBoardAdv = new BasicItem("circuit_board_adv", "An advanced Board");
    public static BasicItem CircuitBoardProcessorEmpty = new BasicItem("circuit_board_processor_empty", "A Processor Board Part");
    public static BasicItem CircuitBoardProcessor = new BasicItem("circuit_board_processor", "A Processor Board");
    public static BasicItem CircuitBasic = new BasicItem("circuit_basic", "A basic Circuit");
    public static BasicItem CircuitGood = new BasicItem("circuit_good", "A good Circuit");
    public static BasicItem CircuitAdv = new BasicItem("circuit_adv", "An advanced Circuit");
    public static BasicItem CircuitDataStorage = new BasicItem("circuit_data_storage", "A Data Storage Chip");
    public static BasicItem CircuitDataControl = new BasicItem("circuit_data_control", "A Data Control Processor");
    public static BasicItem CircuitEnergyFlow = new BasicItem("circuit_energy_flow", "A High Voltage Processor");
    public static BasicItem CircuitDataOrb = new BasicItem("circuit_data_orb", "A High Capacity Data Storage");
    public static BasicItem DataStick = new BasicItem("data_stick", "A Low Capacity Data Storage");

    public static BasicItem BatteryTantalum = new BasicItem("battery_tantalum", "Reusable");
    public static BasicItem BatteryHullSmall = new BasicItem("battery_hull_small", "An empty LV Battery Hull");
    public static BasicItem BatteryHullMedium = new BasicItem("battery_hull_medium", "An empty MV Battery Hull");
    public static BasicItem BatteryHullLarge = new BasicItem("battery_hull_large", "An empty HV Battery Hull");
    public static BasicItem BatterySmallAcid = new BasicItem("battery_small_acid", "Single Use");
    public static BasicItem BatterySmallMercury = new BasicItem("battery_small_mercury", "Single Use");
    public static BasicItem BatterySmallCadmium = new BasicItem("battery_small_cadmium", "Reusable");
    public static BasicItem BatterySmallLithium = new BasicItem("battery_small_lithium", "Reusable");
    public static BasicItem BatterySmallSodium = new BasicItem("battery_small_sodium", "Reusable");
    public static BasicItem BatteryMediumAcid = new BasicItem("battery_medium_acid", "Single Use");
    public static BasicItem BatteryMediumMercury = new BasicItem("battery_medium_mercury", "Single Use");
    public static BasicItem BatteryMediumCadmium = new BasicItem("battery_medium_cadmium", "Reusable");
    public static BasicItem BatteryMediumLithium = new BasicItem("battery_medium_lithium", "Reusable");
    public static BasicItem BatteryMediumSodium = new BasicItem("battery_medium_sodium", "Reusable");
    public static BasicItem BatteryLargeAcid = new BasicItem("battery_large_acid", "Single Use");
    public static BasicItem BatteryLargeMercury = new BasicItem("battery_large_mercury", "Single Use");
    public static BasicItem BatteryLargeCadmium = new BasicItem("battery_large_cadmium", "Reusable");
    public static BasicItem BatteryLargeLithium = new BasicItem("battery_large_lithium", "Reusable");
    public static BasicItem BatteryLargeSodium = new BasicItem("battery_large_sodium", "Reusable");
    public static BasicItem BatteryEnergyOrb = new BasicItem("battery_energy_orb");
    public static BasicItem BatteryEnergyOrbCluster = new BasicItem("battery_energy_orb_cluster");

    public static BasicItem EmptyShape = new BasicItem("empty_shape_plate", "Raw plate to make Molds and Extruder Shapes");
    public static BasicItem MoldPlate = new BasicItem("mold_plate", "Mold for making Plates");
    public static BasicItem MoldGear = new BasicItem("mold_gear", "Mold for making Gears");
    public static BasicItem MoldGearSmall = new BasicItem("mold_small_gear", "Mold for making Small Gears");
    public static BasicItem MoldCoinage = new BasicItem("mold_coinage", "Secure Mold for making Coins (Don't lose it!)");
    public static BasicItem MoldBottle = new BasicItem("mold_bottle", "Mold for making Bottles");
    public static BasicItem MoldIngot = new BasicItem("mold_ingot", "Mold for making Ingots");
    public static BasicItem MoldBall = new BasicItem("mold_ball", "Mold for making Balls");
    public static BasicItem MoldBlock = new BasicItem("mold_block", "Mold for making Blocks");
    public static BasicItem MoldNugget = new BasicItem("mold_nugget", "Mold for making Nuggets");
    public static BasicItem MoldAnvil = new BasicItem("mold_anvil", "Mold for making Anvils");
    public static BasicItem ShapePlate = new BasicItem("shape_plate", "Shape for making Plates");
    public static BasicItem ShapeRod = new BasicItem("shape_rod", "Shape for making Rods");
    public static BasicItem ShapeBolt = new BasicItem("shape_bolt", "Shape for making Bolts");
    public static BasicItem ShapeRing = new BasicItem("shape_ring", "Shape for making Rings");
    public static BasicItem ShapeCell = new BasicItem("shape_cell", "Shape for making Cells");
    public static BasicItem ShapeIngot = new BasicItem("shape_ingot", "Shape for making Ingots");
    public static BasicItem ShapeWire = new BasicItem("shape_wire", "Shape for making Wires");
    public static BasicItem ShapePipeTiny = new BasicItem("shape_pipe_tiny", "Shape for making Tiny Pipes");
    public static BasicItem ShapePipeSmall = new BasicItem("shape_pipe_small", "Shape for making Small Pipes");
    public static BasicItem ShapePipeNormal = new BasicItem("shape_pipe_normal", "Shape for making Normal Pipes");
    public static BasicItem ShapePipeLarge = new BasicItem("shape_pipe_large", "Shape for making Large Pipes");
    public static BasicItem ShapePipeHuge = new BasicItem("shape_pipe_huge", "Shape for making Huge Pipes");
    public static BasicItem ShapeBlock = new BasicItem("shape_block", "Shape for making Blocks");
    public static BasicItem ShapeHeadSword = new BasicItem("shape_head_sword", "Shape for making Sword Blades");
    public static BasicItem ShapeHeadPickaxe = new BasicItem("shape_head_pickaxe", "Shape for making Pickaxe Heads");
    public static BasicItem ShapeHeadShovel = new BasicItem("shape_head_shovel", "Shape for making Shovel Heads");
    public static BasicItem ShapeHeadAxe = new BasicItem("shape_head_axe", "Shape for making Axe Heads");
    public static BasicItem ShapeHeadHoe = new BasicItem("shape_head_hoe", "Shape for making Hoe Heads");
    public static BasicItem ShapeHeadHammer = new BasicItem("shape_head_hammer", "Shape for making Hammer Heads");
    public static BasicItem ShapeHeadFile = new BasicItem("shape_head_file", "Shape for making File Heads");
    public static BasicItem ShapeHeadSaw = new BasicItem("shape_head_saw", "Shape for making Saw Heads");
    public static BasicItem ShapeGear = new BasicItem("shape_gear", "Shape for making Gears");
    public static BasicItem ShapeGearSmall = new BasicItem("shape_gear_small", "Shape for making Small Gears");
    public static BasicItem ShapeBottle = new BasicItem("shape_bottle", "Shape for making Bottles"); //TODO needed?

    //TODO optional items (register anyway, but don't show in JEI?)
    //TODO move to IC2+IC2C Registrar
    public static BasicItem DropTin = new BasicItem("drop_tin", "Source of Tin")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static BasicItem DropLead = new BasicItem("drop_lead", "Source of Lead")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static BasicItem DropSilver = new BasicItem("drop_silver", "Source of Silver")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static BasicItem DropIron = new BasicItem("drop_iron", "Source of Iron")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static BasicItem DropGold = new BasicItem("drop_gold", "Source of Gold")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static BasicItem DropAluminium = new BasicItem("drop_aluminium", "Source of Aluminium")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static BasicItem DropTitanium = new BasicItem("drop_titanium", "Source of Titanium")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static BasicItem DropUranium = new BasicItem("drop_uranium", "Source of Uranium")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static BasicItem DropUranite = new BasicItem("drop_uranite", "Source of Uranite")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static BasicItem DropThorium = new BasicItem("drop_thorium", "Source of Thorium")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static BasicItem DropNickel = new BasicItem("drop_nickel", "Source of Nickel")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static BasicItem DropZinc = new BasicItem("drop_zinc", "Source of Zinc")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static BasicItem DropManganese = new BasicItem("drop_manganese", "Source of Manganese")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static BasicItem DropCopper = new BasicItem("drop_copper", "Source of Copper")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static BasicItem DropTungsten = new BasicItem("drop_tungsten", "Source of Tungsten")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static BasicItem DropPlatinum = new BasicItem("drop_platinum", "Source of Platinum")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static BasicItem DropIridium = new BasicItem("drop_iridium", "Source of Iridium")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static BasicItem DropOsmium = new BasicItem("drop_osmium", "Source of Osmium")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static BasicItem DropNaquadah = new BasicItem("drop_naquadah", "Source of Naquadah")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static BasicItem DropEmerald = new BasicItem("drop_emerald", "Source of Emeralds")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static BasicItem DropOil = new BasicItem("drop_oil", "Source of Oil")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static BasicItem DropUUM = new BasicItem("drop_uum", "Source of UU Matter")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;
    public static BasicItem DropUUA = new BasicItem("drop_uua", "Source of UU Amplifier")/*.optional(Ref.MOD_IC2, Ref.MOD_IC2C)*/;

    //TODO move to Forestry Registrar
    public static BasicItem CombLignite = new BasicItem("comb_lignite", "")/*.optional(Ref.MOD_FR)*/;
    public static BasicItem CombCoal = new BasicItem("comb_coal", "")/*.optional(Ref.MOD_FR)*/;
    public static BasicItem CombResin = new BasicItem("comb_resin", "")/*.optional(Ref.MOD_FR)*/;
    public static BasicItem CombOil = new BasicItem("comb_oil", "")/*.optional(Ref.MOD_FR)*/;
    public static BasicItem CombStone = new BasicItem("comb_stone", "")/*.optional(Ref.MOD_FR)*/;
    public static BasicItem CombCertus = new BasicItem("comb_certus", "")/*.required(Ref.MOD_FR, Ref.MOD_AE)*/;
    public static BasicItem CombRedstone = new BasicItem("comb_redstone", "")/*.optional(Ref.MOD_FR)*/;
    public static BasicItem CombLapis = new BasicItem("comb_lapis", "")/*.optional(Ref.MOD_FR)*/;
    public static BasicItem CombRuby = new BasicItem("comb_ruby", "")/*.optional(Ref.MOD_FR)*/;
    public static BasicItem CombSapphire = new BasicItem("comb_sapphire", "")/*.optional(Ref.MOD_FR)*/;
    public static BasicItem CombDiamond = new BasicItem("comb_diamond", "")/*.optional(Ref.MOD_FR)*/;
    public static BasicItem CombOlivine = new BasicItem("comb_olivine", "")/*.optional(Ref.MOD_FR)*/;
    public static BasicItem CombEmerald = new BasicItem("comb_emerald", "")/*.optional(Ref.MOD_FR)*/;
    public static BasicItem CombSlag = new BasicItem("comb_slag", "")/*.optional(Ref.MOD_FR)*/;
    public static BasicItem CombCopper = new BasicItem("comb_copper", "")/*.optional(Ref.MOD_FR)*/;
    public static BasicItem CombTin = new BasicItem("comb_tin", "")/*.optional(Ref.MOD_FR)*/;
    public static BasicItem CombLead = new BasicItem("comb_lead", "")/*.optional(Ref.MOD_FR)*/;
    public static BasicItem CombIron = new BasicItem("comb_iron", "")/*.optional(Ref.MOD_FR)*/;
    public static BasicItem CombSteel = new BasicItem("comb_steel", "")/*.optional(Ref.MOD_FR)*/;
    public static BasicItem CombNickel = new BasicItem("comb_nickel", "")/*.optional(Ref.MOD_FR)*/;
    public static BasicItem CombZinc = new BasicItem("comb_zinc", "")/*.optional(Ref.MOD_FR)*/;
    public static BasicItem CombSilver = new BasicItem("comb_silver", "")/*.optional(Ref.MOD_FR)*/;
    public static BasicItem CombGold = new BasicItem("comb_gold", "")/*.optional(Ref.MOD_FR)*/;
    public static BasicItem CombAluminium = new BasicItem("comb_aluminium", "")/*.optional(Ref.MOD_FR)*/;
    public static BasicItem CombManganese = new BasicItem("comb_manganese", "")/*.optional(Ref.MOD_FR)*/;
    public static BasicItem CombTitanium = new BasicItem("comb_titanium", "")/*.optional(Ref.MOD_FR)*/;
    public static BasicItem CombChrome = new BasicItem("comb_chrome", "")/*.optional(Ref.MOD_FR)*/;
    public static BasicItem CombTungsten = new BasicItem("comb_tungsten", "")/*.optional(Ref.MOD_FR)*/;
    public static BasicItem CombPlatinum = new BasicItem("comb_platinum", "")/*.optional(Ref.MOD_FR)*/;
    public static BasicItem CombIridium = new BasicItem("comb_iridium", "")/*.optional(Ref.MOD_FR)*/;
    public static BasicItem CombUranium = new BasicItem("comb_uranium", "")/*.optional(Ref.MOD_FR)*/;
    public static BasicItem CombPlutonium = new BasicItem("comb_plutonium", "")/*.optional(Ref.MOD_FR)*/;
    public static BasicItem CombNaquadah = new BasicItem("comb_naquadah", "")/*.optional(Ref.MOD_FR)*/;

    //TODO
    //public static BlockRubberSapling RUBBER_SAPLING = new BlockRubberSapling();
    //public static BlockRubberLog RUBBER_LOG = new BlockRubberLog();
    //public static BlockLeavesBase RUBBER_LEAVES = new BlockLeavesBase("rubber_leaves", RUBBER_SAPLING);

    public static BlockBasic CASING_FIRE_BRICK = new BlockBasic("fire_brick");

    public static BlockCasingMachine CASING_ULV = new BlockCasingMachine("ulv");
    public static BlockCasingMachine CASING_LV = new BlockCasingMachine("lv");
    public static BlockCasingMachine CASING_MV = new BlockCasingMachine("mv");
    public static BlockCasingMachine CASING_HV = new BlockCasingMachine("hv");
    public static BlockCasingMachine CASING_EV = new BlockCasingMachine("ev");
    public static BlockCasingMachine CASING_IV = new BlockCasingMachine("iv");
    public static BlockCasingMachine CASING_LUV = new BlockCasingMachine("luv");
    public static BlockCasingMachine CASING_ZPM = new BlockCasingMachine("zpm");
    public static BlockCasingMachine CASING_UV = new BlockCasingMachine("uv");
    public static BlockCasingMachine CASING_MAX = new BlockCasingMachine("max");

    public static BlockCasing CASING_BRONZE = new BlockCasing("bronze");
    public static BlockCasing CASING_BRICKED_BRONZE = new BlockCasing("bricked_bronze");
    public static BlockCasing CASING_BRONZE_PLATED_BRICK = new BlockCasing("bronze_plated_brick");
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
    public static BlockCasing CASING_FUSION_1 = new BlockCasing("fusion_1", Textures.FUSION_1_CT);
    public static BlockCasing CASING_FUSION_2 = new BlockCasing("fusion_2", Textures.FUSION_2_CT);
    public static BlockCasing CASING_FUSION_3 = new BlockCasing("fusion_3", Textures.FUSION_3_CT);

    public static BlockCasing CASING_TURBINE_1 = new BlockTurbineCasing("turbine_1");
    public static BlockCasing CASING_TURBINE_2 = new BlockTurbineCasing("turbine_2");
    public static BlockCasing CASING_TURBINE_3 = new BlockTurbineCasing("turbine_3");
    public static BlockCasing CASING_TURBINE_4 = new BlockTurbineCasing("turbine_4");

    public static BlockCoil COIL_CUPRONICKEL = new BlockCoil("cupronickel", 113); //1808
    public static BlockCoil COIL_KANTHAL = new BlockCoil("kanthal", 169); //2704
    public static BlockCoil COIL_NICHROME = new BlockCoil("nichrome", 225); //3600
    public static BlockCoil COIL_TUNGSTENSTEEL = new BlockCoil("tungstensteel", 282); //4512
    public static BlockCoil COIL_HSSG = new BlockCoil("hssg", 338); //5408
    public static BlockCoil COIL_NAQUADAH = new BlockCoil("naquadah", 450); //7200
    public static BlockCoil COIL_NAQUADAH_ALLOY = new BlockCoil("naquadah_alloy", 563); //9008
    public static BlockCoil COIL_FUSION = new BlockCoil("fusion", 563); //9008
    public static BlockCoil COIL_SUPERCONDUCTOR = new BlockCoil("superconductor", 563); //9008

//    public static BlockCable CABLE_RED_ALLOY = new BlockCable(RedAlloy, 0, 1, 1, Tier.ULV); //ULV
//    public static BlockCable CABLE_COBALT = new BlockCable(Cobalt, 2, 4, 2, Tier.LV); //LV
//    public static BlockCable CABLE_LEAD = new BlockCable(Lead, 2, 4, 2, Tier.LV);
//    public static BlockCable CABLE_TIN = new BlockCable(Tin, 1, 2, 1, Tier.LV);
//    public static BlockCable CABLE_ZINC = new BlockCable(Zinc, 1, 2, 1, Tier.LV);
//    public static BlockCable CABLE_SOLDERING_ALLOY = new BlockCable(SolderingAlloy, 1, 2, 1, Tier.LV);
//    public static BlockCable CABLE_IRON = new BlockCable(Iron, HC ? 3 : 4, HC ? 6 : 8, 2, Tier.MV); //MV
//    public static BlockCable CABLE_NICKEL = new BlockCable(Nickel, HC ? 3 : 5, HC ? 6 : 10, 3, Tier.MV);
//    public static BlockCable CABLE_CUPRONICKEL = new BlockCable(Cupronickel, HC ? 3 : 4, HC ? 6 : 8, 2, Tier.MV);
//    public static BlockCable CABLE_COPPER = new BlockCable(Copper, HC ? 2 : 3, HC ? 4 : 6, 1, Tier.MV);
//    public static BlockCable CABLE_ANNEALED_COPPER = new BlockCable(AnnealedCopper, HC ? 1 : 2, HC ? 2 : 4, 1, Tier.MV);
//    public static BlockCable CABLE_KANTHAL = new BlockCable(Kanthal, HC ? 3 : 8, HC ? 6 : 16, 4, Tier.HV); //HV
//    public static BlockCable CABLE_GOLD = new BlockCable(Gold, HC ? 2 : 6, HC ? 4 : 12, 3, Tier.HV);
//    public static BlockCable CABLE_ELECTRUM = new BlockCable(Electrum, HC ? 2 : 5, HC ? 4 : 10, 2, Tier.HV);
//    public static BlockCable CABLE_SILVER = new BlockCable(Silver, HC ? 1 : 4, HC ? 2 : 8, 1, Tier.HV);
//    public static BlockCable CABLE_NICHROME = new BlockCable(Nichrome, HC ? 4 : 32, HC ? 8 : 64, 3, Tier.EV); //EV
//    public static BlockCable CABLE_STEEL = new BlockCable(Steel, HC ? 2 : 16, HC ? 4 : 32, 2, Tier.EV);
//    public static BlockCable CABLE_TITANIUM = new BlockCable(Titanium, HC ? 2 : 12, HC ? 4 : 24, 4, Tier.EV);
//    public static BlockCable CABLE_ALUMINIUM = new BlockCable(Aluminium, HC ? 1 : 8, HC ? 2 : 16, 1, Tier.EV);
//    public static BlockCable CABLE_GRAPHENE = new BlockCable(Graphene, HC ? 1 : 16, HC ? 2 : 32, 1, Tier.IV); //IV
//    public static BlockCable CABLE_OSMIUM = new BlockCable(Osmium, HC ? 2 : 32, HC ? 4 : 64, 4, Tier.IV);
//    public static BlockCable CABLE_PLATINUM = new BlockCable(Platinum, HC ? 1 : 16, HC ? 2 : 32, 2, Tier.IV);
//    public static BlockCable CABLE_TUNGSTENSTEEL = new BlockCable(TungstenSteel, HC ? 1 : 14, HC ? 4 : 28, 3, Tier.IV);
//    public static BlockCable CABLE_TUNGSTEN = new BlockCable(Tungsten, HC ? 2 : 12, HC ? 4 : 24, 1, Tier.IV);
//    public static BlockCable CABLE_HSSG = new BlockCable(HSSG, HC ? 2 : 128, HC ? 4 : 256, 4, Tier.LUV); //LUV
//    public static BlockCable CABLE_NIOBIUM_TITANIUM = new BlockCable(NiobiumTitanium, HC ? 2 : 128, HC ? 4 : 256, 4, Tier.LUV);
//    public static BlockCable CABLE_VANADIUM_GALLIUM = new BlockCable(VanadiumGallium, HC ? 2 : 128, HC ? 4 : 256, 4, Tier.LUV);
//    public static BlockCable CABLE_YTTRIUM_BARIUM_CUPRATE = new BlockCable(YttriumBariumCuprate, HC ? 4 : 256, HC ? 8 : 512, 4, Tier.LUV);
//    public static BlockCable CABLE_NAQUADAH = new BlockCable(Naquadah, HC ? 2 : 64, HC ? 4 : 128, 2, Tier.ZPM); //ZPM
//    public static BlockCable CABLE_NAQUADAH_ALLOY = new BlockCable(NaquadahAlloy, HC ? 4 : 64, HC ? 8 : 128, 2, Tier.ZPM);
//    public static BlockCable CABLE_DURANIUM = new BlockCable(Duranium, HC ? 8 : 64, HC ? 16 : 128, 1, Tier.ZPM);
//    public static BlockCable CABLE_SUPERCONDUCTOR = new BlockCable(Superconductor, 1, 1, 4, Tier.MAX); //MAX
//
//    public static BlockFluidPipe FLUID_PIPE_WOOD = new BlockFluidPipe(Wood, 30, 350, false, PipeSize.SMALL, PipeSize.NORMAL, PipeSize.LARGE).setCapacities(10, 10, 30, 60, 60, 60);
//    public static BlockFluidPipe FLUID_PIPE_COPPER = new BlockFluidPipe(Copper, 10, 1000, true);
//    public static BlockFluidPipe FLUID_PIPE_BRONZE = new BlockFluidPipe(Bronze, 20, 2000, true);
//    public static BlockFluidPipe FLUID_PIPE_STEEL = new BlockFluidPipe(Steel, 40, 2500, true);
//    public static BlockFluidPipe FLUID_PIPE_STAINLESS_STEEL = new BlockFluidPipe(StainlessSteel, 60, 3000, true);
//    public static BlockFluidPipe FLUID_PIPE_TITANIUM = new BlockFluidPipe(Titanium, 80, 5000, true);
//    public static BlockFluidPipe FLUID_PIPE_TUNGSTENSTEEL = new BlockFluidPipe(TungstenSteel, 100, 7500, true);
//    public static BlockFluidPipe FLUID_PIPE_PLASTIC = new BlockFluidPipe(Plastic, 60, 250, true);
//    public static BlockFluidPipe FLUID_PIPE_POLYTETRAFLUOROETHYLENE = new BlockFluidPipe(Polytetrafluoroethylene, 480, 600, true);
//    public static BlockFluidPipe FLUID_PIPE_HIGH_PRESSURE = new BlockFluidPipe(HighPressure, 7200, 1500, true, PipeSize.SMALL, PipeSize.NORMAL, PipeSize.LARGE).setCapacities(4800, 4800, 4800, 7200, 9600, 9600);
//    public static BlockFluidPipe FLUID_PIPE_PLASMA = new BlockFluidPipe(PlasmaContainment, 240, 100000, true, PipeSize.NORMAL).setCapacities(240, 240, 240, 240, 240, 240);
//
//    public static BlockItemPipe ITEM_PIPE_CUPRONICKEL = new BlockItemPipe(Cupronickel, 1);
//    public static BlockItemPipe ITEM_PIPE_COBALT_BRASS = new BlockItemPipe(CobaltBrass, 1);
//    public static BlockItemPipe ITEM_PIPE_BRASS = new BlockItemPipe(Brass, 1);
//    public static BlockItemPipe ITEM_PIPE_ELECTRUM = new BlockItemPipe(Electrum, 2);
//    public static BlockItemPipe ITEM_PIPE_ROSE_GOLD = new BlockItemPipe(RoseGold, 2);
//    public static BlockItemPipe ITEM_PIPE_STERLING_SILVER = new BlockItemPipe(SterlingSilver, 2);
//    public static BlockItemPipe ITEM_PIPE_PLATINUM = new BlockItemPipe(Platinum, 4);
//    public static BlockItemPipe ITEM_PIPE_ULTIMET = new BlockItemPipe(Ultimet, 4);
//    public static BlockItemPipe ITEM_PIPE_POLYVINYL_CHLORIDE = new BlockItemPipe(PolyvinylChloride, 4);
//    public static BlockItemPipe ITEM_PIPE_OSMIUM = new BlockItemPipe(Osmium, 8);

//    public static BlockStorage BLOCK_0 = new BlockStorage("0", MaterialType.BLOCK, Aluminium, Beryllium, Bismuth, Carbon, Chrome, Cobalt, Gold, Iridium, Iron, Lanthanum, Lead, Manganese, Molybdenum, Neodymium, Neutronium, Nickel);
//    public static BlockStorage BLOCK_1 = new BlockStorage("1", MaterialType.BLOCK, Osmium, Palladium, Platinum, Plutonium, Plutonium241, Silver, Thorium, Titanium, Tungsten, Uranium, Uranium235, Americium, Antimony, Arsenic, Caesium, Cerium);
//    public static BlockStorage BLOCK_2 = new BlockStorage("2", MaterialType.BLOCK, Copper, Dysprosium, Europium, Gallium, Indium, Lithium, Lutetium, Magnesium, Niobium, Potassium, Silicon, Tantalum, Tin, Vanadium, Yttrium, Zinc);
//    public static BlockStorage BLOCK_3 = new BlockStorage("3", MaterialType.BLOCK, Dilithium, NetherQuartz, NetherStar, Quartzite, BlueTopaz, Charcoal, Coal, Lignite, CoalCoke, LigniteCoke, Diamond, Emerald, GreenSapphire, Ruby, BlueSapphire, Tanzanite);
//    public static BlockStorage BLOCK_4 = new BlockStorage("4", MaterialType.BLOCK, Topaz, Olivine, Opal, Amethyst, Lapis, EnderPearl, EnderEye, Phosphorus, GarnetRed, GarnetYellow, AnnealedCopper, BatteryAlloy, Brass, Bronze, Cupronickel, Electrum);
//    public static BlockStorage BLOCK_5 = new BlockStorage("5", MaterialType.BLOCK, Invar, Kanthal, Magnalium, Nichrome, NiobiumTitanium, SolderingAlloy, StainlessSteel, Steel, Ultimet, VanadiumGallium, WroughtIron, YttriumBariumCuprate, SterlingSilver, RoseGold, BlackBronze, BismuthBronze);
//    public static BlockStorage BLOCK_6 = new BlockStorage("6", MaterialType.BLOCK, BlackSteel, RedSteel, BlueSteel, TungstenSteel, RedAlloy, CobaltBrass, IronMagnetic, SteelMagnetic, NeodymiumMagnetic, NickelZincFerrite, TungstenCarbide, VanadiumSteel, HSSG, HSSE, HSSS, Osmiridium);
//    public static BlockStorage BLOCK_7 = new BlockStorage("7", MaterialType.BLOCK, Duranium, Naquadah, NaquadahAlloy, NaquadahEnriched, Naquadria, Tritanium, Vibranium, Plastic, Epoxid, Silicone, Polycaprolactam, Polytetrafluoroethylene, Rubber, PolyphenyleneSulfide, Polystyrene, StyreneButadieneRubber);
//    public static BlockStorage BLOCK_8 = new BlockStorage("8", MaterialType.BLOCK, PolyvinylChloride, GalliumArsenide, EpoxidFiberReinforced);
//
//    public static BlockStorage FRAME_0 = new BlockStorage("0", MaterialType.FRAME, Aluminium, Iridium, Iron, Neutronium, Tin, Brass, Bronze, Invar, StainlessSteel, Steel, WroughtIron, BlackSteel, BlueSteel, TungstenSteel, HSSG, HSSE);
//    public static BlockStorage FRAME_1 = new BlockStorage("1", MaterialType.FRAME, Osmiridium, Tritanium, Vibranium, Polytetrafluoroethylene);
}
