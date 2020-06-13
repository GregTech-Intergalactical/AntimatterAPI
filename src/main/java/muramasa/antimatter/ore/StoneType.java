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
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Collection;
import java.util.List;

public class StoneType implements IAntimatterObject, IRegistryEntryProvider {

    private final String domain, id;
    //private int harvestLevel;
    private final boolean gravity = false;
    public boolean generateBlock = false;
    private final Material material;
    private final Texture texture;
    private final SoundType soundType;
    private BlockState state;
    
    public StoneType(String domain, String id, Material material, Texture texture, SoundType soundType, boolean generateBlock) {
        this.domain = domain;
        this.id = "stone_" + id;
        this.material = material;
        this.texture = texture;
        this.soundType = soundType;
        this.generateBlock = generateBlock;
        AntimatterAPI.register(StoneType.class, this);
    }

    @Override
    public void onRegistryBuild(String currentDomain, IForgeRegistry<?> registry) {
        if (!this.domain.equals(currentDomain) || !generateBlock || registry != null) return;
        this.setState(new BlockStone(this));
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
