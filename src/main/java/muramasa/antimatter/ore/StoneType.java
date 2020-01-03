package muramasa.antimatter.ore;

import muramasa.gtu.Configs;
import muramasa.gtu.Ref;
import muramasa.antimatter.GregTechAPI;
import muramasa.gtu.data.Materials;
import muramasa.antimatter.materials.Material;
import muramasa.antimatter.registration.IGregTechObject;
import muramasa.antimatter.texture.Texture;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class StoneType implements IGregTechObject {

    private static List<StoneType> stoneGenerating = new LinkedList<>();
    
    public final static BlockState AIR = Blocks.AIR.getDefaultState();
    private static BlockState STONE_STATE = Blocks.STONE.getDefaultState();
    private static BlockState SAND_STATE = Blocks.SAND.getDefaultState();
    
    private static SoundType STONE_SOUND = SoundType.STONE;
    private static SoundType SAND_SOUND = SoundType.SAND;
    
    private static boolean IS_UB_LOADED = ModList.get().isLoaded(Ref.MOD_UB);

    //STONE should be the only non-removable StoneType. It serves as the foundation. It is also used natively by BlockRock
    public static final StoneType STONE = new StoneType("stone", Materials.Stone, STONE_STATE, true, new Texture("minecraft", "block/stone"));

    //TODO evaluate if needed. These are considered "stone" and are replace by ores anyway.
    //TODO Might need Red Sandstone in here to stay in-line with red sand/sandstone
    public static StoneType GRANITE = new StoneType("granite", Materials.Stone, Blocks.GRANITE.getDefaultState(), !Configs.WORLD.DISABLE_VANILLA_STONE_GEN || Ref.debugStones, new Texture("minecraft", "block/granite"));
    public static StoneType DIORITE = new StoneType("diorite", Materials.Stone, Blocks.DIORITE.getDefaultState(), !Configs.WORLD.DISABLE_VANILLA_STONE_GEN || Ref.debugStones, new Texture("minecraft", "block/diorite"));
    public static StoneType ANDESITE = new StoneType("andesite", Materials.Stone, Blocks.ANDESITE.getDefaultState(), !Configs.WORLD.DISABLE_VANILLA_STONE_GEN || Ref.debugStones, new Texture("minecraft", "block/andesite"));

    public static StoneType SAND = new StoneType("sand", "sand", Materials.SiliconDioxide, SAND_STATE, new Texture("minecraft", "block/sand"), SAND_SOUND, true);
    public static StoneType SAND_RED = new StoneType("sand_red", "sand", Materials.SiliconDioxide, Blocks.RED_SAND.getDefaultState(), new Texture("minecraft", "block/red_sand"), SAND_SOUND, true);
    public static StoneType SANDSTONE = new StoneType("sandstone", "sandstone", Materials.SiliconDioxide, Blocks.SANDSTONE.getDefaultState(), new Texture("minecraft", "block/sandstone"), STONE_SOUND, false);
    
    public static StoneType NETHERRACK = new StoneType("netherrack", "nether", Materials.Netherrack, Blocks.NETHERRACK.getDefaultState(), new Texture("minecraft", "block/netherrack"));
    public static StoneType ENDSTONE = new StoneType("endstone", "end", Materials.Endstone, Blocks.END_STONE.getDefaultState(), new Texture("minecraft", "block/end_stone"));

    public static StoneType GRANITE_RED = new StoneType("granite_red", "granite_red", Materials.GraniteRed, true, new Texture("block/stone/granite_red"), STONE_SOUND, 2);
    public static StoneType GRANITE_BLACK = new StoneType("granite_black", "granite_black", Materials.GraniteBlack, true, new Texture("block/stone/granite_black"), STONE_SOUND, 2);
    public static StoneType MARBLE = new StoneType("marble", "marble", Materials.Marble, true, new Texture("block/stone/marble"), STONE_SOUND, 0);
    public static StoneType BASALT = new StoneType("basalt", "basalt", Materials.Basalt, true, new Texture("block/stone/basalt"), STONE_SOUND, 0);

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
        GregTechAPI.register(StoneType.class, this);
    }

    public StoneType(String id, String oreId, Material material, boolean generateStone, Texture texture, SoundType soundType, int harvestLevel) {
        this(id, oreId, material, AIR, true, generateStone, texture, soundType, harvestLevel, false);
    }
    
    public StoneType(String id, String oreId, Material material, BlockState baseState, Texture texture, SoundType soundType, boolean gravity) {
        this(id, oreId, material, baseState, true, false, texture, soundType, 0, gravity);
    } 
    
    public StoneType(String id, Material material, BlockState baseState, boolean generate, Texture texture) {
        this(id, "", material, baseState, generate, false, texture, STONE_SOUND, 0, false);
    }
    
    public StoneType(String id, String oreId, Material material, BlockState baseState, Texture texture) {
        this(id, oreId, material, baseState, texture, STONE_SOUND, false);
    }

    //For registrar uses
    public StoneType(String id, String oreId, String mod, Material material, BlockState baseState, Texture texture, SoundType soundType, int harvestLevel, boolean gravity) {
        this(id, oreId, material, baseState, true, false, texture, soundType, harvestLevel, gravity);
        this.mod = mod;
    }
    
    public StoneType(String id, String oreId, String mod, Material material, Texture texture) {
        this(id, oreId, mod, material, AIR, texture, STONE_SOUND, 0, false);
    }
    
    public StoneType(String id, String mod, Texture texture) {
        this(id, "", mod, Materials.Stone, AIR, texture, STONE_SOUND, 0, false);
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
        return GregTechAPI.all(StoneType.class).stream().toArray(size -> new StoneType[size]);
    }

    //TODO collection
    public static StoneType[] getAllActive() {
        return GregTechAPI.all(StoneType.class).stream().filter(s -> s.generate).toArray(size -> new StoneType[size]);
    }

    //TODO collection
    public static StoneType[] getVanillaTypes() {
        return GregTechAPI.all(StoneType.class).stream().filter(s -> s.mod.isEmpty()).toArray(size -> new StoneType[size]);
    }
    
    public static StoneType get(String id) {
        return GregTechAPI.get(StoneType.class, id);
    }
    
}
