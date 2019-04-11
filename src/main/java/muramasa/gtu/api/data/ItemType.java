package muramasa.gtu.api.data;

import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.items.ItemFluidCell;
import muramasa.gtu.api.items.StandardItem;
import muramasa.gtu.api.registration.GregTechRegistry;
import muramasa.gtu.api.util.GTLoc;
import muramasa.gtu.api.util.Utils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;

import java.util.Collection;
import java.util.LinkedHashMap;

public class ItemType implements IStringSerializable {

    private static LinkedHashMap<String, ItemType> TYPE_LOOKUP = new LinkedHashMap<>();

    public static ItemType StickyResin = new ItemType("sticky_resin");

//    public static ItemType EmptyCell = new ItemType("empty_cell");
    public static ItemType DebugScanner = new ItemType("debug_scanner", TextFormatting.AQUA + "" + TextFormatting.ITALIC + "Development Item");
//    public static ItemType PumpComponent = new ItemType("component_pump", "");
//    public static ItemType ConveyorComponent = new ItemType("component_conveyor", "");
//    public static ItemType ItemPort = new ItemType("item_port", "Can be placed on machines as a cover");
//    public static ItemType FluidPort = new ItemType("fluid_port", "Can be placed on machines as a cover");
    public static ItemType EnergyPort = new ItemType("energy_port", "Can be placed on machines as a cover");
    public static ItemType ComputerMonitor = new ItemType("computer_monitor", "Can be placed on machines as a cover");

    public static ItemType CellTin = new ItemType("fluid_cell_tin") {
        @Override
        public Item getNewInstance() {
            return new ItemFluidCell(this, Ref.MB_INGOT * 9);
        }
    };
    public static ItemType CellSteel = new ItemType("fluid_cell_steel") {
        @Override
        public Item getNewInstance() {
            return new ItemFluidCell(this, 16000);
        }
    };
    public static ItemType CellTungstensteel = new ItemType("fluid_cell_tungstensteel") {
        @Override
        public Item getNewInstance() {
            return new ItemFluidCell(this, 64000);
        }
    };

    public static ItemType ItemFilter = new ItemType("item_filter");
    public static ItemType DiamondSawBlade = new ItemType("diamond_saw_blade");
    public static ItemType DiamondGrindHead = new ItemType("diamond_grind_head");
    public static ItemType TungstenGrindHead = new ItemType("tungsten_grind_head");
    public static ItemType IridiumAlloyIngot = new ItemType("iridium_alloy_ingot", "Used to make Iridium Plates");
    public static ItemType IridiumReinforcedPlate = new ItemType("iridium_reinforced_plate", "GT2s Most Expensive Component");
    public static ItemType IridiumNeutronReflector = new ItemType("iridium_neutron_reflector", "Indestructible");
    public static ItemType QuantumEye = new ItemType("quantum_eye", "Improved Ender Eye");
    public static ItemType QuantumStat = new ItemType("quantum_star", "Improved Nether Star");
    public static ItemType GraviStar = new ItemType("gravi_star", "Ultimate Nether Star");

