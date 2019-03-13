package muramasa.gregtech.api.enums;

import muramasa.gregtech.api.data.Materials;
import muramasa.gregtech.api.materials.Material;
import muramasa.gregtech.api.texture.Texture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

import java.util.ArrayList;
import java.util.Collection;

public class StoneType implements IStringSerializable {

    private static ArrayList<StoneType> generating = new ArrayList<>(), all = new ArrayList<>();
    public static int lastInternalId = 0;

    public static StoneType STONE = new StoneType("stone", false, Materials.Stone);
    public static StoneType GRANITE = new StoneType("granite", false, Materials.Stone);
    public static StoneType DIORITE = new StoneType("diorite", false, Materials.Stone);
    public static StoneType ANDESITE = new StoneType("andesite", false, Materials.Stone);
    public static StoneType NETHERRACK = new StoneType("netherrack", false, Materials.Netherrack);
    public static StoneType ENDSTONE = new StoneType("endstone", false, Materials.Endstone);

    public static StoneType GRANITE_RED = new StoneType("granite_red", Materials.GarnetRed);
    public static StoneType GRANITE_BLACK = new StoneType("granite_black", Materials.GraniteBlack);
    public static StoneType MARBLE = new StoneType("marble", Materials.Marble);
    public static StoneType BASALT = new StoneType("basalt", Materials.Basalt);

    private String name;
    private Material material;
    private int internalId;

    public StoneType(String name, Material material) {
        this(name, true, material);
    }

    public StoneType(String name, boolean generate, Material material) {
        this.name = name;
        this.material = material;
        this.internalId = lastInternalId++;
        if (generate) {
            generating.add(this);
        }
        all.add(this);
    }

    @Override
    public String getName() {
        return name;
    }

    public ItemStack getDroppedDust() {
        return material.getDust(1);
    }

    public Texture getTexture() {
        return new Texture("blocks/stone/" + getName());
    }

    public int getInternalId() {
        return internalId;
    }

    public static Collection<StoneType> getGenerating() {
        return generating;
    }

    public static Collection<StoneType> getAll() {
        return all;
    }

    public static int getLastInternalId() {
        return lastInternalId;
    }
}
