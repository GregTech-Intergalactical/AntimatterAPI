package muramasa.gregtech.api.pipe;

import net.minecraft.util.math.AxisAlignedBB;

import java.util.Locale;

public enum PipeSize {

    VTINY(1),
    TINY(2),
    SMALL(4),
    NORMAL(8),
    LARGE(12),
    HUGE(16);

    public static PipeSize[] VALUES;

    static {
        VALUES = values();
    }

    private int cableThickness;
    private AxisAlignedBB AABB;

    PipeSize(int cableThickness) {
        this.cableThickness = cableThickness;
        float offset = 0.0625f * ordinal();
        AABB = new AxisAlignedBB(0.4375 - offset, 0.4375 - offset, 0.4375 - offset, 0.5625 + offset, 0.5625 + offset, 0.5625 + offset);
    }

    public String getName() {
        return name().toLowerCase(Locale.ENGLISH);
    }

    public String getDisplayName() {
        return getName();
    }

    public int getCableThickness() {
        return cableThickness;
    }

    public AxisAlignedBB getAABB() {
        return AABB;
    }
}
