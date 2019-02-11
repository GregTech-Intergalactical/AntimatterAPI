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

    public static Prefix Ore = new Prefix("ore", true, false, GenerationFlag.CRUSHED.getBit());
    public static Prefix Block = new Prefix("block", true, false, GenerationFlag.CRUSHED.getBit());

    public static Prefix Chunk = new Prefix("chunk", true, GenerationFlag.CRUSHED.getBit());
    public static Prefix Crushed = new Prefix("crushed", false, GenerationFlag.CRUSHED.getBit());
    public static Prefix CrushedCentrifuged = new Prefix("crushed_centrifuged", false, GenerationFlag.CRUSHEDC.getBit());
    public static Prefix CrushedPurified = new Prefix("crushed_purified", false, GenerationFlag.CRUSHEDP.getBit());
    public static Prefix Dust = new Prefix("dust", true, GenerationFlag.DUST.getBit());
    public static Prefix DustSmall = new Prefix("dust_small", false, GenerationFlag.DUST.getBit());
    public static Prefix DustTiny = new Prefix("dust_tiny", false, GenerationFlag.DUST.getBit());
    public static Prefix Nugget = new Prefix("nugget", false, GenerationFlag.INGOT.getBit());
    public static Prefix Ingot = new Prefix("ingot", true, GenerationFlag.INGOT.getBit());
    public static Prefix IngotHot = new Prefix("ingot_hot", false, GenerationFlag.HINGOT.getBit());
    public static Prefix Plate = new Prefix("plate", true, GenerationFlag.PLATE.getBit());
    public static Prefix PlateDense = new Prefix("plate_dense", true, GenerationFlag.DPLATE.getBit());
    public static Prefix Gem = new Prefix("gem", true, GenerationFlag.BGEM.getBit());
    public static Prefix GemChipped = new Prefix("gem_chipped", true, GenerationFlag.GEM.getBit());
    public static Prefix GemFlawed = new Prefix("gem_flawed", true, GenerationFlag.GEM.getBit());
    public static Prefix GemFlawless = new Prefix("gem_flawless", true, GenerationFlag.GEM.getBit());
    public static Prefix GemExquisite = new Prefix("gem_exquisite", true, GenerationFlag.GEM.getBit());
    public static Prefix Foil = new Prefix("foil", true, GenerationFlag.FOIL.getBit());
    public static Prefix Rod = new Prefix("rod", true, GenerationFlag.ROD.getBit());
    public static Prefix Bolt = new Prefix("bolt", true, GenerationFlag.BOLT.getBit());
    public static Prefix Screw = new Prefix("screw", true, GenerationFlag.SCREW.getBit());
    public static Prefix Ring = new Prefix("ring", true, GenerationFlag.RING.getBit());
    public static Prefix Spring = new Prefix("spring", true, GenerationFlag.SPRING.getBit());
    public static Prefix WireFine = new Prefix("wire_fine", true, GenerationFlag.WIREF.getBit());
    public static Prefix Rotor = new Prefix("rotor", true, GenerationFlag.ROTOR.getBit());
    public static Prefix Gear = new Prefix("gear", true, GenerationFlag.GEAR.getBit());
    public static Prefix GearSmall = new Prefix("gear_small", true, GenerationFlag.SGEAR.getBit());
    public static Prefix Lens = new Prefix("lens", true, GenerationFlag.GEM.getBit());
    public static Prefix Cell = new Prefix("cell", true, GenerationFlag.LIQUID.getBit());
    public static Prefix CellGas = new Prefix("cell_gas", true, GenerationFlag.GAS.getBit());
    public static Prefix CellPlasma = new Prefix("cell_plasma", true, GenerationFlag.PLASMA.getBit());

    private String name, namePre, namePost;

    private boolean generatesItems, hasLocName, visible;
    private long generationBits;

    public Prefix(String name, boolean visible, long generationBits) {
        this.name = name;
        this.visible = visible;
        this.generationBits = generationBits;
        this.generatesItems = true;
        PREFIX_LOOKUP.put(name, this);
    }

    public Prefix(String name, boolean visible, boolean generatesItems, long generationBits) {
        this(name, visible, generationBits);
        this.generatesItems = generatesItems;
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
        return generatesItems && (material.getItemMask() & generationBits) != 0;
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
