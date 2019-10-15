package muramasa.gtu.api.texture;

import muramasa.gtu.Ref;
import net.minecraft.client.Minecraft;
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
        return Minecraft.getInstance().getTextureMap().getSprite(this);
    }
}
