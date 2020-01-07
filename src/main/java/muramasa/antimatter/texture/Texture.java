package muramasa.antimatter.texture;

import muramasa.antimatter.client.ModelUtils;
import muramasa.gtu.Ref;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

public class Texture extends ResourceLocation {

    public Texture(String domain, String path) {
        super(domain, path);
    }

    public Texture(String path) {
        this(Ref.MODID, path);
    }

    public TextureAtlasSprite getSprite() {
        return ModelUtils.getSprite(this);
    }
}
