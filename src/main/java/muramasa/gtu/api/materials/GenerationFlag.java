package muramasa.gtu.api.materials;

import java.util.ArrayList;
import java.util.Locale;

public enum GenerationFlag implements IMaterialFlag {
	
    ORE(), //Add Ores (TODO: dimensional ores, stone types, need separate prefix?)
    DUST(), //Add Dusts
    SMALL_DUST(), //Add Small Dusts
    TINY_DUST(), //Add Tiny Dusts
    IMPURE_DUST(), //Add Impure Dusts
    PURE_DUST(), //Add Pure Dusts
    CRUSHED(), //Add Crushed Ores
    CENTRIFUGED_CRUSHED(), //Add Centrifuged Crushed Ores
    PURIFIED_CRUSHED(), //Add Purified Crushed Ores
    INGOT(), //Add Ingots (and nuggets)
    NUGGET(), //And Nuggets, rarely implemented by itself
    BASIC_GEM(), //Add Basic Gem
    GEM_VARIANTS(), //Add Gem quality variants
    LENS(), //Add Lens (anything that's transparent gem)

    PLATE(), //Add Plates
    DENSE_PLATE(),
    ROD(), //Add Rods
    LONG_ROD(), //Add Long Rods
    RING(), //Add Rings
    FOIL(), //Add Foils
    BOLT(), //Add Bolts
    SCREW(), //Add Screws
    SMALL_GEAR(),
    GEAR(), //Add Gears
    FINE_WIRE(), //Add Fine Wire
    TURBINE_ROTOR(), //Add Turbine Rotors
    SPRING(), //Add Springs
    HOT_INGOT(), //Hot Ingots
    BLOCK(), //Add Blocks
    FRAME(), //Add Frame Blocks
    
    LIQUID(), //Add Standard Fluid
    GAS(), //Add Gas Fluid
    PLASMA(), //Add Plasma Fluid
    TOOLS(); //Add Tool Parts

    private long bit;
    private GenerationFlag[] subFlags;
    private ArrayList<Material> materials = new ArrayList<>();

    GenerationFlag(GenerationFlag... subFlags) {
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
