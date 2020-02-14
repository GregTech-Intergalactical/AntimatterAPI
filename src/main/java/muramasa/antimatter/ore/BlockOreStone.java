package muramasa.antimatter.ore;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.blocks.BlockBasic;
import muramasa.antimatter.materials.Material;
import muramasa.antimatter.texture.Texture;

public class BlockOreStone extends BlockBasic {

    private Material material;

    public BlockOreStone(String domain, Material material, Texture... textures) {
        super(domain, "stone_" + material.getId(), textures);
        this.material = material;
        AntimatterAPI.register(BlockOreStone.class, this);
    }

    public Material getMaterial() {
        return material;
    }

    @Override
    public Texture[] getTextures() {
        return textures == null || textures.length == 0 ? new Texture[]{new Texture(domain, "block/stone/" + material.getId())} : textures;
    }
}
