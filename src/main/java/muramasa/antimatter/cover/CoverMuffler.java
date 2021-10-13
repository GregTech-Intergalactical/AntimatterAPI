package muramasa.antimatter.cover;

import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

public class CoverMuffler extends BaseCover {

    public CoverMuffler() {
        register();
    }
    @Override
    public String getId() {
        return "muffler";
    }

    @Override
    public ResourceLocation getModel(String type, Direction dir, Direction facing) {
        if (type.equals("pipe")) return PIPE_COVER_MODEL;
        return getBasicModel();
    }

    @Override
    public boolean ticks() {
        return false;
    }
}
