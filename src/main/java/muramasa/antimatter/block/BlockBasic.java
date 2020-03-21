package muramasa.antimatter.block;

import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.IModelProvider;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockBasic extends Block implements IAntimatterObject, ITextureProvider, IModelProvider {

    protected String domain, id;
    protected Texture[] textures;

    public BlockBasic(String domain, String id, Block.Properties properties, Texture... textures) {
        super(properties);
        this.domain = domain;
        this.id = id;
        this.textures = textures;
        setRegistryName(domain, id);
    }

    public BlockBasic(String namespace, String id, Texture... textures) {
        this(namespace, id, Block.Properties.create(Material.IRON).hardnessAndResistance(1.0f, 1.0f).sound(SoundType.STONE), textures);
    }

    public String getDomain() {
        return domain;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Texture[] getTextures() {
        return textures;
    }
}
