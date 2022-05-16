package muramasa.antimatter.ore;

import muramasa.antimatter.block.BlockStone;
import muramasa.antimatter.block.BlockStoneSlab;
import muramasa.antimatter.block.BlockStoneStair;
import muramasa.antimatter.block.BlockStoneWall;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.registration.RegistryType;
import muramasa.antimatter.texture.Texture;
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
            for (int i = 0; i < SUFFIXES.length; i++) {
                if (i == 7) {
                    blocks.put(SUFFIXES[i], this.getState().getBlock());
                    continue;
                }
                if (i > 7 && i < 14) {
                    if (i == 12){
                        blocks.put(SUFFIXES[i], AntimatterPlatformUtils.getBlockFromId("minecraft", "polished_" + this.getId() + "_slab"));
                        continue;
                    }
                    if (i == 13){
                        blocks.put(SUFFIXES[i], AntimatterPlatformUtils.getBlockFromId("minecraft", this.getId() + "_slab"));
                        continue;
                    }
                    blocks.put(SUFFIXES[i], new BlockStoneSlab(this, SUFFIXES[i - 6]));
                    continue;
                }
                if (i > 13 && i < 20) {
                    if (i == 18){
                        blocks.put(SUFFIXES[i], AntimatterPlatformUtils.getBlockFromId("minecraft", "polished_" + this.getId() + "_stairs"));
                        continue;
                    }
                    if (i == 19){
                        blocks.put(SUFFIXES[i], AntimatterPlatformUtils.getBlockFromId("minecraft", this.getId() + "_stairs"));
                        continue;
                    }
                    blocks.put(SUFFIXES[i], new BlockStoneStair(this, SUFFIXES[i - 12], blocks.get(SUFFIXES[i - 8])));
                    continue;
                }
                if (i > 19) {

                    if (i == 25){
                        blocks.put(SUFFIXES[i], AntimatterPlatformUtils.getBlockFromId("minecraft", this.getId() + "_wall"));
                        continue;
                    }
                    blocks.put(SUFFIXES[i], new BlockStoneWall(this, SUFFIXES[i - 18]));
                    continue;
                }
                if (i == 6){
                    blocks.put(SUFFIXES[i], AntimatterPlatformUtils.getBlockFromId("minecraft", "polished_" + this.getId()));
                    continue;
                }
                blocks.put(SUFFIXES[i], new BlockStone(this, SUFFIXES[i]));
            }
        }
    }

    @Override
    public Texture getTexture() {
        return vanillaTexture;
    }
}
