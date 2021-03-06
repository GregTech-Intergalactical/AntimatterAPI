package muramasa.antimatter.texture;

import muramasa.antimatter.client.ModelUtils;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

public class Texture extends ResourceLocation {

    public Texture(String domain, String path) {
        super(domain, path);
    }

    public Texture(String name) {
        super(name);
    }

    public RenderMaterial asMaterial() {
        return ModelUtils.getBlockMaterial(this);
    }

    public TextureAtlasSprite asSprite() {
        return ModelUtils.getSprite(this);
    }
}
