package muramasa.antimatter.ore;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.block.BlockStone;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.IRegistryEntryProvider;
import muramasa.antimatter.texture.Texture;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.List;
import java.util.Collection;

public class StoneType implements IAntimatterObject, IRegistryEntryProvider {

    private String domain, id;
    //private int harvestLevel;
    private boolean gravity = false, generateBlock = false;
    private Material material;
    private Texture texture;
    private SoundType soundType;
    private BlockState state;
    
    public StoneType(String domain, String id, Material material, Texture texture, SoundType soundType, boolean generateBlock) {
        this.domain = domain;
        this.id = id;
        this.material = material;
        this.texture = texture;
        this.soundType = soundType;
        this.generateBlock = generateBlock;
        AntimatterAPI.register(StoneType.class, "stone_" + id, this);
    }

    @Override
    public void onRegistryBuild(String domain, IForgeRegistry<?> registry) {
        if (!this.domain.equals(domain) || !doesGenerateBlock() || registry != ForgeRegistries.BLOCKS) return;
        BlockStone stone = new BlockStone(this);
        setState(stone);
    }

    public String getDomain() {
        return domain;
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

    public boolean doesGenerateBlock() {
        return generateBlock;
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
        List<Texture> textures = new ObjectArrayList<>();
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
        return AntimatterAPI.get(StoneType.class, "stone_" + id);
    }
}
