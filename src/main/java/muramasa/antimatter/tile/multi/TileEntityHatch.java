package muramasa.antimatter.tile.multi;

import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.ComponentHandler;
import muramasa.antimatter.capability.machine.HatchComponentHandler;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.structure.IComponent;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import java.util.Optional;

public class TileEntityHatch extends TileEntityMachine implements IComponent {

    private final LazyOptional<HatchComponentHandler> componentHandler = LazyOptional.of(() -> new HatchComponentHandler(this));

    public TileEntityHatch(Machine<?> type) {
        super(type);
    }

    @Override
    public LazyOptional<HatchComponentHandler> getComponentHandler() {
        return componentHandler;
    }

    @Override
    public void onMachineEvent(IMachineEvent event, Object... data) {
        if (event instanceof ContentEvent) {
            componentHandler.map(ComponentHandler::getFirstController).orElse(Optional.empty()).ifPresent(controller -> {
                switch ((ContentEvent) event) {
                    case ITEM_INPUT_CHANGED:
                        controller.onMachineEvent(event, data);
                        break;
                    case ITEM_OUTPUT_CHANGED:
                        controller.onMachineEvent(event, data);
                    case ITEM_CELL_CHANGED:
                        //TODO handle cells
                        break;
                    case FLUID_INPUT_CHANGED:
                        //TODO
                        break;
                }
            });
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        if (cap == AntimatterCaps.COMPONENT_HANDLER_CAPABILITY) return componentHandler.cast();
        return super.getCapability(cap, side);
    }
}
