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
    public ResourceLocation getModel(Direction dir, Direction facing) {
        return getBasicModel();
    }
}
