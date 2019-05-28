package muramasa.gtu.api.data;

import muramasa.gtu.api.interfaces.IGregTechObject;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.texture.Texture;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;

public class StoneType implements IGregTechObject {

    private static ArrayList<StoneType> generating = new ArrayList<>(), all = new ArrayList<>();
    public static int lastInternalId = 0;
    
    //TODO: more functionality, soundtype etc

    public static StoneType STONE = new StoneType("stone", Materials.Stone, false, new ResourceLocation("minecraft", "blocks/stone"));
    public static StoneType GRANITE = new StoneType("granite", Materials.Stone, false, new ResourceLocation("minecraft", "blocks/stone_granite"));
    public static StoneType DIORITE = new StoneType("diorite", Materials.Stone, false, new ResourceLocation("minecraft", "blocks/stone_diorite"));
    public static StoneType ANDESITE = new StoneType("andesite", Materials.Stone, false, new ResourceLocation("minecraft", "blocks/stone_andesite"));
    
    public static StoneType SAND = new StoneType("sand", Materials.SiliconDioxide, false, new ResourceLocation("minecraft", "blocks/sand"));
    public static StoneType SANDSTONE = new StoneType("sandstone", Materials.SiliconDioxide, false, new ResourceLocation("minecraft", "blocks/sandstone"));
    
    public static StoneType NETHERRACK = new StoneType("netherrack", Materials.Netherrack, false, new ResourceLocation("minecraft", "blocks/netherrack"));
    public static StoneType ENDSTONE = new StoneType("endstone", Materials.Endstone, false, new ResourceLocation("minecraft", "blocks/end_stone"));

    public static StoneType GRANITE_RED = new StoneType("granite_red", Materials.GraniteRed, true, new ResourceLocation("gregtech", "blocks/stone/granite_red"));
    public static StoneType GRANITE_BLACK = new StoneType("granite_black", Materials.GraniteBlack, true, new ResourceLocation("gregtech", "blocks/stone/granite_black"));
    public static StoneType MARBLE = new StoneType("marble", Materials.Marble, true, new ResourceLocation("gregtech", "blocks/stone/marble"));
    public static StoneType BASALT = new StoneType("basalt", Materials.Basalt, true, new ResourceLocation("gregtech", "blocks/stone/basalt"));

    private String name;
    private Material material;
    private ResourceLocation loc;
    private int internalId;

    public StoneType(String name, Material material, boolean generate, ResourceLocation loc) {
        this.name = name;
        this.material = material;
        this.loc = loc;
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
        return new Texture(loc);
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
