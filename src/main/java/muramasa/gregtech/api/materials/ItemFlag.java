package muramasa.gregtech.api.materials;

import muramasa.gregtech.api.data.Materials;
import muramasa.gregtech.api.interfaces.IMaterialFlag;

import java.util.ArrayList;
import java.util.Locale;

public enum ItemFlag implements IMaterialFlag {

    //TODO rename to generation flag?

    ORE(),
    DUST(), //Add Dusts
    DUSTS(),
    DUSTT(),
    CRUSHED(),
    CRUSHEDC(),
    CRUSHEDP(),
    DUSTIP(),
    DUSTP(),
    INGOT(), //Add Ingots and Nuggets (Can't have Nuggets without Ingots)
    NUGGET(),
    BGEM(), //Add Basic Gem
    GEM(), //Add Gem quality variants
    LENS(),

    PLATE(), //Add Plates
    ROD(), //Add Rods
    RING(), //Add Rings
    BOLT(), //Add Bolts
    FOIL(), //Add Foils
    SCREW(), //Add Screws
    GEAR(), //Add Gears
    SGEAR(), //Add Small Gear
    WIREF(), //Add Fine Wire
    ROTOR(), //Add Rotors
    DPLATE(), //Add Dense Plates
    SPRING(), //Add Springs
    HINGOT(), //Hot Ingots
    BLOCK(), //Add Blocks
    FRAME(), //Add Frame Blocks
    LIQUID(), //Add Standard Fluid
    GAS(), //Add Gas Fluid
    PLASMA(), //Add Plasma Fluid
    TOOLS(); //Add Tool Parts

    private long bit;
    private ItemFlag[] subFlags;
    private ArrayList<Material> materials = new ArrayList<>();

    ItemFlag(ItemFlag... subFlags) {
        this.subFlags = subFlags;
        bit = 1 << ordinal();
    }

    @Override
    public String getName() {
        return name().toLowerCase(Locale.ENGLISH);
    }

    @Override
    public void add(Material... mats) {
        for (Material material : mats) {
            if (!materials.contains(material)) {
                materials.add(material);
            }
            if (material == Materials.Steel) {
                throw new NullPointerException();
            }
        }
    }

    @Override
    public long getBit() {
        return this.bit;
    }

    @Override
    public ArrayList<Material> getMats() {
        return materials;
    }
}
