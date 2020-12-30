package muramasa.antimatter.cover;

import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import tesseract.api.IRefreshable;

//Behaves like CoverOutput in terms of refresh but no event handler.
public class CoverInput extends Cover {

    public CoverInput() {
        register();
    }
    @Override
    public String getId() {
        return "input";
    }

    @Override
    public ResourceLocation getModel(Direction dir, Direction facing) {
        return getBasicModel();
    }

    @Override
    public void onPlace(CoverStack<?> instance, Direction side) {
        super.onPlace(instance, side);
        refresh(instance);
    }

    @Override
    public void onRemove(CoverStack<?> instance, Direction side) {
        super.onRemove(instance,side);
        refresh(instance);
    }

    protected void refresh(CoverStack<?> instance) {
        instance.getTile().getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(t -> {
            if (t instanceof IRefreshable) {
                ((IRefreshable)t).refreshNet();
            }
        });
        instance.getTile().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(t -> {
            if (t instanceof IRefreshable) {
                ((IRefreshable)t).refreshNet();
            }
        });
    }
}
