package muramasa.antimatter.cover;

import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

//Behaves like CoverOutput in terms of refresh but no event handler.
public class CoverInput extends BaseCover{

    public CoverInput() {
        register();
    }
    @Override
    public String getId() {
        return "input";
    }

    @Override
    public ResourceLocation getModel(Direction dir, Direction facing) {
        return getBasicDepthModel();
    }

    @Override
    public void onPlace(CoverStack<?> instance, Direction side) {
        super.onPlace(instance, side);
    }
}
