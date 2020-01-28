package muramasa.antimatter.ore;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.materials.Material;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.texture.Texture;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class StoneType implements IAntimatterObject {

    private static List<StoneType> stoneGenerating = new LinkedList<>();

    private String id, oreId, mod = "";
    private int harvestLevel;
    private boolean gravity, generate;
    private Material material;
    private Texture texture;
    private SoundType soundType;
    private BlockState baseState;
    
    public StoneType(String id, String oreId, Material material, BlockState baseState, boolean generate, boolean generateStone, Texture texture, SoundType soundType, int harvestLevel, boolean gravity) {
        this.id = id;
        this.oreId = oreId;
        this.material = material;
        this.baseState = baseState;
        this.generate = generate;
        if (generateStone) stoneGenerating.add(this);
        this.texture = texture;
        this.soundType = soundType;
        this.harvestLevel = harvestLevel;
        this.gravity = gravity;
        AntimatterAPI.register(StoneType.class, this);
    }

    public StoneType(String id, String oreId, Material material, boolean generateStone, Texture texture, SoundType soundType, int harvestLevel) {
        this(id, oreId, material, Blocks.AIR.getDefaultState(), true, generateStone, texture, soundType, harvestLevel, false);
    }

    public StoneType(String id, String oreId, Material material, BlockState baseState, Texture texture, SoundType soundType, boolean gravity) {
        this(id, oreId, material, baseState, true, false, texture, soundType, 0, gravity);
    }

    public StoneType(String id, Material material, BlockState baseState, boolean generate, Texture texture) {
        this(id, "", material, baseState, generate, false, texture, SoundType.STONE, 0, false);
    }

    public StoneType(String id, String oreId, Material material, BlockState baseState, Texture texture) {
        this(id, oreId, material, baseState, texture, SoundType.STONE, false);
    }

    //For registrar uses
    public StoneType(String id, String oreId, String mod, Material material, BlockState baseState, Texture texture, SoundType soundType, int harvestLevel, boolean gravity) {
        this(id, oreId, material, baseState, true, false, texture, soundType, harvestLevel, gravity);
        this.mod = mod;
    }

    public StoneType(String id, String oreId, String mod, Material material, Texture texture) {
        this(id, oreId, mod, material, Blocks.AIR.getDefaultState(), texture, SoundType.STONE, 0, false);
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
    
    public BlockState getBaseState() {
        return baseState;
    }
    
    public boolean getGenerating() {
        return generate;
    }

    public Texture getTexture() {
        return texture;
    }
    
    public SoundType getSoundType() {
    	return soundType;
    }
    
    public int getHarvestLevel() {
        return harvestLevel;
    }
    
    public boolean getGravity() {
        return gravity;
    }
    
    public void setBaseState(BlockState baseState) {
        this.baseState = baseState;
    }
    
    public void setGenerating(boolean generate) {
        this.generate = generate;
    }

    public static List<StoneType> getStoneGenerating() {
        return stoneGenerating;
    }

    public static Collection<Texture> getAllTextures() {
        ArrayList<Texture> textures = new ArrayList<>();
        for (StoneType type : getAllActive()) {
            textures.add(type.getTexture());
        }
        return textures;
    }

    //TODO collection
    public static StoneType[] getAll() {
        return AntimatterAPI.all(StoneType.class).stream().toArray(size -> new StoneType[size]);
    }

    //TODO collection
    public static StoneType[] getAllActive() {
        return AntimatterAPI.all(StoneType.class).stream().filter(s -> s.generate).toArray(size -> new StoneType[size]);
    }

    //TODO collection
    public static StoneType[] getVanillaTypes() {
        return AntimatterAPI.all(StoneType.class).stream().filter(s -> s.mod.isEmpty()).toArray(size -> new StoneType[size]);
    }
    
    public static StoneType get(String id) {
        return AntimatterAPI.get(StoneType.class, id);
    }
    
}
