package muramasa.antimatter.cover;

import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.machine.MachineEnergyHandler;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import tesseract.api.gt.IGTNode;

public class CoverDynamo extends Cover{

    public CoverDynamo(String id) {
        super(id);
        register();
    }
    @Override
    public String getId() {
        return id;
    }

    @Override
    public ResourceLocation getModel(Direction dir, Direction facing) {
        return getBasicModel();
    }

    @Override
    public void onRemove(CoverStack<?> instance, Direction side) {
        super.onRemove(instance,side);
        instance.getTile().getCapability(AntimatterCaps.ENERGY_HANDLER_CAPABILITY).ifPresent(IGTNode::refreshNet);
    }

    @Override
    public void onPlace(CoverStack<?> instance, Direction side) {
        super.onPlace(instance, side);
        instance.getTile().getCapability(AntimatterCaps.ENERGY_HANDLER_CAPABILITY).ifPresent(IGTNode::refreshNet);
    }
}
