package muramasa.gregtech.api.enums;

import muramasa.gregtech.api.items.MetaItem;
import muramasa.gregtech.loaders.ContentLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public enum ItemList {

    Debug_Scanner("Debug Scanner", TextFormatting.AQUA + "" + TextFormatting.ITALIC + "Development Item"),
    Component_Pump("Pump Component", ""),
    Component_Conveyor("Conveyor Component", ""),
    Cover_Item_Port("Item Port", "Can be placed on machines as a cover"),
    Cover_Fluid_Port("Fluid Port", "Can be placed on machines as a cover"),
    Cover_Energy_Port("Energy Port", "Can be placed on machines as a cover");

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


    private String displayName;
    private String tooltip;

    ItemList(String displayName, String tooltip) {
        this.displayName = displayName;
        this.tooltip = tooltip;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getTooltip() {
        return tooltip;
    }

    public boolean isItemEqual(ItemStack stack) {
        return stack.getItem() instanceof MetaItem && stack.getMetadata() == ordinal();
    }

    public ItemStack get(int size) { //TODO implement 0 size = no recipe consume
        return new ItemStack(ContentLoader.metaItem, size, ordinal());
    }

    public static ItemList get(ItemStack stack) {
        return ItemList.values()[stack.getMetadata()];
    }

    public static boolean isAnyItemEqual(ItemStack stack, ItemList... items) {
        for (ItemList item : items) {
            if (item.isItemEqual(stack)) return true;
        }
        return false;
    }

    public static boolean doesShowExtendedHighlight(ItemStack stack) {
        return isAnyItemEqual(stack, Cover_Item_Port, Cover_Fluid_Port, Cover_Energy_Port);
    }
}
