package muramasa.antimatter.block;

import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.IModelProvider;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockBasic extends Block implements IAntimatterObject, ITextureProvider, IModelProvider {

    public BlockBasic(Block.Properties properties) {
        super(properties);
    }

    public BlockBasic() {
        this(Block.Properties.create(Material.IRON).hardnessAndResistance(1.0f, 1.0f).sound(SoundType.STONE));
    }

    @Override
    public Texture[] getTextures() {
        return new Texture[0];
    }
}
