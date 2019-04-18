package muramasa.gtu.api.texture;

import muramasa.gtu.api.interfaces.IGregTechObject;

import java.util.Locale;

public enum TextureType implements IGregTechObject {

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
