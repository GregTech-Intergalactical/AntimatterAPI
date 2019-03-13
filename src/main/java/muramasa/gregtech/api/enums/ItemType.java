package muramasa.gregtech.api.enums;

import muramasa.gregtech.api.GregTechAPI;
import muramasa.gregtech.api.cover.impl.*;
import muramasa.gregtech.api.items.StandardItem;
import muramasa.gregtech.api.materials.ItemFlag;
import muramasa.gregtech.api.materials.Material;
import muramasa.gregtech.api.util.Utils;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.text.TextFormatting;

import java.util.Collection;
import java.util.LinkedHashMap;

public class ItemType implements IStringSerializable {

    private static LinkedHashMap<String, ItemType> TYPE_LOOKUP = new LinkedHashMap<>();

    public static ItemType EmptyCell = new ItemType("empty_cell");
    public static ItemType DebugScanner = new ItemType("debug_scanner", TextFormatting.AQUA + "" + TextFormatting.ITALIC + "Development Item");
    public static ItemType PumpComponent = new ItemType("component_pump", "");
    public static ItemType ConveyorComponent = new ItemType("component_conveyor", "");
    public static ItemType ItemPort = new ItemType("item_port", "Can be placed on machines as a cover");
    public static ItemType FluidPort = new ItemType("fluid_port", "Can be placed on machines as a cover");
    public static ItemType EnergyPort = new ItemType("energy_port", "Can be placed on machines as a cover");

    public static ItemType EmptyShape = new ItemType("empty_shape_plate", "Raw plate to make Molds and Extruder Shapes");
    public static ItemType MoldPlate = new ItemType("mold_plate", "Mold for making Plates");
    public static ItemType MoldGear = new ItemType("mold_gear", "Mold for making Gears");
    public static ItemType MoldGearSmall = new ItemType("mold_small_gear", "Mold for making Small Gears");
    public static ItemType MoldCoinage = new ItemType("mold_coinage", "Secure Mold for making Coins (Don't lose it!)");
    public static ItemType MoldBottle = new ItemType("mold_bottle", "Mold for making Bottles");
    public static ItemType MoldIngot = new ItemType("mold_ingot", "Mold for making Ingots");
    public static ItemType MoldBall = new ItemType("mold_ball", "Mold for making Balls");
    public static ItemType MoldBlock = new ItemType("bold_block", "Mold for making Blocks");
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
    public static ItemType ShapeGear = new ItemType("shape_head_gear", "Shape for making Gears");
    public static ItemType ShapeGearSmall = new ItemType("shape_head_gear_small", "Shape for making Small Gears");
    public static ItemType ShapeBottle = new ItemType("shape_bottle", "Shape for making Bottles"); //TODO needed?

    public static void init() {
        GregTechAPI.registerCover(GregTechAPI.CoverNone = new CoverNone());
        GregTechAPI.registerCover(GregTechAPI.CoverItem = new CoverItem());
        GregTechAPI.registerCover(GregTechAPI.CoverFluid = new CoverFluid());
        GregTechAPI.registerCover(GregTechAPI.CoverEnergy = new CoverEnergy());
        GregTechAPI.registerCover(GregTechAPI.CoverPlate = new CoverPlate());

        GregTechAPI.registerCoverCatalyst(ItemPort.get(1), GregTechAPI.CoverItem);
        GregTechAPI.registerCoverCatalyst(FluidPort.get(1), GregTechAPI.CoverFluid);
        GregTechAPI.registerCoverCatalyst(EnergyPort.get(1), GregTechAPI.CoverEnergy);

        for (Material mat : ItemFlag.PLATE.getMats()) {
            GregTechAPI.registerCoverCatalyst(mat.getPlate(1), GregTechAPI.CoverPlate);
        }
    }

    private String name, tooltip;

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
        return I18n.format("item.standard." + getName() + ".name");
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
        if (count == 0) return Utils.addNoConsumeTag(StandardItem.get(name, 1));
        return StandardItem.get(name, count);
    }

    public static ItemType get(String type) {
        return TYPE_LOOKUP.get(type);
    }

    public static Collection<ItemType> getAll() {
        return TYPE_LOOKUP.values();
    }
}
