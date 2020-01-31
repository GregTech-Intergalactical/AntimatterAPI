package muramasa.antimatter.texture;

import muramasa.antimatter.registration.IAntimatterObject;

import java.util.Locale;

@Deprecated
public enum TextureType implements IAntimatterObject {

    TOP,
    BOTTOM,
    FRONT,
    BACK,
    SIDE;

    public String getId() {
        return name().toLowerCase(Locale.ENGLISH);
    }

    @Override
    public String toString() {
        return getId();
    }
}
