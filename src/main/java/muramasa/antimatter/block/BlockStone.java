package muramasa.antimatter.block;

import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.texture.Texture;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockStone extends BlockBasic {

    protected StoneType type;

    public BlockStone(StoneType type) {
        super(type.getDomain(), type.getId(), Block.Properties.create(Material.ROCK).sound(type.getSoundType()));
        this.type = type;
    }

    public StoneType getType() {
        return type;
    }

    @Override
    public Texture[] getTextures() {
        return new Texture[]{type.getTexture()};
    }
}
