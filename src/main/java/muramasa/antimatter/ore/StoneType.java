package muramasa.antimatter.ore;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.block.BlockStone;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.texture.Texture;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;

import java.util.ArrayList;
import java.util.Collection;

public class StoneType implements IAntimatterObject {

    private String id;
    //private int harvestLevel;
    private boolean gravity = false, generateBlock = false;
    private Material material;
    private Texture texture;
    private SoundType soundType;
    private BlockState state;
    private BlockStone blockStone;
    
    public StoneType(String domain, String id, Material material, Texture texture, SoundType soundType, boolean generateBlock) {
        this.id = id;
        this.material = material;
        this.texture = texture;
        this.soundType = soundType;
        this.generateBlock = generateBlock;
        if (generateBlock) {
            blockStone = new BlockStone(domain, this);
            state = blockStone.getDefaultState();
        }
        AntimatterAPI.register(StoneType.class, this);
    }

    @Override
    public String getId() {
        return id;
    }

    public Material getMaterial() {
        return material;
    }
    
    public BlockState getState() {
        return state;
    }

    public Texture getTexture() {
        return texture;
    }
    
    public SoundType getSoundType() {
    	return soundType;
    }

    public StoneType setState(Block block) {
        this.state = block.getDefaultState();
        return this;
    }

    public StoneType setState(BlockState blockState) {
        this.state = blockState;
        return this;
    }
    
//    public int getHarvestLevel() {
//        return harvestLevel;
//    }
//
//    public boolean getGravity() {
//        return gravity;
//    }

    public static Collection<Texture> getAllTextures() {
        ArrayList<Texture> textures = new ArrayList<>();
        for (StoneType type : getAllGeneratingBlock()) {
            textures.add(type.getTexture());
        }
        return textures;
    }

    //TODO collection
    public static StoneType[] getAllGeneratingBlock() {
        return AntimatterAPI.all(StoneType.class).stream().filter(s -> s.generateBlock).toArray(StoneType[]::new);
    }
    
    public static StoneType get(String id) {
        return AntimatterAPI.get(StoneType.class, id);
    }
    
}
