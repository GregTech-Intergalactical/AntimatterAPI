package muramasa.antimatter.cover;

import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import tesseract.api.IRefreshable;

public class CoverEnergy extends BaseCover implements IRefreshableCover{

    public CoverEnergy() {
        super();
        register();
    }
    @Override
    public String getId() {
        return "energy";
    }
    @Override
    public ResourceLocation getModel(Direction dir, Direction facing) {
        return getBasicModel();
    }

    @Override
    public void onRemove(CoverStack<?> instance, Direction side) {
        super.onRemove(instance,side);
    //    instance.getTile().getCapability(AntimatterCaps.ENERGY_HANDLER_CAPABILITY).ifPresent(IGTNode::refreshNet);
    }

    @Override
    public void onPlace(CoverStack<?> instance, Direction side) {
        super.onPlace(instance, side);
   //     instance.getTile().getCapability(AntimatterCaps.ENERGY_HANDLER_CAPABILITY).ifPresent(IGTNode::refreshNet);
    }

    @Override
    public void refresh(CoverStack<?> instance) {
        if (instance.getTile() instanceof TileEntityMachine) {
            ((TileEntityMachine)instance.getTile()).energyHandler.ifPresent(IRefreshable::refreshNet);
        }
    }
}