    public static ItemType MotorLV = new ItemType("motor_lv");
    public static ItemType MotorMV = new ItemType("motor_mv");
    public static ItemType MotorHV = new ItemType("motor_hv");
    public static ItemType MotorEV = new ItemType("motor_ev");
    public static ItemType MotorIV = new ItemType("motor_iv");
    public static ItemType PumpLV = new ItemType("pump_lv", "640 L/s (as Cover)");
    public static ItemType PumpMV = new ItemType("pump_mv", "2,560 L/s (as Cover)");
    public static ItemType PumpHV = new ItemType("pump_hv", "10,240 L/s (as Cover)");
    public static ItemType PumpEV = new ItemType("pump_ev", "40,960 L/s (as Cover)");
    public static ItemType PumpIV = new ItemType("pump_iv", "163,840 L/s (as Cover)");
    public static ItemType FluidRegulatorLV = new ItemType("fluid_regulator_lv", "Configurable up to 640 L/s (as Cover)");
    public static ItemType FluidRegulatorMV = new ItemType("fluid_regulator_mv", "Configurable up to 2,560 L/s (as Cover)");
    public static ItemType FluidRegulatorHV = new ItemType("fluid_regulator_hv", "Configurable up to 10,240 L/s (as Cover)");
    public static ItemType FluidRegulatorEV = new ItemType("fluid_regulator_ev", "Configurable up to 40,960 L/s (as Cover)");
    public static ItemType FluidRegulatorIV = new ItemType("fluid_regulator_iv", "Configurable up to 163,840 L/s (as Cover)");
    public static ItemType ConveyorLV = new ItemType("conveyor_lv", "1 Stack every 20s (as Cover)");
    public static ItemType ConveyorMV = new ItemType("conveyor_mv", "1 Stack every 5s (as Cover)");
    public static ItemType ConveyorHV = new ItemType("conveyor_hv", "1 Stack every 1s (as Cover)");
    public static ItemType ConveyorEV = new ItemType("conveyor_ev", "1 Stack every 0.5s (as Cover)");
    public static ItemType ConveyorIV = new ItemType("conveyor_iv", "1 Stack every 0.05s (as Cover)");
    public static ItemType PistonLV = new ItemType("piston_lv");
    public static ItemType PistonMV = new ItemType("piston_mv");
    public static ItemType PistonHV = new ItemType("piston_hv");
    public static ItemType PistonEV = new ItemType("piston_ev");
    public static ItemType PistonIV = new ItemType("piston_iv");
    public static ItemType RobotArmLV = new ItemType("robot_arm_lv", "Insets into specific Slots (as Cover)");
    public static ItemType RobotArmMV = new ItemType("robot_arm_mv", "Insets into specific Slots (as Cover)");
    public static ItemType RobotArmHV = new ItemType("robot_arm_hv", "Insets into specific Slots (as Cover)");
    public static ItemType RobotArmEV = new ItemType("robot_arm_ev", "Insets into specific Slots (as Cover)");
    public static ItemType RobotArmIV = new ItemType("robot_arm_iv", "Insets into specific Slots (as Cover)");
    public static ItemType FieldGenLV = new ItemType("field_gen_lv");
    public static ItemType FieldGenMV = new ItemType("field_gen_mv");
    public static ItemType FieldGenHV = new ItemType("field_gen_hv");
    public static ItemType FieldGenEV = new ItemType("field_gen_ev");
    public static ItemType FieldGenIV = new ItemType("field_gen_iv");
    public static ItemType EmitterLV = new ItemType("emitter_lv");
    public static ItemType EmitterMV = new ItemType("emitter_mv");
    public static ItemType EmitterHV = new ItemType("emitter_hv");
    public static ItemType EmitterEV = new ItemType("emitter_ev");
    public static ItemType EmitterIV = new ItemType("emitter_iv");
    public static ItemType SensorLV = new ItemType("sensor_lv");
    public static ItemType SensorMV = new ItemType("sensor_mv");
    public static ItemType SensorHV = new ItemType("sensor_hv");
    public static ItemType SensorEV = new ItemType("sensor_ev");
    public static ItemType SensorIV = new ItemType("sensor_iv");

