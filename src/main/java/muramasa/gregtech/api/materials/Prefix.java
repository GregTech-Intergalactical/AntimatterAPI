package muramasa.gregtech.api.materials;

import muramasa.gregtech.api.enums.GenerationFlag;
import muramasa.gregtech.common.utils.Ref;
import net.minecraft.client.resources.I18n;

import java.util.Locale;

public enum Prefix {

//    ORE(true, 0), //TODO capitalize and add getJeiCategoryID
    CHUNK(true, GenerationFlag.CRUSHED.getBit()),
    CRUSHED(false, GenerationFlag.CRUSHED.getBit()),
    CRUSHED_CENTRIFUGED(false, GenerationFlag.CRUSHEDC.getBit()),
    CRUSHED_PURIFIED(false, GenerationFlag.CRUSHEDP.getBit()),
    DUST(true, GenerationFlag.DUST.getBit()),
    DUST_SMALL(false, GenerationFlag.DUST.getBit()),
    DUST_TINY(false, GenerationFlag.DUST.getBit()),
    NUGGET(false, GenerationFlag.INGOT.getBit()),
    INGOT(true, GenerationFlag.INGOT.getBit()),
    INGOT_HOT(false, GenerationFlag.HINGOT.getBit()),
    PLATE(true, GenerationFlag.PLATE.getBit()),
    PLATE_DENSE(true, GenerationFlag.DPLATE.getBit()),
    GEM(true, GenerationFlag.BGEM.getBit()),
    GEM_CHIPPED(true, GenerationFlag.GEM.getBit()),
    GEM_FLAWED(true, GenerationFlag.GEM.getBit()),
    GEM_FLAWLESS(true, GenerationFlag.GEM.getBit()),
    GEM_EXQUISITE(true, GenerationFlag.GEM.getBit()),
    FOIL(true, GenerationFlag.FOIL.getBit()),
    ROD(true, GenerationFlag.ROD.getBit()),
    BOLT(true, GenerationFlag.BOLT.getBit()),
    SCREW(true, GenerationFlag.SCREW.getBit()),
    RING(true, GenerationFlag.RING.getBit()),
    SPRING(true, GenerationFlag.SPRING.getBit()),
    WIRE_FINE(true, GenerationFlag.WIREF.getBit()),
    ROTOR(true, GenerationFlag.ROTOR.getBit()),
    GEAR(true, GenerationFlag.GEAR.getBit()),
    GEAR_SMALL(true, GenerationFlag.SGEAR.getBit()),
    LENS(true, GenerationFlag.GEM.getBit()),
    CELL(true, GenerationFlag.FLUID.getBit() | GenerationFlag.GAS.getBit()),
    CELL_PLASMA(true, GenerationFlag.PLASMA.getBit());

    private String namePre, namePost;

    private boolean hasLocName, visible;
    private long generationBits;

    Prefix(boolean visible, long generationBits) {
        this.visible = visible;
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

    public boolean isVisible() {
        return visible || Ref.showAllItems;
    }

    public boolean allowGeneration(Material material) {
        return (material.getItemMask() & generationBits) != 0;
    }

    @Override
    public String toString() {
        return getName();
    }
}
