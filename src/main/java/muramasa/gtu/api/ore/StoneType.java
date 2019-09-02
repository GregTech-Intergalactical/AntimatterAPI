package muramasa.gtu.api.ore;

import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.data.Materials;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.registration.IGregTechObject;
import muramasa.gtu.api.texture.Texture;
import muramasa.gtu.api.util.Utils;
import net.minecraft.block.SoundType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class StoneType implements IGregTechObject {

    private static List<StoneType> generating = new LinkedList<>(), all = new LinkedList<>();

    public static StoneType STONE = new StoneType("stone", "", Materials.Stone, false, new Texture("minecraft", "blocks/stone"), SoundType.STONE);

    //TODO evaluate if needed. These are considered "stone" and are replace by ores anyway.
    public static StoneType GRANITE = new StoneType("granite", "", Materials.Stone, false, new Texture("minecraft", "blocks/stone_granite"), SoundType.STONE);
    public static StoneType DIORITE = new StoneType("diorite", "", Materials.Stone, false, new Texture("minecraft", "blocks/stone_diorite"), SoundType.STONE);
    public static StoneType ANDESITE = new StoneType("andesite", "", Materials.Stone, false, new Texture("minecraft", "blocks/stone_andesite"), SoundType.STONE);

    public static StoneType SAND = new StoneType("sand", "sand", Materials.SiliconDioxide, false, new Texture("minecraft", "blocks/sand"), SoundType.SAND);
    public static StoneType SAND_RED = new StoneType("sand_red", "sand", Materials.SiliconDioxide, false, new Texture("minecraft", "blocks/red_sand"), SoundType.SAND);
    public static StoneType SANDSTONE = new StoneType("sandstone", "sandstone", Materials.SiliconDioxide, false, new Texture("minecraft", "blocks/sandstone_normal"), SoundType.STONE);

    public static StoneType NETHERRACK = new StoneType("netherrack", "nether", Materials.Netherrack, false, new Texture("minecraft", "blocks/netherrack"), SoundType.STONE);
    public static StoneType ENDSTONE = new StoneType("endstone", "end", Materials.Endstone, false, new Texture("minecraft", "blocks/end_stone"), SoundType.STONE);

    public static StoneType GRANITE_RED = new StoneType("granite_red", "granite_red", Materials.GraniteRed, true, new Texture("blocks/stone/granite_red"), SoundType.STONE);
    public static StoneType GRANITE_BLACK = new StoneType("granite_black", "granite_black", Materials.GraniteBlack, true, new Texture("blocks/stone/granite_black"), SoundType.STONE);
    public static StoneType MARBLE = new StoneType("marble", "marble", Materials.Marble, true, new Texture("blocks/stone/marble"), SoundType.STONE);
    public static StoneType BASALT = new StoneType("basalt", "basalt", Materials.Basalt, true, new Texture("blocks/stone/basalt"), SoundType.STONE);

    private String id, oreId, mod = "";
    private Material material;
    private Texture texture;
    private SoundType soundType;
    
    public StoneType(String id, String oreId, Material material, boolean generate, Texture texture, SoundType soundType) {
        this.id = id;
        this.oreId = oreId;
        this.material = material;
        this.texture = texture;
        this.soundType = soundType;
        if (generate) {
            generating.add(this);
        }
        all.add(this);
        GregTechAPI.register(StoneType.class, this);
    }

    public StoneType(String id, String oreId, String mod, Material material, boolean generate, Texture texture, SoundType soundType) {
        this(id, oreId, material, generate, texture, soundType);
        this.mod = mod;
    }

    @Override
    public String getId() {
        return id;
    }
    
    public String getOreId() {
        return oreId;
    }

    public Material getMaterial() {
        return material;
    }

    public Texture getTexture() {
        return texture;
    }
    
    public SoundType getSoundType() {
    	return soundType;
    }

    public static List<StoneType> getGenerating() {
        return generating;
    }

    public static List<StoneType> getAllActive() {
        return all.stream().filter(s -> (s.mod.isEmpty() || Utils.isModLoaded(s.mod))).collect(Collectors.toList());
    }

    public static Collection<Texture> getAllTextures() {
        ArrayList<Texture> textures = new ArrayList<>();
        for (StoneType type : getAllActive()) {
            textures.add(type.getTexture());
        }
        return textures;
    }
}
