package muramasa.itech.api.enums;

import muramasa.itech.api.interfaces.IMaterialFlag;
import muramasa.itech.api.materials.Materials;

import java.util.ArrayList;

public enum ItemFlag implements IMaterialFlag {

    DUST(), //Add Dusts
//    ORE(), //Add Ore Items
//    DUSTI(), //TODO deprecate
//    DUSTP(), //TODO deprecate
    CRUSHED(),
    CRUSHEDC(),
    CRUSHEDP(),
    INGOT(), //Add Ingots
    //NUGGET(), //Add Nuggets //TODO remove? cant have nuggets without ingots
    BGEM(), //Add Basic Gems
    GEM(), //Add Gem quality variants
    //    CELL(), //Add Fluid Cells //TODO pointless with fluidStack flag?
    FLUID(), //Add Standard Fluid
    PLASMA(), //Add Plasma Fluid
    TOOL(), //Add Tool Parts
    PLATE(), //Add Plates
    ROD(), //Add Rods
    RING(), //Add Rings
    BOLT(), //Add Bolts
    FOIL(), //Add Foils
    SCREW(), //Add Screws
    GEAR(), //Add Gears
    SGEAR(), //Add Small Gear
    WIREF(), //Add Fine Wire
    ROTOR(), //Add Rotors //TODO deprecate? only 1 rotor
    DPLATE(), //Add Dense Plates
    SPRING(), //Add Springs
    HINGOT(), //Hot Ingots
    GAS(), //Add Gas Fluid
    BLOCK(), //Add Blocks
    FRAME(); //Add Frame Blocks

    public static int totalEntries;

    private int bit;
    private ArrayList<Materials> materialsList;
    private Materials[] materials;

    ItemFlag() {
        bit = 1 << ordinal();
        materialsList = new ArrayList<>();
    }

    public static void finish() {
        for (ItemFlag flag : ItemFlag.values()) {
            flag.materials = flag.materialsList.toArray(new Materials[0]);
            flag.materialsList = null;
            totalEntries += flag.materials.length;
        }
    }

    public void add(Materials... mats) {
        for (Materials material : mats) {
            materialsList.add(material);
        }
    }

    public int getMask() {
        return this.bit;
    }

    public Materials[] getMats() {
        return materials;
    }
}
