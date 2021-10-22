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
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import java.util.Collections;

import static muramasa.antimatter.Data.*;
import static muramasa.antimatter.machine.MachineFlag.*;

public class TileEntityHatch<T extends TileEntityHatch<T>> extends TileEntityMachine<T> implements IComponent {

    private final LazyOptional<HatchComponentHandler<T>> componentHandler = LazyOptional.of(() -> new HatchComponentHandler(this));

    public TileEntityHatch(Machine<?> type) {
        super(type);
        if (type.has(ENERGY)) {
            energyHandler.set(() -> new MachineEnergyHandler<T>((T) this, 0, getMachineTier().getVoltage() * 66L, type.getOutputCover() == COVERENERGY ? tier.getVoltage() : 0, type.getOutputCover() == COVERDYNAMO ? tier.getVoltage() : 0,
                    type.getOutputCover() == COVERENERGY ? 2 : 0, type.getOutputCover() == COVERDYNAMO ? 1 : 0) {
                @Override
                public boolean canInput(Direction direction) {
                    Direction out = tile.coverHandler.map(MachineCoverHandler::getOutputFacing).orElse(null);
                    if (out == null) return false;
                    ICover o = tile.getMachineType().getOutputCover();
                    return o.equals(COVERENERGY) && direction == out;
                }

                @Override
                public boolean canOutput(Direction direction) {
                    Direction out = tile.coverHandler.map(MachineCoverHandler::getOutputFacing).orElse(null);
                    if (out == null) return false;
                    ICover o = tile.getMachineType().getOutputCover();
                    return o.equals(COVERDYNAMO) && direction == out;
                }
            });
        }
    }

    @Override
    public LazyOptional<HatchComponentHandler<T>> getComponentHandler() {
        return componentHandler;
    }

    @Override
    public void onMachineEvent(IMachineEvent event, Object... data) {
        if (isClientSide()) return;
        super.onMachineEvent(event, data);
        if (event instanceof ContentEvent) {
            componentHandler.map(ComponentHandler::getControllers).orElse(Collections.emptyList()).forEach(controller -> {
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
            componentHandler.map(ComponentHandler::getControllers).orElse(Collections.emptyList()).forEach(controller -> {
                switch ((MachineEvent) event) {
                    //Forward energy event to controller.
                    case ENERGY_DRAINED:
                    case ENERGY_INPUTTED:
                        controller.onMachineEvent(event, data);
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

    @Override
    public ResourceLocation getGuiTexture() {
        return new ResourceLocation(getMachineType().getDomain(), "textures/gui/machine/hatch.png");
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        if (cap == AntimatterCaps.COMPONENT_HANDLER_CAPABILITY) return componentHandler.cast();
        return super.getCapability(cap, side);
    }
}
