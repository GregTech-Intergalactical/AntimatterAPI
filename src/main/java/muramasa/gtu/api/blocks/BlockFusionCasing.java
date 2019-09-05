package muramasa.gtu.api.blocks;

import muramasa.gtu.api.texture.Texture;

public class BlockFusionCasing extends BlockCasing {

    private Texture[] textures;

    public BlockFusionCasing(String type, Texture[] textures) {
        super(type);
        this.textures = textures;
    }

    @Override
    public void onConfig() {
        buildBasicConfig(textures);
    }
}
