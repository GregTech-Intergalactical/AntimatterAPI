package muramasa.gregtech.api.enums;

import muramasa.gregtech.api.data.Materials;
import muramasa.gregtech.api.materials.Material;
import muramasa.gregtech.api.texture.Texture;
import net.minecraft.util.IStringSerializable;

import java.util.ArrayList;
import java.util.Collection;

public class StoneType implements IStringSerializable {

    private static ArrayList<StoneType> generating = new ArrayList<>(), all = new ArrayList<>();
    public static int lastInternalId = 0;

    public static StoneType STONE = new StoneType("stone", Materials.Stone, false, "stone");
    public static StoneType GRANITE = new StoneType("granite", Materials.Stone, false, "stone_granite");
    public static StoneType DIORITE = new StoneType("diorite", Materials.Stone, false, "stone_diorite");
    public static StoneType ANDESITE = new StoneType("andesite", Materials.Stone, false, "stone_andesite");
    public static StoneType NETHERRACK = new StoneType("netherrack", Materials.Netherrack, false, "netherrack");
    public static StoneType ENDSTONE = new StoneType("endstone", Materials.Endstone, false, "end_stone");

    public static StoneType GRANITE_RED = new StoneType("granite_red", Materials.GarnetRed);
    public static StoneType GRANITE_BLACK = new StoneType("granite_black", Materials.GraniteBlack);
    public static StoneType MARBLE = new StoneType("marble", Materials.Marble);
    public static StoneType BASALT = new StoneType("basalt", Materials.Basalt);

    private String name;
    private Material material;
    private String textureName;
    private int internalId;

    public StoneType(String name, Material material) {
        this(name, material, true, name);
    }

    public StoneType(String name, Material material, boolean generate, String textureName) {
        this.name = name;
        this.material = material;
        this.textureName = textureName;
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

    public Material getMaterial() {
        return material;
    }

    public Texture getTexture() {
        if (!generating.contains(this)) new Texture("minecraft", "blocks/" + textureName);
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

    public static Collection<Texture> getAllTextures() {
        ArrayList<Texture> textures = new ArrayList<>();
        for (StoneType type : getAll()) {
            textures.add(type.getTexture());
        }
        return textures;
    }

    public static int getLastInternalId() {
        return lastInternalId;
    }
}
