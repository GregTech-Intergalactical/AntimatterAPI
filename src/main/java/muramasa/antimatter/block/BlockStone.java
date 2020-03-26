package muramasa.antimatter.block;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.texture.Texture;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.ModLoadingContext;

public class BlockStone extends BlockBasic {

    protected StoneType type;

    public BlockStone(StoneType type) {
        super(Block.Properties.create(Material.ROCK).sound(type.getSoundType()));
        this.type = type;
        setRegistryName(ModLoadingContext.get().getActiveNamespace(), type.getId());
        AntimatterAPI.register(BlockStone.class, this);
    }

    public StoneType getType() {
        return type;
    }

    @Override
    public Texture[] getTextures() {
        return new Texture[]{type.getTexture()};
    }
}
