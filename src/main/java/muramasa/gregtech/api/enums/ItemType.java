package muramasa.gregtech.api.enums;

import muramasa.gregtech.api.GregTechAPI;
import muramasa.gregtech.api.cover.behaviour.*;
import muramasa.gregtech.api.items.StandardItem;
import muramasa.gregtech.api.materials.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.text.TextFormatting;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;

public class ItemType implements IStringSerializable {

    private static LinkedHashMap<String, ItemType> TYPE_LOOKUP = new LinkedHashMap<>();

    public static ItemType EmptyCell = new ItemType("Empty Cell");
    public static ItemType DebugScanner = new ItemType("Debug Scanner",TextFormatting.AQUA + "" + TextFormatting.ITALIC + "Development Item");
    public static ItemType PumpComponent = new ItemType("Pump Component", "");
    public static ItemType ConveyorComponent = new ItemType("Conveyor Component", "");
    public static ItemType ItemPort = new ItemType("Item Port", "Can be placed on machines as a cover");
    public static ItemType FluidPort = new ItemType("Fluid Port", "Can be placed on machines as a cover");
    public static ItemType EnergyPort = new ItemType("Energy Port", "Can be placed on machines as a cover");

//    Empty_Shape("Empty Shape Plate", "Raw plate to make Molds and Extruder Shapes"),
//    Mold_Plate("Mold (Plate)", "Mold for making Plates"),
//    Mold_Gear("Mold (Gear)", "Mold for making Gears"),
//    Mold_Gear_Small("Mold (Small Gear)", "Mold for making Small Gears"),
//    Mold_Coinage("Mold (Coinage)", "Secure Mold for making Coins (Don't lose it!)"),
//    Mold_Bottle("Mold (Bottle)", "Mold for making Bottles"),
//    Mold_Ingot("Mold (Ingot)", "Mold for making Ingots"),
//    Mold_Ball("Mold (Ball)", "Mold for making Balls"),
//    Mold_Block("Mold (Block)", "Mold for making Blocks"),
//    Mold_Nugget("Mold (Nugget)", "Mold for making Nuggets"),
//    Mold_Anvil("Mold (Anvil)", "Mold for making Anvils"),
//    Shape_Plate("Extruder Shape (Plate)", "Shape for making Plates"),
//    Shape_Rod("Extruder Shape (Rod)", "Shape for making Rods"),
//    Shape_Bolt("Extruder Shape (Bolt)", "Shape for making Bolts"),
//    Shape_Ring("Extruder Shape (Ring)", "Shape for making Rings"),
//    Shape_Cell("Extruder Shape (Cell)", "Shape for making Cells"),
//    Shape_Ingot("Extruder Shape (Ingot)", "Shape for making Ingots"),
//    Shape_Wire("Extruder Shape (Wire)", "Shape for making Wires"),
//    Shape_Pipe_Tiny("Extruder Shape (Tiny Pipe)", "Shape for making Tiny Pipes"),
//    Shape_Pipe_Small("Extruder Shape (Small Pipe)", "Shape for making Small Pipes"),
//    Shape_Pipe_Normal("Extruder Shape (Normal Pipe)", "Shape for making Normal Pipes"),
//    Shape_Pipe_Large("Extruder Shape (Large Pipe)", "Shape for making Large Pipes"),
//    Shape_Pipe_Huge("Extruder Shape (Huge Pipe)", "Shape for making Huge Pipes"),
//    Shape_Block("Extruder Shape (Block)", "Shape for making Blocks"),
//    Shape_Head_Sword("Extruder Shape (Sword Blade)", "Shape for making Sword Blades"),
//    Shape_Head_Pickaxe("Extruder Shape (Pickaxe Head)", "Shape for making Pickaxe Heads"),
//    Shape_Head_Shovel("Extruder Shape (Shovel Head)", "Shape for making Shovel Heads"),
//    Shape_Head_Axe("Extruder Shape (Axe Head)", "Shape for making Axe Heads"),
//    Shape_Head_Hoe("Extruder Shape (Hoe Head)", "Shape for making Hoe Heads"),
//    Shape_Head_Hammer("Extruder Shape (Hammer Head)", "Shape for making Hammer Heads"),
//    Shape_Head_File("Extruder Shape (File Head)", "Shape for making File Heads"),
//    Shape_Head_Saw("Extruder Shape (Saw Blade)", "Shape for making Saw Heads"),
//    Shape_Gear("Extruder Shape (Gear)", "Shape for making Gears"),
//    Shape_Gear_Small("Extruder Shape (Small Gear)", "Shape for making Small Gears"),
//    Shape_Bottle("Extruder Shape (Bottle)", "Shape for making Bottles"); //TODO needed?

    public static void init() {
        GregTechAPI.CoverBehaviourNone = new CoverBehaviourNone();
        //TODO avoid creating "dummy" instance due to requiring name string
        GregTechAPI.CoverBehaviourPlate = new CoverBehaviourPlate(-1);
        for (Material mat : GenerationFlag.PLATE.getMats()) {
            GregTechAPI.registerCover(mat.getPlate(1), new CoverBehaviourPlate(mat.getRGB()));
        }
        GregTechAPI.CoverBehaviourItem = new CoverBehaviourItem();
        GregTechAPI.CoverBehaviourFluid = new CoverBehaviourFluid();
        GregTechAPI.CoverBehaviourEnergy = new CoverBehaviourEnergy();


        GregTechAPI.registerCover(ItemPort.get(1), GregTechAPI.CoverBehaviourItem);
        GregTechAPI.registerCover(FluidPort.get(1), GregTechAPI.CoverBehaviourFluid);
        GregTechAPI.registerCover(EnergyPort.get(1), GregTechAPI.CoverBehaviourEnergy);
    }

    private String name, displayName, tooltip;

    public ItemType(String displayName, String tooltip) {
        this.name = displayName.toLowerCase(Locale.ENGLISH).replace(" ", "_");
        this.displayName = displayName;
        this.tooltip = tooltip;
        TYPE_LOOKUP.put(name, this);
    }

    public ItemType(String displayName) {
        this(displayName, "");
    }

    @Override
    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getTooltip() {
        return tooltip;
    }

    public boolean isEqual(ItemStack stack) {
        return stack.getItem() instanceof StandardItem && ((StandardItem) stack.getItem()).getType() == this;
    }

    public static boolean isAnyItemEqual(ItemStack stack, ItemType... items) {
        for (ItemType item : items) {
            if (item.isEqual(stack)) return true;
        }
        return false;
    }

    public static boolean doesShowExtendedHighlight(ItemStack stack) {
        return isAnyItemEqual(stack, ItemPort, FluidPort, EnergyPort);
    }

    public ItemStack get(int count) { //TODO implement 0 size = no recipe consume
        return StandardItem.get(name, count);
    }

    public static ItemType get(String type) {
        return TYPE_LOOKUP.get(type);
    }

    public static Collection<ItemType> getAll() {
        return TYPE_LOOKUP.values();
    }
}
