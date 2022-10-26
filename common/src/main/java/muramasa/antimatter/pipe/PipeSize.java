package muramasa.antimatter.pipe;

import muramasa.antimatter.registration.IAntimatterObject;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.phys.AABB;

import java.util.Locale;

public enum PipeSize implements IAntimatterObject {

    VTINY(1),
    TINY(2),
    SMALL(4),
    NORMAL(8),
    LARGE(12),
    HUGE(16);

    public static final PipeSize[] VALUES;

    static {
        VALUES = values();
    }

    private final int cableThickness;
    private final AABB AABB;

    PipeSize(int cableThickness) {
        this.cableThickness = cableThickness;
        float offset = 0.0625f * ordinal();
        AABB = new AABB(0.4375 - offset, 0.4375 - offset, 0.4375 - offset, 0.5625 + offset, 0.5625 + offset, 0.5625 + offset);
    }

    @Override
    public String getId() {
        return name().toLowerCase(Locale.ENGLISH);
    }

    public Component getDisplayName() {
        return new TranslatableComponent("pipe." + getId());
    }

    public int getCableThickness() {
        return cableThickness;
    }

    public AABB getAABB() {
        return AABB;
    }
}
