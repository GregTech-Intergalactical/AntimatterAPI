package muramasa.gtu.api.blocks;

import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.registration.IGregTechObject;
import muramasa.gtu.api.registration.IModelOverride;
import muramasa.gtu.api.texture.Texture;
import net.minecraft.block.Block;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

//TODO probably very future, but add system to build dynamic baked models from
//TODO a TextureData object.
//TODO Maybe some base class could have a overridable String getVariant method
//TODO for automatic basic model injection
//TODO allow to specify textures by passing TextureData
public abstract class BlockBaked extends Block implements IGregTechObject, IModelOverride {

    protected Set<Texture> TEXTURES = new HashSet<>();

    public BlockBaked(net.minecraft.block.material.Material material) {
        super(material);
        GregTechAPI.register(BlockBaked.class, this);
    }

    public Collection<Texture> getTextures() {
        return TEXTURES;
    }
}
