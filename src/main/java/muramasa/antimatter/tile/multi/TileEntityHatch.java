package muramasa.antimatter.tile.multi;

import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.ComponentHandler;
import muramasa.antimatter.capability.machine.*;
import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.machine.MachineFlag;
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

import static muramasa.antimatter.Data.COVERDYNAMO;
import static muramasa.antimatter.Data.COVERENERGY;
import static muramasa.antimatter.machine.MachineFlag.*;
import static muramasa.antimatter.machine.MachineFlag.GENERATOR;

//TODO: HATCH SHOULD NOT HAVE TWO OUTPUTS!
public class
TileEntityHatch extends TileEntityMachine implements IComponent {

    private final LazyOptional<HatchComponentHandler> componentHandler = LazyOptional.of(() -> new HatchComponentHandler(this));

    public TileEntityHatch(Machine<?> type) {
        super(type);
       // this.itemHandler = type.has(ITEM) ? LazyHolder.of(() -> new MachineItemHandler<>(this)) : LazyHolder.empty();
      //  this.fluidHandler = type.has(FLUID) ? LazyHolder.of(() -> new MachineFluidHandler<>(this)) : LazyHolder.empty();
        //T tile, long energy, long capacity, int voltageIn, int voltageOut, int amperageIn, int amperageOut
        this.energyHandler = type.has(ENERGY) ? LazyHolder.of(() -> new MachineEnergyHandler<TileEntityHatch>(this, 0,getMachineTier().getVoltage() * 66L, type.getOutputCover() == COVERENERGY ? tier.getVoltage() : 0,type.getOutputCover() == COVERDYNAMO ? tier.getVoltage() : 0,
                type.getOutputCover() == COVERENERGY ? 2 : 0,type.getOutputCover() == COVERDYNAMO ? 1 : 0){
            @Override
            public boolean canInput(Dir direction) {
                Direction out = tile.coverHandler.map(MachineCoverHandler::getOutputFacing).orElse(null);
                if (out == null) return false;
                Cover o = tile.getMachineType().getOutputCover();
                return o.equals(COVERENERGY) && direction.getIndex() == out.getIndex();
            }

            @Override
            public boolean canOutput(Dir direction) {
                Direction out = tile.coverHandler.map(MachineCoverHandler::getOutputFacing).orElse(null);
                if (out == null) return false;
                Cover o = tile.getMachineType().getOutputCover();
                return o.equals(COVERDYNAMO) && direction.getIndex() == out.getIndex();
            }

            @Override
            public boolean connects(Dir direction) {
                return true;
            }
        }) : LazyHolder.empty();
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

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        if (cap == AntimatterCaps.COMPONENT_HANDLER_CAPABILITY) return componentHandler.cast();
        return super.getCapability(cap, side);
    }
}