    public static ItemType NandChip = new ItemType("nand_chip", "A very simple circuit");
    public static ItemType AdvCircuitParts = new ItemType("adv_circuit_parts", "Used for making Advanced Circuits");
    public static ItemType EtchedWiringMV = new ItemType("etched_wiring_mv", "Circuit board parts");
    public static ItemType EtchedWiringHV = new ItemType("etched_wiring_hv", "Circuit board parts");
    public static ItemType EtchedWiringEV = new ItemType("etched_wiring_ev", "Circuit board parts");
    public static ItemType EngravedCrystalChip = new ItemType("engraved_crystal_chip", "Needed for Circuits");
    public static ItemType EngravedLapotronChip = new ItemType("engraved_lapotron_chip", "Needed for Circuits");
    public static ItemType CircuitBoardEmpty = new ItemType("circuit_board_empty", "A board Part");
    public static ItemType CircuitBoardBasic = new ItemType("circuit_board_basic", "A basic Board");
    public static ItemType CircuitBoardAdv = new ItemType("circuit_board_adv", "An advanced Board");
    public static ItemType CircuitBoardProcessorEmpty = new ItemType("circuit_board_processor_empty", "A Processor Board Part");
    public static ItemType CircuitBoardProcessor = new ItemType("circuit_board_processor", "A Processor Board");
    public static ItemType CircuitBasic = new ItemType("circuit_basic", "A basic Circuit");
    public static ItemType CircuitGood = new ItemType("circuit_good", "A good Circuit");
    public static ItemType CircuitAdv = new ItemType("circuit_adv", "An advanced Circuit");
    public static ItemType CircuitDataStorage = new ItemType("circuit_data_storage", "A Data Storage Chip");
    public static ItemType CircuitDataControl = new ItemType("circuit_data_control", "A Data Control Processor");
    public static ItemType CircuitEnergyFlow = new ItemType("circuit_energy_flow", "A High Voltage Processor");
    public static ItemType CircuitDataOrb = new ItemType("circuit_data_orb", "A High Capacity Data Storage");
    public static ItemType DataStick = new ItemType("data_stick", "A Low Capacity Data Storage");

    public static ItemType BatteryTantalum = new ItemType("battery_tantalum", "Reusable");
    public static ItemType BatteryHullSmall = new ItemType("battery_hull_small", "An empty LV Battery Hull");
    public static ItemType BatteryHullMedium = new ItemType("battery_hull_medium", "An empty MV Battery Hull");
    public static ItemType BatteryHullLarge = new ItemType("battery_hull_large", "An empty HV Battery Hull");
    public static ItemType BatterySmallAcid = new ItemType("battery_small_acid", "Single Use");
    public static ItemType BatterySmallMercury = new ItemType("battery_small_mercury", "Single Use");
    public static ItemType BatterySmallCadmium = new ItemType("battery_small_cadmium", "Reusable");
    public static ItemType BatterySmallLithium = new ItemType("battery_small_lithium", "Reusable");
    public static ItemType BatterySmallSodium = new ItemType("battery_small_sodium", "Reusable");
    public static ItemType BatteryMediumAcid = new ItemType("battery_medium_acid", "Single Use");
    public static ItemType BatteryMediumMercury = new ItemType("battery_medium_mercury", "Single Use");
    public static ItemType BatteryMediumCadmium = new ItemType("battery_medium_cadmium", "Reusable");
    public static ItemType BatteryMediumLithium = new ItemType("battery_medium_lithium", "Reusable");
    public static ItemType BatteryMediumSodium = new ItemType("battery_medium_sodium", "Reusable");
    public static ItemType BatteryLargeAcid = new ItemType("battery_large_acid", "Single Use");
    public static ItemType BatteryLargeMercury = new ItemType("battery_large_mercury", "Single Use");
    public static ItemType BatteryLargeCadmium = new ItemType("battery_large_cadmium", "Reusable");
    public static ItemType BatteryLargeLithium = new ItemType("battery_large_lithium", "Reusable");
    public static ItemType BatteryLargeSodium = new ItemType("battery_large_sodium", "Reusable");
    public static ItemType BatteryEnergyOrb = new ItemType("battery_energy_orb");
    public static ItemType BatteryEnergyOrbCluster = new ItemType("battery_energy_orb_cluster");

