package muramasa.antimatter.tile.multi;

import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.ComponentHandler;
import muramasa.antimatter.capability.machine.HatchComponentHandler;
import muramasa.antimatter.capability.machine.MachineCoverHandler;
import muramasa.antimatter.capability.machine.MachineEnergyHandler;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.machine.event.MachineEvent;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.structure.IComponent;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.LazyHolder;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import tesseract.util.Dir;

import javax.annotation.Nonnull;
import java.util.Optional;

import static muramasa.antimatter.Data.*;
import static muramasa.antimatter.machine.MachineFlag.*;

//TODO: HATCH SHOULD NOT HAVE TWO OUTPUTS!
public class TileEntityHatch extends TileEntityMachine implements IComponent {

    private final LazyOptional<HatchComponentHandler> componentHandler = LazyOptional.of(() -> new HatchComponentHandler(this));

    public TileEntityHatch(Machine<?> type) {
        super(type);
        this.energyHandler = type.has(ENERGY) ? LazyOptional.of(() -> new MachineEnergyHandler<TileEntityHatch>(this, 0,getMachineTier().getVoltage() * 66L, type.getOutputCover() == COVERENERGY ? tier.getVoltage() : 0,type.getOutputCover() == COVERDYNAMO ? tier.getVoltage() : 0,
                type.getOutputCover() == COVERENERGY ? 2 : 0,type.getOutputCover() == COVERDYNAMO ? 1 : 0){
            @Override
            public boolean canInput(Dir direction) {
                Direction out = tile.coverHandler.map(MachineCoverHandler::getOutputFacing).orElse(null);
                if (out == null) return false;
                ICover o = tile.getMachineType().getOutputCover();
                return o.equals(COVERENERGY) && direction.getIndex() == out.getIndex();
            }

            @Override
            public boolean canOutput(Dir direction) {
                Direction out = tile.coverHandler.map(MachineCoverHandler::getOutputFacing).orElse(null);
                if (out == null) return false;
                ICover o = tile.getMachineType().getOutputCover();
                return o.equals(COVERDYNAMO) && direction.getIndex() == out.getIndex();
            }

            @Override
            public boolean connects(Dir direction) {
                return true;
            }
        }) : LazyOptional.empty();
}

    @Override
    public LazyOptional<HatchComponentHandler> getComponentHandler() {
        return componentHandler;
    }

    @Override
    public void onMachineEvent(IMachineEvent event, Object... data) {
        if (isClientSide()) return;
        super.onMachineEvent(event,data);
        if (event instanceof ContentEvent) {
            componentHandler.map(ComponentHandler::getFirstController).orElse(Optional.empty()).ifPresent(controller -> {
                switch ((ContentEvent) event) {
                    case ITEM_INPUT_CHANGED:
                    case ITEM_OUTPUT_CHANGED:
                    case ITEM_CELL_CHANGED:
                    case FLUID_INPUT_CHANGED:
                    case FLUID_OUTPUT_CHANGED:
                        controller.onMachineEvent(event, data);
                        break;
                }
            });
        } else if (event instanceof MachineEvent) {
            componentHandler.map(ComponentHandler::getFirstController).ifPresent(controller -> {
                switch ((MachineEvent)event) {
                    //Forward energy event to controller.
                    case ENERGY_DRAINED:
                    case ENERGY_INPUTTED:
                        controller.ifPresent(c -> c.onMachineEvent(event, data));
                        break;
                }
            });
        }
    }

    @Override
    public void onFirstTick() {
        super.onFirstTick();
        coverHandler.ifPresent(t -> {
            COVEROUTPUT.setEjects(t.get(t.getOutputFacing()), has(FLUID), has(ITEM));
        });
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        if (cap == AntimatterCaps.COMPONENT_HANDLER_CAPABILITY) return componentHandler.cast();
        return super.getCapability(cap, side);
    }
}
