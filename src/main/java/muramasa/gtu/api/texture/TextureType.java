package muramasa.gtu.api.texture;

import muramasa.gtu.api.registration.IGregTechObject;

import java.util.Locale;

public enum TextureType implements IGregTechObject {

    BASE,
    TOP,
    BOTTOM,
    FRONT,
    BACK,
    SIDE;

    public String getId() {
        return name().toLowerCase(Locale.ENGLISH);
    }
}
