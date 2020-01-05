package muramasa.antimatter.blocks;

import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.IModelProvider;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public abstract class BlockBasic extends Block implements IAntimatterObject, ITextureProvider, IModelProvider {

    protected Texture[] textures;

    public BlockBasic(Block.Properties properties, Texture... textures) {
        super(properties);
        this.textures = textures;
    }

    public BlockBasic() {
        this(Block.Properties.create(Material.IRON).hardnessAndResistance(1.0f, 1.0f).sound(SoundType.STONE));
    }

    @Override
    public Texture[] getTextures() {
        return textures;
    }
}
