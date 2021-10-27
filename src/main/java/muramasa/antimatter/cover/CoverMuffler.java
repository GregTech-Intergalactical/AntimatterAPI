package muramasa.antimatter.cover;

import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.machine.Tier;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class CoverMuffler extends BaseCover {

    public CoverMuffler(ICoverHandler<?> source, @Nullable Tier tier, Direction side, CoverFactory factory) {
        super(source, tier, side, factory);
    }

    @Override
    public String getId() {
        return "muffler";
    }

    @Override
    public ResourceLocation getModel(String type, Direction dir, Direction facing) {
        if (type.equals("pipe"))
            return PIPE_COVER_MODEL;
        return getBasicModel();
    }

    @Override
    public boolean ticks() {
        return false;
    }
}