    public static ItemType EmptyShape = new ItemType("empty_shape_plate", "Raw plate to make Molds and Extruder Shapes");
    public static ItemType MoldPlate = new ItemType("mold_plate", "Mold for making Plates");
    public static ItemType MoldGear = new ItemType("mold_gear", "Mold for making Gears");
    public static ItemType MoldGearSmall = new ItemType("mold_small_gear", "Mold for making Small Gears");
    public static ItemType MoldCoinage = new ItemType("mold_coinage", "Secure Mold for making Coins (Don't lose it!)");
    public static ItemType MoldBottle = new ItemType("mold_bottle", "Mold for making Bottles");
    public static ItemType MoldIngot = new ItemType("mold_ingot", "Mold for making Ingots");
    public static ItemType MoldBall = new ItemType("mold_ball", "Mold for making Balls");
    public static ItemType MoldBlock = new ItemType("mold_block", "Mold for making Blocks");
    public static ItemType MoldNugget = new ItemType("mold_nugget", "Mold for making Nuggets");
    public static ItemType MoldAnvil = new ItemType("mold_anvil", "Mold for making Anvils");
    public static ItemType ShapePlate = new ItemType("shape_plate", "Shape for making Plates");
    public static ItemType ShapeRod = new ItemType("shape_rod", "Shape for making Rods");
    public static ItemType ShapeBolt = new ItemType("shape_bolt", "Shape for making Bolts");
    public static ItemType ShapeRing = new ItemType("shape_ring", "Shape for making Rings");
    public static ItemType ShapeCell = new ItemType("shape_cell", "Shape for making Cells");
    public static ItemType ShapeIngot = new ItemType("shape_ingot", "Shape for making Ingots");
    public static ItemType ShapeWire = new ItemType("shape_wire", "Shape for making Wires");
    public static ItemType ShapePipeTiny = new ItemType("shape_pipe_tiny", "Shape for making Tiny Pipes");
    public static ItemType ShapePipeSmall = new ItemType("shape_pipe_small", "Shape for making Small Pipes");
    public static ItemType ShapePipeNormal = new ItemType("shape_pipe_normal", "Shape for making Normal Pipes");
    public static ItemType ShapePipeLarge = new ItemType("shape_pipe_large", "Shape for making Large Pipes");
    public static ItemType ShapePipeHuge = new ItemType("shape_pipe_huge", "Shape for making Huge Pipes");
    public static ItemType ShapeBlock = new ItemType("shape_block", "Shape for making Blocks");
    public static ItemType ShapeHeadSword = new ItemType("shape_head_sword", "Shape for making Sword Blades");
    public static ItemType ShapeHeadPickaxe = new ItemType("shape_head_pickaxe", "Shape for making Pickaxe Heads");
    public static ItemType ShapeHeadShovel = new ItemType("shape_head_shovel", "Shape for making Shovel Heads");
    public static ItemType ShapeHeadAxe = new ItemType("shape_head_axe", "Shape for making Axe Heads");
    public static ItemType ShapeHeadHoe = new ItemType("shape_head_hoe", "Shape for making Hoe Heads");
    public static ItemType ShapeHeadHammer = new ItemType("shape_head_hammer", "Shape for making Hammer Heads");
    public static ItemType ShapeHeadFile = new ItemType("shape_head_file", "Shape for making File Heads");
    public static ItemType ShapeHeadSaw = new ItemType("shape_head_saw", "Shape for making Saw Heads");
    public static ItemType ShapeGear = new ItemType("shape_gear", "Shape for making Gears");
    public static ItemType ShapeGearSmall = new ItemType("shape_gear_small", "Shape for making Small Gears");
    public static ItemType ShapeBottle = new ItemType("shape_bottle", "Shape for making Bottles"); //TODO needed?

