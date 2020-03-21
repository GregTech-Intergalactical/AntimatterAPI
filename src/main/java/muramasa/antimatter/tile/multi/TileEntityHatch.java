package muramasa.antimatter.tile.multi;

import muramasa.antimatter.capability.AntimatterCapabilities;
import muramasa.antimatter.capability.impl.HatchComponentHandler;
import muramasa.antimatter.capability.impl.MachineFluidHandler;
import muramasa.antimatter.machine.ContentEvent;
import muramasa.antimatter.structure.IComponent;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import java.util.Optional;

import static muramasa.antimatter.machine.MachineFlag.FLUID;

public class TileEntityHatch extends TileEntityMachine implements IComponent {

    protected Optional<HatchComponentHandler> componentHandler = Optional.empty();

    @Override
    public void onLoad() {
        super.onLoad();
        componentHandler = Optional.of(new HatchComponentHandler(this));
        if (getMachineType().hasFlag(FLUID)) fluidHandler = Optional.of(new MachineFluidHandler(this, 8000 * getTierId(), fluidData));
    }

    @Override
    public Optional<HatchComponentHandler> getComponentHandler() {
        return componentHandler;
    }

    @Override
    public void onContentsChanged(ContentEvent type, int slot) {
        componentHandler.ifPresent(h -> h.getFirstController().ifPresent(controller -> {
            switch (type) {
                case ITEM_INPUT:
                    controller.onContentsChanged(type, slot);
                    break;
                case ITEM_OUTPUT:
                    controller.onContentsChanged(type, slot);
                case ITEM_CELL:
                    //TODO handle cells
                    break;
                case FLUID_INPUT:
                    //TODO
                    break;
            }
        }));
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        if (cap == AntimatterCapabilities.COMPONENT && componentHandler.isPresent()) return LazyOptional.of(() -> componentHandler.get()).cast();
        return super.getCapability(cap, side);
    }
}
