package muramasa.antimatter.ore;

import muramasa.antimatter.block.BlockStone;
import muramasa.antimatter.block.BlockStoneSlab;
import muramasa.antimatter.block.BlockStoneStair;
import muramasa.antimatter.block.BlockStoneWall;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.texture.Texture;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CobbleStoneType extends StoneType {
    String beginningPath;
    Map<String, Block> blocks = new LinkedHashMap<>();
    public static final String[] SUFFIXES = {"bricks_chiseled", "bricks_cracked", "bricks_mossy", "cobble_mossy", "bricks", "cobble", "smooth", "", "bricks_mossy_slab", "cobble_mossy_slab", "bricks_slab", "cobble_slab", "smooth_slab", "slab", "bricks_mossy_stairs", "cobble_mossy_stairs", "bricks_stairs", "cobble_stairs", "smooth_stairs", "stairs", "bricks_mossy_wall", "cobble_mossy_wall", "bricks_wall", "cobble_wall", "smooth_wall", "wall"};
    public CobbleStoneType(String domain, String id, Material material, String beginningPath, SoundType soundType, boolean generateBlock) {
        super(domain, id, material, new Texture(domain, beginningPath  + id + "/stone"), soundType, generateBlock);
        this.beginningPath = beginningPath;
    }

    @Override
    public void onRegistryBuild(IForgeRegistry<?> registry) {
        if (registry == ForgeRegistries.BLOCKS){
            if (generateBlock){
                for (int i = 0; i < SUFFIXES.length; i++){
                    if (i == 7){
                        setState(new BlockStone(this));
                        blocks.put(SUFFIXES[i], this.getState().getBlock());
                        continue;
                    }
                    if (i > 7 && i < 14){
                        blocks.put(SUFFIXES[i], new BlockStoneSlab(this, SUFFIXES[i - 6]));
                        continue;
                    }
                    if (i > 13 && i < 20){
                        blocks.put(SUFFIXES[i], new BlockStoneStair(this, SUFFIXES[i - 12], blocks.get(SUFFIXES[i - 8])));
                        continue;
                    }
                    if (i > 19){
                        blocks.put(SUFFIXES[i], new BlockStoneWall(this, SUFFIXES[i - 18]));
                        continue;
                    }
                    blocks.put(SUFFIXES[i], new BlockStone(this, SUFFIXES[i]));
                }
            }
        }
    }

    public String getBeginningPath() {
        return beginningPath;
    }

    public Block getBlock(String name) {
        return blocks.get(name);
    }
}
