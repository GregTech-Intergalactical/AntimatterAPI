package muramasa.antimatter.cover;

import net.minecraft.util.Direction;
import tesseract.api.capability.TesseractGTCapability;
import tesseract.api.gt.IGTNode;

public class CoverDynamo extends BaseCover {

    public CoverDynamo(String id) {
        super(id);
        register();
    }
    @Override
    public String getId() {
        return id;
    }

    @Override
    protected String getRenderId() {
        return "dynamo";
    }

    //@Override
    //public ResourceLocation getModel(Direction dir, Direction facing) {
    //    return getBasicModel();
    //}

    @Override
    public void onPlace(CoverStack<?> instance, Direction side) {
        super.onPlace(instance, side);
        instance.getTile().getCapability(TesseractGTCapability.ENERGY_HANDLER_CAPABILITY).ifPresent(IGTNode::refreshNet);
    }
}
