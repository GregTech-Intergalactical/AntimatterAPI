package muramasa.antimatter.cover;

import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.machine.Tier;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

//Behaves like CoverOutput in terms of refresh but no event handler.
public class CoverInput extends BaseCover {

    public CoverInput(ICoverHandler<?> source, @Nullable Tier tier, Direction side, CoverFactory factory) {
        super(source, tier, side, factory);
    }

    @Override
    public ResourceLocation getModel(String type, Direction dir) {
        if (type.equals("pipe"))
            return PIPE_COVER_MODEL;
        return getBasicDepthModel();
    }

    @Override
    public void onPlace() {
        super.onPlace();
    }
}
