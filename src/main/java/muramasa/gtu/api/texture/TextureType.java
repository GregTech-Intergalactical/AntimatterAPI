package muramasa.gtu.api.texture;

import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum TextureType implements IStringSerializable {

    BASE,
    TOP,
    BOTTOM,
    FRONT,
    BACK,
    SIDE;

    public String getName() {
        return name().toLowerCase(Locale.ENGLISH);
    }
}
