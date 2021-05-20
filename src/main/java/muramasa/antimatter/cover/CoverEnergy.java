package muramasa.antimatter.cover;

import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.util.Direction;
import tesseract.api.capability.TesseractGTCapability;
import tesseract.api.gt.IGTNode;

public class CoverEnergy extends BaseCover{

    public CoverEnergy() {
        super();
        register();
    }
    @Override
    public String getId() {
        return "energy";
    }
  //  @Override
   // public ResourceLocation getModel(Direction dir, Direction facing) {
//        return getBasicModel();
//    }

    @Override
    public void onPlace(CoverStack<?> instance, Direction side) {
        super.onPlace(instance, side);
        ((TileEntityMachine<?>)instance.getTile()).invalidateCap(TesseractGTCapability.ENERGY_HANDLER_CAPABILITY);
    }
}
