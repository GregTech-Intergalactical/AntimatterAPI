package muramasa.gtu.api.materials;

import muramasa.gtu.Ref;
import muramasa.gtu.api.interfaces.IGregTechObject;
import muramasa.gtu.api.util.GTLoc;
import net.minecraft.item.ItemStack;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;

import static muramasa.gtu.api.materials.GenerationFlag.*;

public class Prefix implements IGregTechObject {

    private static LinkedHashMap<String, Prefix> PREFIX_LOOKUP = new LinkedHashMap<>();
    private static LinkedHashMap<String, ItemStack> ITEM_REPLACEMENT = new LinkedHashMap<>();

    public static Prefix Ore = new Prefix("ore", true, false, ORE);
    public static Prefix Block = new Prefix("block", true, false, BLOCK);

//    public static Prefix Chunk = new Prefix("chunk", true, ORE);
    public static Prefix Crushed = new Prefix("crushed", false, CRUSHED);
    public static Prefix CrushedPurified = new Prefix("crushed_purified", false, PURIFIED_CRUSHED);
    public static Prefix CrushedCentrifuged = new Prefix("crushed_centrifuged", false, CENTRIFUGED_CRUSHED);
    public static Prefix DustPure = new Prefix("dust_pure", false, PURE_DUST);
    public static Prefix DustImpure = new Prefix("dust_impure", false, IMPURE_DUST);
    public static Prefix Dust = new Prefix("dust", true, DUST);
    public static Prefix DustSmall = new Prefix("dust_small", false, SMALL_DUST);
    public static Prefix DustTiny = new Prefix("dust_tiny", false, TINY_DUST);
    public static Prefix Nugget = new Prefix("nugget", false, NUGGET);
    public static Prefix Ingot = new Prefix("ingot", true, INGOT);
    public static Prefix IngotHot = new Prefix("ingot_hot", false, HOT_INGOT);
    public static Prefix Plate = new Prefix("plate", true, PLATE);
    public static Prefix PlateDense = new Prefix("plate_dense", true, DENSE_PLATE);
    public static Prefix Gem = new Prefix("gem", true, BASIC_GEM);
    public static Prefix GemChipped = new Prefix("gem_chipped", true, GEM_VARIANTS);
    public static Prefix GemFlawed = new Prefix("gem_flawed", true, GEM_VARIANTS);
    public static Prefix GemFlawless = new Prefix("gem_flawless", true, GEM_VARIANTS);
    public static Prefix GemExquisite = new Prefix("gem_exquisite", true, GEM_VARIANTS);
    public static Prefix Foil = new Prefix("foil", true, FOIL);
    public static Prefix Rod = new Prefix("rod", true, ROD);
    //public static Prefix RodLong = new Prefix("rod_long", true, LONG_ROD);
    //public static Prefix Bolt = new Prefix("bolt", true, BOLT);
    public static Prefix Screw = new Prefix("screw", true, SCREW);
    public static Prefix Ring = new Prefix("ring", true, RING);
    //public static Prefix Spring = new Prefix("spring", true, SPRING);
    public static Prefix WireFine = new Prefix("wire_fine", true, FINE_WIRE);
    public static Prefix TurbineRotor = new Prefix("rotor", true, TURBINE_ROTOR);
    public static Prefix Gear = new Prefix("gear", true, GEAR);
    //public static Prefix GearSmall = new Prefix("gear_small", true, SGEAR);
    public static Prefix Lens = new Prefix("lens", true, LENS);
//    public static Prefix TurbineBlade = new Prefix("turbine_blade", true, TOOLS);

    private String name, namePre, namePost;

    private boolean doesGenerate, hasLocName, visible;
    private long generationBits;

    public Prefix(String name, boolean visible, GenerationFlag flag) {
        this.name = name;
        this.visible = visible;
//        for (GenerationFlag flag : flags) {
//            generationBits |= flag.getBit();
//        }
        generationBits |= flag.getBit();
        this.doesGenerate = true;
        PREFIX_LOOKUP.put(name, this);
    }

    public Prefix(String name, boolean visible, boolean generatesItems, GenerationFlag flag) {
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
