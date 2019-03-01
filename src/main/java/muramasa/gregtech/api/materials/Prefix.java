package muramasa.gregtech.api.materials;

import muramasa.gregtech.common.utils.Ref;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;

import static muramasa.gregtech.api.materials.ItemFlag.*;

public class Prefix implements IStringSerializable {

    private static LinkedHashMap<String, Prefix> PREFIX_LOOKUP = new LinkedHashMap<>();
    private static LinkedHashMap<String, ItemStack> ITEM_REPLACEMENT = new LinkedHashMap<>();

    public static Prefix Ore = new Prefix("ore", true, false, ORE);
    public static Prefix Block = new Prefix("block", true, false, BLOCK);

//    public static Prefix Chunk = new Prefix("chunk", true, ORE);
    public static Prefix Crushed = new Prefix("crushed", false, CRUSHED);
    public static Prefix CrushedPurified = new Prefix("crushed_purified", false, CRUSHEDP);
    public static Prefix CrushedCentrifuged = new Prefix("crushed_centrifuged", false, CRUSHEDC, CRUSHEDP);
    public static Prefix DustPure = new Prefix("dust_pure", false, DUSTP);
    public static Prefix DustImpure = new Prefix("dust_impure", false, DUSTIP);
    public static Prefix Dust = new Prefix("dust", true, DUST);
    public static Prefix DustSmall = new Prefix("dust_small", false, DUSTS);
    public static Prefix DustTiny = new Prefix("dust_tiny", false, DUSTT);
    public static Prefix Nugget = new Prefix("nugget", false, NUGGET);
    public static Prefix Ingot = new Prefix("ingot", true, INGOT);
    public static Prefix IngotHot = new Prefix("ingot_hot", false, HINGOT);
    public static Prefix Plate = new Prefix("plate", true, PLATE);
    public static Prefix PlateDense = new Prefix("plate_dense", true, DPLATE);
    public static Prefix Gem = new Prefix("gem", true, BGEM);
    public static Prefix GemChipped = new Prefix("gem_chipped", true, GEM);
    public static Prefix GemFlawed = new Prefix("gem_flawed", true, GEM);
    public static Prefix GemFlawless = new Prefix("gem_flawless", true, GEM);
    public static Prefix GemExquisite = new Prefix("gem_exquisite", true, GEM);
    public static Prefix Foil = new Prefix("foil", true, FOIL);
    public static Prefix Rod = new Prefix("rod", true, ROD);
    public static Prefix Bolt = new Prefix("bolt", true, BOLT);
    public static Prefix Screw = new Prefix("screw", true, SCREW);
    public static Prefix Ring = new Prefix("ring", true, RING);
    public static Prefix Spring = new Prefix("spring", true, SPRING);
    public static Prefix WireFine = new Prefix("wire_fine", true, WIREF);
    public static Prefix Rotor = new Prefix("rotor", true, ROTOR);
    public static Prefix Gear = new Prefix("gear", true, GEAR);
    public static Prefix GearSmall = new Prefix("gear_small", true, SGEAR);
    public static Prefix Lens = new Prefix("lens", true, LENS);
    public static Prefix Cell = new Prefix("cell", true, LIQUID);
    public static Prefix CellGas = new Prefix("cell_gas", true, GAS);
    public static Prefix CellPlasma = new Prefix("cell_plasma", true, PLASMA);

    private String name, namePre, namePost;

    private boolean doesGenerate, hasLocName, visible;
    private long generationBits;

    public Prefix(String name, boolean visible, ItemFlag... flags) {
        this.name = name;
        this.visible = visible;
        for (ItemFlag flag : flags) {
            generationBits |= flag.getBit();
        }
        this.doesGenerate = true;
        PREFIX_LOOKUP.put(name, this);
    }

    public Prefix(String name, boolean visible, boolean generatesItems, ItemFlag... flags) {
        this(name, visible, flags);
        this.doesGenerate = generatesItems;
    }

    public String getName() {
        return name.toLowerCase(Locale.ENGLISH);
    }

    public String getDisplayName(Material material) { //TODO cache, server side crash with local?
        if (!hasLocName) {
            namePre = I18n.format("prefix.pre." + getName() + ".name");
            namePre = namePre.equals("") ? "" : namePre + " ";
            namePost = I18n.format("prefix.post." + getName() + ".name");
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

    public void addItemReplacement(Material material, ItemStack stack) {
        ITEM_REPLACEMENT.put(getName() + material.getName(), stack);
    }

    public ItemStack getItemReplacement(Material material) {
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
