package muramasa.gregtech.api.pipe;

import net.minecraft.util.math.AxisAlignedBB;

import java.util.Locale;

public enum PipeSize {

    VTINY(),
    TINY(),
    SMALL(),
    NORMAL(),
    LARGE(),
    HUGE();

    public static PipeSize[] VALUES;

    static {
        VALUES = values();
    }

    private AxisAlignedBB AABB;

    PipeSize() {
        float offset = 0.0625f * ordinal();
        AABB = new AxisAlignedBB(0.4375 - offset, 0.4375 - offset, 0.4375 - offset, 0.5625 + offset, 0.5625 + offset, 0.5625 + offset);
    }

    public String getName() {
        return name().toLowerCase(Locale.ENGLISH);
    }

    public String getDisplayName() {
        return getName();
    }

    public AxisAlignedBB getAABB() {
        return AABB;
    }
}
