package muramasa.gtu.api.materials;

import muramasa.gtu.api.interfaces.IMaterialFlag;

import java.util.ArrayList;
import java.util.Locale;

public enum ItemFlag implements IMaterialFlag {

    //TODO rename to generation flag?

    GENERATE_ORE(),
    GENERATE_DUST(), //Add Dusts
    GENERATE_SMALL_DUST(),
    GENERATE_TINY_DUST(),
    GENERATE_IMPURE_DUST(),
    GENERATE_PURE_DUST(),
    GENERATE_CRUSHED(),
    GENERATE_CENTRIFUGED_CRUSHED(),
    GENERATE_PURIFIED_CRUSHED(),   
    GENERATE_INGOT(), //Add Ingots and Nuggets (Can't have Nuggets without Ingots)
    GENERATE_NUGGET(),
    GENERATE_BASIC_GEM(), //Add Basic Gem
    GENERATE_GEM_VARIANTS(), //Add Gem quality variants
    GENERATE_LENS(),

    GENERATE_PLATE(), //Add Plates
    GENERATE_DENSE_PLATE(),
    GENERATE_ROD(), //Add Rods
    GENERATE_LONG_ROD(), //Add Long Rods
    GENERATE_RING(), //Add Rings
    GENERATE_FOIL(), //Add Foils
    GENERATE_SCREW(), //Add Screws
    GENERATE_GEAR(), //Add Gears
    GENERATE_FINE_WIRE(), //Add Fine Wire
    GENERATE_TURBINE_ROTOR(), //Add Rotors
    GENERATE_SPRING(), //Add Springs
    GENERATE_HOT_INGOT(), //Hot Ingots
    GENERATE_BLOCK(), //Add Blocks
    GENERATE_FRAME(), //Add Frame Blocks
    
    GENERATE_LIQUID(), //Add Standard Fluid
    GENERATE_GAS(), //Add Gas Fluid
    GENERATE_PLASMA(), //Add Plasma Fluid
    GENERATE_TOOLS(); //Add Tool Parts

    private long bit;
    private ItemFlag[] subFlags;
    private ArrayList<Material> materials = new ArrayList<>();

    ItemFlag(ItemFlag... subFlags) {
        this.subFlags = subFlags;
        bit = 1L << ordinal();
    }

    @Override
    public String getName() {
        return name().toLowerCase(Locale.ENGLISH);
    }

    @Override
    public void add(Material... mats) {
        for (Material material : mats) {
            if (!materials.contains(material)) materials.add(material);
        }
    }

    @Override
    public void remove(Material... mats) {
        for (Material material : mats) {
            if (materials.remove(material));
        }
    }

    @Override
    public long getBit() {
        return bit;
    }

    @Override
    public ArrayList<Material> getMats() {
        return materials;
    }
}
