package muramasa.gregtech.api.materials;

import muramasa.gregtech.api.enums.ItemFlag;
import muramasa.gregtech.common.utils.Ref;
import net.minecraft.client.resources.I18n;

import java.util.Locale;

public enum Prefix {

//    ORE(true, 0), //TODO capitalize and add getJeiCategoryID
    CHUNK(true, ItemFlag.CRUSHED.getBit()),
    CRUSHED(false, ItemFlag.CRUSHED.getBit()),
    CRUSHED_CENTRIFUGED(false, ItemFlag.CRUSHEDC.getBit()),
    CRUSHED_PURIFIED(false, ItemFlag.CRUSHEDP.getBit()),
    DUST(true, ItemFlag.DUST.getBit()),
    DUST_SMALL(false, ItemFlag.DUST.getBit()),
    DUST_TINY(false, ItemFlag.DUST.getBit()),
    NUGGET(false, ItemFlag.INGOT.getBit()),
    INGOT(true, ItemFlag.INGOT.getBit()),
    INGOT_HOT(false, ItemFlag.HINGOT.getBit()),
    PLATE(true, ItemFlag.PLATE.getBit()),
    PLATE_DENSE(true, ItemFlag.DPLATE.getBit()),
    GEM(true, ItemFlag.BGEM.getBit()),
    GEM_CHIPPED(true, ItemFlag.GEM.getBit()),
    GEM_FLAWED(true, ItemFlag.GEM.getBit()),
    GEM_FLAWLESS(true, ItemFlag.GEM.getBit()),
    GEM_EXQUISITE(true, ItemFlag.GEM.getBit()),
    FOIL(true, ItemFlag.FOIL.getBit()),
    ROD(true, ItemFlag.ROD.getBit()),
    BOLT(true, ItemFlag.BOLT.getBit()),
    SCREW(true, ItemFlag.SCREW.getBit()),
    RING(true, ItemFlag.RING.getBit()),
    SPRING(true, ItemFlag.SPRING.getBit()),
    WIRE_FINE(true, ItemFlag.WIREF.getBit()),
    ROTOR(true, ItemFlag.ROTOR.getBit()),
    GEAR(true, ItemFlag.GEAR.getBit()),
    GEAR_SMALL(true, ItemFlag.SGEAR.getBit()),
    LENS(true, ItemFlag.GEM.getBit()),
    CELL(true, ItemFlag.FLUID.getBit() | ItemFlag.GAS.getBit()),
    CELL_PLASMA(true, ItemFlag.PLASMA.getBit());

    private String namePre, namePost;

    private boolean hasLocName, showInCreative;
    private long generationBits;

    Prefix(boolean showInCreative, long generationBits) {
        this.showInCreative = showInCreative;
        this.generationBits = generationBits;
    }

    public String getName() {
        return name().toLowerCase(Locale.ENGLISH);
    }

    public String getNameWithMaterial(Material material) {
        return name() + material.getName();
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

    public boolean showInCreative() {
        return showInCreative || Ref.showAllItemsInCreative;
    }

    public boolean allowGeneration(Material material) {
        return (material.getItemMask() & generationBits) != 0;
    }
}
