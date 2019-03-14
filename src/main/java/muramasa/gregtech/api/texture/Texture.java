package muramasa.gregtech.api.texture;

import muramasa.gregtech.Ref;
import muramasa.gregtech.api.data.Machines;
import muramasa.gregtech.api.machines.MachineState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

public class Texture {

    private static Texture ERROR = Machines.INVALID.getOverlayTextures(MachineState.IDLE)[2];

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
