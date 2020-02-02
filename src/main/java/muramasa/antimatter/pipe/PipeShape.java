package muramasa.antimatter.pipe;

import muramasa.antimatter.registration.IAntimatterObject;

import java.util.Locale;

public enum PipeShape implements IAntimatterObject {

    ALL,
    ARROW,
    BASE,
    CORNER,
    CROSS,
    ELBOW,
    FIVE,
    LINE,
    SIDE,
    SINGLE;

    public static final PipeShape[] VALUES = values();

    @Override
    public String getId() {
        return name().toLowerCase(Locale.ENGLISH);
    }
}
