package muramasa.antimatter.cover;

import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.machine.Tier;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public abstract class CoverTintable extends BaseCover {

    public CoverTintable(ICoverHandler<?> source, @Nullable Tier tier, Direction side, CoverFactory factory) {
        super(source, tier, side, factory);
    }

    public abstract int getRGB();
}
