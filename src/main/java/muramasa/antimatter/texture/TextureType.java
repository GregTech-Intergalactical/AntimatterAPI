package muramasa.antimatter.texture;

import muramasa.antimatter.registration.IGregTechObject;

import java.util.Locale;

public enum TextureType implements IGregTechObject {

    TOP,
    BOTTOM,
    FRONT,
    BACK,
    SIDE;

    public String getId() {
        return name().toLowerCase(Locale.ENGLISH);
    }
}