    public static ItemType DropTin = new ItemType("drop_tin", "Source of Tin").optional(Ref.MOD_IC2, Ref.MOD_IC2C);
    public static ItemType DropLead = new ItemType("drop_lead", "Source of Lead").optional(Ref.MOD_IC2, Ref.MOD_IC2C);
    public static ItemType DropSilver = new ItemType("drop_silver", "Source of Silver").optional(Ref.MOD_IC2, Ref.MOD_IC2C);
    public static ItemType DropIron = new ItemType("drop_iron", "Source of Iron").optional(Ref.MOD_IC2, Ref.MOD_IC2C);
    public static ItemType DropGold = new ItemType("drop_gold", "Source of Gold").optional(Ref.MOD_IC2, Ref.MOD_IC2C);
    public static ItemType DropAluminium = new ItemType("drop_aluminium", "Source of Aluminium").optional(Ref.MOD_IC2, Ref.MOD_IC2C);
    public static ItemType DropTitanium = new ItemType("drop_titanium", "Source of Titanium").optional(Ref.MOD_IC2, Ref.MOD_IC2C);
    public static ItemType DropUranium = new ItemType("drop_uranium", "Source of Uranium").optional(Ref.MOD_IC2, Ref.MOD_IC2C);
    public static ItemType DropUranite = new ItemType("drop_uranite", "Source of Uranite").optional(Ref.MOD_IC2, Ref.MOD_IC2C);
    public static ItemType DropThorium = new ItemType("drop_thorium", "Source of Thorium").optional(Ref.MOD_IC2, Ref.MOD_IC2C);
    public static ItemType DropNickel = new ItemType("drop_nickel", "Source of Nickel").optional(Ref.MOD_IC2, Ref.MOD_IC2C);
    public static ItemType DropZinc = new ItemType("drop_zinc", "Source of Zinc").optional(Ref.MOD_IC2, Ref.MOD_IC2C);
    public static ItemType DropManganese = new ItemType("drop_manganese", "Source of Manganese").optional(Ref.MOD_IC2, Ref.MOD_IC2C);
    public static ItemType DropCopper = new ItemType("drop_copper", "Source of Copper").optional(Ref.MOD_IC2, Ref.MOD_IC2C);
    public static ItemType DropTungsten = new ItemType("drop_tungsten", "Source of Tungsten").optional(Ref.MOD_IC2, Ref.MOD_IC2C);
    public static ItemType DropPlatinum = new ItemType("drop_platinum", "Source of Platinum").optional(Ref.MOD_IC2, Ref.MOD_IC2C);
    public static ItemType DropIridium = new ItemType("drop_iridium", "Source of Iridium").optional(Ref.MOD_IC2, Ref.MOD_IC2C);
    public static ItemType DropOsmium = new ItemType("drop_osmium", "Source of Osmium").optional(Ref.MOD_IC2, Ref.MOD_IC2C);
    public static ItemType DropNaquadah = new ItemType("drop_gold", "Source of Naquadah").optional(Ref.MOD_IC2, Ref.MOD_IC2C);
    public static ItemType DropEmerald = new ItemType("drop_emerald", "Source of Emeralds").optional(Ref.MOD_IC2, Ref.MOD_IC2C);
    public static ItemType DropOil = new ItemType("drop_oil", "Source of Oil").optional(Ref.MOD_IC2, Ref.MOD_IC2C);
    public static ItemType DropUUM = new ItemType("drop_uum", "Source of UU Matter").optional(Ref.MOD_IC2, Ref.MOD_IC2C);
    public static ItemType DropUUA = new ItemType("drop_uua", "Source of UU Amplifier").optional(Ref.MOD_IC2, Ref.MOD_IC2C);

