package muramasa.gregtech.api.texture;

import muramasa.gregtech.Ref;
import net.minecraft.util.ResourceLocation;

public class Texture {

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
}
