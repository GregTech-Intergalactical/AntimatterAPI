package muramasa.itech.api.enums;

import muramasa.itech.api.items.MetaItem;
import muramasa.itech.loaders.ContentLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public enum ItemList {

    DebugScanner("Debug Scanner", TextFormatting.AQUA + "" + TextFormatting.ITALIC + "Development Item"),
    ComponentPump("Pump Component", ""),
    ComponentConveyor("Conveyor Component", ""),
    CoverItemPort("Item Port", "Can be placed on machines as a cover"),
    CoverFluidPort("Fluid Port", "Can be placed on machines as a cover"),
    CoverEnergyPort("Energy Port", "Can be placed on machines as a cover"),

    EmptyShape("Empty Shape Plate", "Raw plate to make Molds and Extruder Shapes"),
    MoldPlate("Mold (Plate)", "Mold for making Plates"),
    MoldGear("Mold (Gear)", "Mold for making Gears"),
    MoldGearSmall("Mold (Small Gear)", "Mold for making Small Gears"),
    MoldCoinage("Mold (Coinage)", "Secure Mold for making Coins (Don't lose it!)"),
    MoldBottle("Mold (Bottle)", "Mold for making Bottles"),
    MoldIngot("Mold (Ingot)", "Mold for making Ingots"),
    MoldBall("Mold (Ball)", "Mold for making Balls"),
    MoldBlock("Mold (Block)", "Mold for making Blocks"),
    MoldNugget("Mold (Nugget)", "Mold for making Nuggets"),
    MoldAnvil("Mold (Anvil)", "Mold for making Anvils"),
    ShapePlate("Extruder Shape (Plate)", "Shape for making Plates"),
    ShapeRod("Extruder Shape (Rod)", "Shape for making Rods"),
    ShapeBolt("Extruder Shape (Bolt)", "Shape for making Bolts"),
    ShapeRing("Extruder Shape (Ring)", "Shape for making Rings"),
    ShapeCell("Extruder Shape (Cell)", "Shape for making Cells"),
    ShapeIngot("Extruder Shape (Ingot)", "Shape for making Ingots"),
    ShapeWire("Extruder Shape (Wire)", "Shape for making Wires"),
    ShapePipeTiny("Extruder Shape (Tiny Pipe)", "Shape for making Tiny Pipes"),
    ShapePipeSmall("Extruder Shape (Small Pipe)", "Shape for making Small Pipes"),
    ShapePipeNormal("Extruder Shape (Normal Pipe)", "Shape for making Normal Pipes"),
    ShapePipeLarge("Extruder Shape (Large Pipe)", "Shape for making Large Pipes"),
    ShapePipeHuge("Extruder Shape (Huge Pipe)", "Shape for making Huge Pipes"),
    ShapeBlock("Extruder Shape (Block)", "Shape for making Blocks"),
    ShapeHeadSword("Extruder Shape (Sword Blade)", "Shape for making Sword Blades"),
    ShapeHeadSPickaxe("Extruder Shape (Pickaxe Head)", "Shape for making Pickaxe Heads"),
    ShapeHeadShovel("Extruder Shape (Shovel Head)", "Shape for making Shovel Heads"),
    ShapeHeadAxe("Extruder Shape (Axe Head)", "Shape for making Axe Heads"),
    ShapeHeadHoe("Extruder Shape (Hoe Head)", "Shape for making Hoe Heads"),
    ShapeHeadHammer("Extruder Shape (Hammer Head)", "Shape for making Hammer Heads"),
    ShapeHeadFile("Extruder Shape (File Head)", "Shape for making File Heads"),
    ShapeHeadSaw("Extruder Shape (Saw Blade)", "Shape for making Saw Heads"),
    ShapeGear("Extruder Shape (Gear)", "Shape for making Gears"),
    ShapeGearSmall("Extruder Shape (Small Gear)", "Shape for making Small Gears"),
    ShapeBottle("Extruder Shape (Bottle)", "Shape for making Bottles"); //TODO needed?


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
        return stack.getItem() instanceof MetaItem && stack.getMetadata() == MetaItem.standardItemStartIndex + ordinal();
    }

    public ItemStack get(int size) { //TODO implement 0 size = no recipe consume
        return new ItemStack(ContentLoader.metaItem, size, MetaItem.standardItemStartIndex + ordinal());
    }

    public static ItemList get(ItemStack stack) {
        return ItemList.values()[stack.getMetadata() - MetaItem.standardItemStartIndex];
    }

    public static boolean isAnyItemEqual(ItemStack stack, ItemList... items) {
        for (ItemList item : items) {
            if (item.isItemEqual(stack)) return true;
        }
        return false;
    }

    public static boolean doesShowExtendedHighlight(ItemStack stack) {
        return isAnyItemEqual(stack, CoverItemPort, CoverFluidPort, CoverEnergyPort);
    }
}