    //TODO move to Forestry Registrar
    public static ItemType CombLignite = new ItemType("comb_lignite", "").optional(Ref.MOD_FR);
    public static ItemType CombCoal = new ItemType("comb_coal", "").optional(Ref.MOD_FR);
    public static ItemType CombResin = new ItemType("comb_resin", "").optional(Ref.MOD_FR);
    public static ItemType CombOil = new ItemType("comb_oil", "").optional(Ref.MOD_FR);
    public static ItemType CombStone = new ItemType("comb_stone", "").optional(Ref.MOD_FR);
    public static ItemType CombCertus = new ItemType("comb_certus", "").required(Ref.MOD_FR, Ref.MOD_AE);
    public static ItemType CombRedstone = new ItemType("comb_redstone", "").optional(Ref.MOD_FR);
    public static ItemType CombLapis = new ItemType("comb_lapis", "").optional(Ref.MOD_FR);
    public static ItemType CombRuby = new ItemType("comb_ruby", "").optional(Ref.MOD_FR);
    public static ItemType CombSapphire = new ItemType("comb_sapphire", "").optional(Ref.MOD_FR);
    public static ItemType CombDiamond = new ItemType("comb_diamond", "").optional(Ref.MOD_FR);
    public static ItemType CombOlivine = new ItemType("comb_olivine", "").optional(Ref.MOD_FR);
    public static ItemType CombEmerald = new ItemType("comb_emerald", "").optional(Ref.MOD_FR);
    public static ItemType CombSlag = new ItemType("comb_slag", "").optional(Ref.MOD_FR);
    public static ItemType CombCopper = new ItemType("comb_copper", "").optional(Ref.MOD_FR);
    public static ItemType CombTin = new ItemType("comb_tin", "").optional(Ref.MOD_FR);
    public static ItemType CombLead = new ItemType("comb_lead", "").optional(Ref.MOD_FR);
    public static ItemType CombIron = new ItemType("comb_iron", "").optional(Ref.MOD_FR);
    public static ItemType CombSteel = new ItemType("comb_steel", "").optional(Ref.MOD_FR);
    public static ItemType CombNickel = new ItemType("comb_nickel", "").optional(Ref.MOD_FR);
    public static ItemType CombZinc = new ItemType("comb_zinc", "").optional(Ref.MOD_FR);
    public static ItemType CombSilver = new ItemType("comb_silver", "").optional(Ref.MOD_FR);
    public static ItemType CombGold = new ItemType("comb_gold", "").optional(Ref.MOD_FR);
    public static ItemType CombAluminium = new ItemType("comb_aluminium", "").optional(Ref.MOD_FR);
    public static ItemType CombManganese = new ItemType("comb_manganese", "").optional(Ref.MOD_FR);
    public static ItemType CombTitanium = new ItemType("comb_titanium", "").optional(Ref.MOD_FR);
    public static ItemType CombChrome = new ItemType("comb_chrome", "").optional(Ref.MOD_FR);
    public static ItemType CombTungsten = new ItemType("comb_tungsten", "").optional(Ref.MOD_FR);
    public static ItemType CombPlatinum = new ItemType("comb_platinum", "").optional(Ref.MOD_FR);
    public static ItemType CombIridium = new ItemType("comb_iridium", "").optional(Ref.MOD_FR);
    public static ItemType CombUranium = new ItemType("comb_uranium", "").optional(Ref.MOD_FR);
    public static ItemType CombPlutonium = new ItemType("comb_plutonium", "").optional(Ref.MOD_FR);
    public static ItemType CombNaquadah = new ItemType("comb_naquadah", "").optional(Ref.MOD_FR);

    private String name, tooltip;
    private boolean enabled = true;

    public ItemType(String name, String tooltip) {
        this.name = name;
        this.tooltip = tooltip;
        TYPE_LOOKUP.put(name, this);
    }

    public ItemType(String name) {
        this(name, "");
    }

    @Override
    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return GTLoc.get("item.standard." + getName() + ".name");
    }

    public String getTooltip() {
        return tooltip;
    }

    public boolean isEnabled() {
        return enabled || Ref.enableAllModItem;
    }

    public ItemType required(String... mods) {
        for (int i = 0; i < mods.length; i++) {
            if (!Loader.isModLoaded(mods[i])) {
                enabled = false;
                break;
            }
        }
        return this;
    }

    public ItemType optional(String... mods) {
        enabled = false;
        for (int i = 0; i < mods.length; i++) {
            if (Loader.isModLoaded(mods[i])) {
                enabled = true;
                break;
            }
        }
        return this;
    }

    public boolean isEqual(ItemStack stack) {
        return stack.getItem() instanceof StandardItem && ((StandardItem) stack.getItem()).getType() == this;
    }

    public static boolean doesShowExtendedHighlight(ItemStack stack) {
        return GregTechAPI.getCoverFromCatalyst(stack) != null;
    }

    public Item getNewInstance() {
        return new StandardItem(this);
    }

    public StandardItem getItem() {
        return GregTechRegistry.getStandardItem(this);
    }

    public ItemStack get(int count) {
        if (count == 0) return Utils.addNoConsumeTag(new ItemStack(getItem(), 1));
        return new ItemStack(getItem(), count);
    }

    public static ItemType get(String type) {
        return TYPE_LOOKUP.get(type);
    }

    public static Collection<ItemType> getAll() {
        return TYPE_LOOKUP.values();
    }
}
