package muramasa.antimatter.ore;

import muramasa.antimatter.block.BlockStone;
import muramasa.antimatter.block.BlockStoneSlab;
import muramasa.antimatter.block.BlockStoneStair;
import muramasa.antimatter.block.BlockStoneWall;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.registration.RegistryType;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

public class VanillaStoneType extends CobbleStoneType{
    private final Texture vanillaTexture;
    public VanillaStoneType(String domain, String id, Material material, String beginningPath, Texture vanillaTexture, SoundType soundType, boolean generateBlock) {
        super(domain, id, material, beginningPath, soundType, generateBlock);
        this.vanillaTexture = vanillaTexture;
    }

    @Override
    public void onRegistryBuild(RegistryType registry) {

        if (registry == RegistryType.BLOCKS) {
            if (this.getId().equals("basalt")){
                for (int i = 0; i < SUFFIXES.length; i++) {
                    Block stone;
                    if (i == 7) {
                        stone = this.getState().getBlock();
                    } else if (i == 6){
                        stone = AntimatterPlatformUtils.getBlockFromId("minecraft", "smooth_" + this.getId());
                    }else {
                        stone = new BlockStone(this, SUFFIXES[i]);
                    }
                    blocks.put(SUFFIXES[i], stone);
                    if (i < 2){
                        continue;
                    }
                    int i2 = i - 2;
                    blocks.put(SLAB_SUFFIXES[i2], new BlockStoneSlab(this, SUFFIXES[i]));
                    blocks.put(STAIR_SUFFIXES[i2], new BlockStoneStair(this, SUFFIXES[i], stone));
                    blocks.put(WALL_SUFFIXES[i2], new BlockStoneWall(this, SUFFIXES[i]));
                }
                return;
            }
            for (int i = 0; i < SUFFIXES.length; i++) {
                int i2 = i - 2;
                Block stone, stair = null, slab = null, wall = null;
                if (i == 7) {
                    stone = this.getState().getBlock();
                    slab = AntimatterPlatformUtils.getBlockFromId("minecraft", this.getId() + "_slab");
                    stair = AntimatterPlatformUtils.getBlockFromId("minecraft", this.getId() + "_stairs");
                    wall = AntimatterPlatformUtils.getBlockFromId("minecraft", this.getId() + "_wall");
                } else if (i == 6){
                    stone = AntimatterPlatformUtils.getBlockFromId("minecraft", "polished_" + this.getId());
                    slab = AntimatterPlatformUtils.getBlockFromId("minecraft", "polished_" + this.getId() + "_slab");
                    stair = AntimatterPlatformUtils.getBlockFromId("minecraft", "polished_" + this.getId() + "_stairs");
                    wall = new BlockStoneWall(this, SUFFIXES[i2]);
                } else {
                    stone = new BlockStone(this, SUFFIXES[i]);
                    if (i >= 2) {
                        slab = new BlockStoneSlab(this, SUFFIXES[i2]);
                        stair = new BlockStoneStair(this, SUFFIXES[i2], stone);
                        wall = new BlockStoneWall(this, SUFFIXES[i2]);
                    }
                }
                blocks.put(SUFFIXES[i], stone);
                if (i < 2){
                    continue;
                }
                blocks.put(SLAB_SUFFIXES[i2], slab);
                blocks.put(STAIR_SUFFIXES[i2], stair);
                blocks.put(WALL_SUFFIXES[i2], wall);
            }
        }
    }

    @Override
    public Texture getTexture() {
        return vanillaTexture;
    }
}
