package muramasa.gtu.api.texture;

import muramasa.gtu.Ref;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

public class Texture {

    public static final Texture ERROR = new Texture("blocks/machine/overlay/invalid/front");

    private ResourceLocation loc;

    public Texture(ResourceLocation loc) {
        this.loc = loc;
    }

    public Texture(String domain, String path) {
        loc = new ResourceLocation(domain, path);
    }

    public Texture(String path) {
        this(Ref.MODID, path);
    }

    public ResourceLocation getLoc() {
        return loc;
    }

    //TODO evaluate if needed
    public void setEmpty() {
        loc = new ResourceLocation(Ref.MODID, "blocks/machine/empty");
    }

    public TextureAtlasSprite getSprite() {
        TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(loc.toString());
        return sprite != null ? sprite : Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(ERROR.getLoc().toString());
    }
}
