package muramasa.gregtech.api.materials;

import muramasa.gregtech.api.enums.GenerationFlag;
import muramasa.gregtech.common.utils.Ref;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IStringSerializable;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;

public class Prefix implements IStringSerializable {

    private static LinkedHashMap<String, Prefix> PREFIX_LOOKUP = new LinkedHashMap<>();

    public static Prefix Ore = new Prefix("ore", true, false, GenerationFlag.CRUSHED);
    public static Prefix Block = new Prefix("block", true, false, GenerationFlag.CRUSHED);

    public static Prefix Chunk = new Prefix("chunk", true, GenerationFlag.CRUSHED);
    public static Prefix Crushed = new Prefix("crushed", false, GenerationFlag.CRUSHED);
    public static Prefix CrushedCentrifuged = new Prefix("crushed_centrifuged", false, GenerationFlag.CRUSHEDC);
    public static Prefix CrushedPurified = new Prefix("crushed_purified", false, GenerationFlag.CRUSHEDP);
    public static Prefix Dust = new Prefix("dust", true, GenerationFlag.DUST);
    public static Prefix DustSmall = new Prefix("dust_small", false, GenerationFlag.DUST);
    public static Prefix DustTiny = new Prefix("dust_tiny", false, GenerationFlag.DUST);
    public static Prefix Nugget = new Prefix("nugget", false, GenerationFlag.INGOT);
    public static Prefix Ingot = new Prefix("ingot", true, GenerationFlag.INGOT);
    public static Prefix IngotHot = new Prefix("ingot_hot", false, GenerationFlag.HINGOT);
    public static Prefix Plate = new Prefix("plate", true, GenerationFlag.PLATE);
    public static Prefix PlateDense = new Prefix("plate_dense", true, GenerationFlag.DPLATE);
    public static Prefix Gem = new Prefix("gem", true, GenerationFlag.BGEM);
    public static Prefix GemChipped = new Prefix("gem_chipped", true, GenerationFlag.GEM);
    public static Prefix GemFlawed = new Prefix("gem_flawed", true, GenerationFlag.GEM);
    public static Prefix GemFlawless = new Prefix("gem_flawless", true, GenerationFlag.GEM);
    public static Prefix GemExquisite = new Prefix("gem_exquisite", true, GenerationFlag.GEM);
    public static Prefix Foil = new Prefix("foil", true, GenerationFlag.FOIL);
    public static Prefix Rod = new Prefix("rod", true, GenerationFlag.ROD);
    public static Prefix Bolt = new Prefix("bolt", true, GenerationFlag.BOLT);
    public static Prefix Screw = new Prefix("screw", true, GenerationFlag.SCREW);
    public static Prefix Ring = new Prefix("ring", true, GenerationFlag.RING);
    public static Prefix Spring = new Prefix("spring", true, GenerationFlag.SPRING);
    public static Prefix WireFine = new Prefix("wire_fine", true, GenerationFlag.WIREF);
    public static Prefix Rotor = new Prefix("rotor", true, GenerationFlag.ROTOR);
    public static Prefix Gear = new Prefix("gear", true, GenerationFlag.GEAR);
    public static Prefix GearSmall = new Prefix("gear_small", true, GenerationFlag.SGEAR);
    public static Prefix Lens = new Prefix("lens", true, GenerationFlag.GEM);
    public static Prefix Cell = new Prefix("cell", true, GenerationFlag.LIQUID);
    public static Prefix CellGas = new Prefix("cell_gas", true, GenerationFlag.GAS);
    public static Prefix CellPlasma = new Prefix("cell_plasma", true, GenerationFlag.PLASMA);

    private String name, namePre, namePost;

    private boolean doesGenerate, hasLocName, visible;
    private long generationBits;

    public Prefix(String name, boolean visible, GenerationFlag... flags) {
        this.name = name;
        this.visible = visible;
        for (GenerationFlag flag : flags) {
            generationBits |= flag.getBit();
        }
        this.doesGenerate = true;
        PREFIX_LOOKUP.put(name, this);
    }

    public Prefix(String name, boolean visible, boolean generatesItems, GenerationFlag... flags) {
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
        return doesGenerate && (material.getItemMask() & generationBits) != 0;
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
