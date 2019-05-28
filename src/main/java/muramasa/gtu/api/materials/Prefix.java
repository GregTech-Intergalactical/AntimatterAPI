package muramasa.gtu.api.materials;

import muramasa.gtu.Ref;
import muramasa.gtu.api.interfaces.IGregTechObject;
import muramasa.gtu.api.util.GTLoc;
import net.minecraft.item.ItemStack;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;

import static muramasa.gtu.api.materials.ItemFlag.*;

public class Prefix implements IGregTechObject {

    private static LinkedHashMap<String, Prefix> PREFIX_LOOKUP = new LinkedHashMap<>();
    private static LinkedHashMap<String, ItemStack> ITEM_REPLACEMENT = new LinkedHashMap<>();

    public static Prefix Ore = new Prefix("ore", true, false, GENERATE_ORE);
    public static Prefix Block = new Prefix("block", true, false, GENERATE_BLOCK);

//    public static Prefix Chunk = new Prefix("chunk", true, ORE);
    public static Prefix Crushed = new Prefix("crushed", false, GENERATE_CRUSHED);
    public static Prefix CrushedPurified = new Prefix("crushed_purified", false, GENERATE_PURIFIED_CRUSHED);
    public static Prefix CrushedCentrifuged = new Prefix("crushed_centrifuged", false, GENERATE_CENTRIFUGED_CRUSHED);
    public static Prefix DustPure = new Prefix("dust_pure", false, GENERATE_PURE_DUST);
    public static Prefix DustImpure = new Prefix("dust_impure", false, GENERATE_IMPURE_DUST);
    public static Prefix Dust = new Prefix("dust", true, GENERATE_DUST);
    public static Prefix DustSmall = new Prefix("dust_small", false, GENERATE_SMALL_DUST);
    public static Prefix DustTiny = new Prefix("dust_tiny", false, GENERATE_TINY_DUST);
    public static Prefix Nugget = new Prefix("nugget", false, GENERATE_NUGGET);
    public static Prefix Ingot = new Prefix("ingot", true, GENERATE_INGOT);
    public static Prefix IngotHot = new Prefix("ingot_hot", false, GENERATE_HOT_INGOT);
    public static Prefix Plate = new Prefix("plate", true, GENERATE_PLATE);
    public static Prefix PlateDense = new Prefix("plate_dense", true, GENERATE_DENSE_PLATE);
    public static Prefix Gem = new Prefix("gem", true, GENERATE_BASIC_GEM);
    public static Prefix GemChipped = new Prefix("gem_chipped", true, GENERATE_GEM_VARIANTS);
    public static Prefix GemFlawed = new Prefix("gem_flawed", true, GENERATE_GEM_VARIANTS);
    public static Prefix GemFlawless = new Prefix("gem_flawless", true, GENERATE_GEM_VARIANTS);
    public static Prefix GemExquisite = new Prefix("gem_exquisite", true, GENERATE_GEM_VARIANTS);
    public static Prefix Foil = new Prefix("foil", true, GENERATE_FOIL);
    public static Prefix Rod = new Prefix("rod", true, GENERATE_ROD);
    //public static Prefix RodLong = new Prefix("rod_long", true, GENERATE_LONG_ROD);
    //public static Prefix Bolt = new Prefix("bolt", true, BOLT);
    public static Prefix Screw = new Prefix("screw", true, GENERATE_SCREW);
    public static Prefix Ring = new Prefix("ring", true, GENERATE_RING);
    //public static Prefix Spring = new Prefix("spring", true, SPRING);
    public static Prefix WireFine = new Prefix("wire_fine", true, GENERATE_FINE_WIRE);
    public static Prefix TurbineRotor = new Prefix("rotor", true, GENERATE_TURBINE_ROTOR);
    public static Prefix Gear = new Prefix("gear", true, GENERATE_GEAR);
    //public static Prefix GearSmall = new Prefix("gear_small", true, SGEAR);
    public static Prefix Lens = new Prefix("lens", true, GENERATE_LENS);
    public static Prefix Cell = new Prefix("cell", true, GENERATE_LIQUID);
    public static Prefix CellGas = new Prefix("cell_gas", true, GENERATE_GAS);
    public static Prefix CellPlasma = new Prefix("cell_plasma", true, GENERATE_PLASMA);
//    public static Prefix TurbineBlade = new Prefix("turbine_blade", true, TOOLS);

    private String name, namePre, namePost;

    private boolean doesGenerate, hasLocName, visible;
    private long generationBits;

    public Prefix(String name, boolean visible, ItemFlag flag) {
        this.name = name;
        this.visible = visible;
//        for (ItemFlag flag : flags) {
//            generationBits |= flag.getBit();
//        }
        generationBits |= flag.getBit();
        this.doesGenerate = true;
        PREFIX_LOOKUP.put(name, this);
    }

    public Prefix(String name, boolean visible, boolean generatesItems, ItemFlag flag) {
        this(name, visible, flag);
        this.doesGenerate = generatesItems;
    }

    @Override
    public String getName() {
        return name.toLowerCase(Locale.ENGLISH);
    }

    public String getDisplayName(Material material) { //TODO cache, server side crash with local?
        if (!hasLocName) {
            namePre = GTLoc.get("prefix.pre." + getName() + ".name");
            namePre = namePre.equals("") ? "" : namePre + " ";
            namePost = GTLoc.get("prefix.post." + getName() + ".name");
            namePost = namePost.equals("") ? "" : " " + namePost;
            hasLocName = true;
        }
        return namePre + material.getDisplayName() + namePost;
    }

    public boolean isVisible() {
        return visible || Ref.showAllItems;
    }

    public boolean allowGeneration(Material material) {
        return doesGenerate && (material.getItemMask() & generationBits) != 0 && !ITEM_REPLACEMENT.containsKey(getName() + material.getName());
    }

    public void addReplacement(Material material, ItemStack stack) {
        ITEM_REPLACEMENT.put(getName() + material.getName(), stack);
    }

    public ItemStack getReplacement(Material material) {
        return ITEM_REPLACEMENT.get(getName() + material.getName());
    }

    @Override
    public String toString() {
        return getName();
    }

    public static Prefix get(String name) {
        return PREFIX_LOOKUP.get(name);
    }

    public static Collection<Prefix> getAll() {
        return PREFIX_LOOKUP.values();
    }
}
