package muramasa.itech.api.materials;

import muramasa.itech.api.enums.ItemFlag;
import muramasa.itech.common.utils.Ref;
import net.minecraft.client.resources.I18n;

import java.util.Locale;

public enum Prefix {

//    ORE(true, 0), //TODO capitalize and add getJeiCategoryID
    CHUNK(true, ItemFlag.CRUSHED.getMask()),
    CRUSHED(false, ItemFlag.CRUSHED.getMask()),
    CRUSHED_CENTRIFUGED(false, ItemFlag.CRUSHEDC.getMask()),
    CRUSHED_PURIFIED(false, ItemFlag.CRUSHEDP.getMask()),
    DUST(true, ItemFlag.DUST.getMask()),
    DUST_SMALL(false, ItemFlag.DUST.getMask()),
    DUST_TINY(false, ItemFlag.DUST.getMask()),
    NUGGET(false, ItemFlag.INGOT.getMask()),
    INGOT(true, ItemFlag.INGOT.getMask()),
    INGOT_HOT(false, ItemFlag.HINGOT.getMask()),
    PLATE(true, ItemFlag.PLATE.getMask()),
    PLATE_DENSE(true, ItemFlag.DPLATE.getMask()),
    GEM(true, ItemFlag.BGEM.getMask()),
    GEM_CHIPPED(true, ItemFlag.GEM.getMask()),
    GEM_FLAWED(true, ItemFlag.GEM.getMask()),
    GEM_FLAWLESS(true, ItemFlag.GEM.getMask()),
    GEM_EXQUISITE(true, ItemFlag.GEM.getMask()),
    FOIL(true, ItemFlag.FOIL.getMask()),
    ROD(true, ItemFlag.ROD.getMask()),
    BOLT(true, ItemFlag.BOLT.getMask()),
    SCREW(true, ItemFlag.SCREW.getMask()),
    RING(true, ItemFlag.RING.getMask()),
    SPRING(true, ItemFlag.SPRING.getMask()),
    WIRE_FINE(true, ItemFlag.WIREF.getMask()),
    ROTOR(true, ItemFlag.ROTOR.getMask()),
    GEAR(true, ItemFlag.GEAR.getMask()),
    GEAR_SMALL(true, ItemFlag.SGEAR.getMask()),
    LENS(true, ItemFlag.GEM.getMask()),
    CELL(true, ItemFlag.FLUID.getMask() | ItemFlag.GAS.getMask()),
    CELL_PLASMA(true, ItemFlag.PLASMA.getMask());

    private String namePre, namePost;

    private boolean hasLocName, showInCreative;
    private int generationBits;

    Prefix(boolean showInCreative, int generationBits) {
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
