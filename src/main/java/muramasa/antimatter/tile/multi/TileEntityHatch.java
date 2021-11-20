package muramasa.antimatter.tile.multi;

import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.ComponentHandler;
import muramasa.antimatter.capability.machine.HatchComponentHandler;
import muramasa.antimatter.capability.machine.MachineCoverHandler;
import muramasa.antimatter.capability.machine.MachineEnergyHandler;
import muramasa.antimatter.cover.CoverOutput;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.machine.event.MachineEvent;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.structure.IComponent;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.Explosion;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import tesseract.api.gt.GTTransaction;

import javax.annotation.Nonnull;
import java.util.Collections;

import static muramasa.antimatter.Data.COVERDYNAMO;
import static muramasa.antimatter.Data.COVERENERGY;
import static muramasa.antimatter.machine.MachineFlag.*;

public class TileEntityHatch<T extends TileEntityHatch<T>> extends TileEntityMachine<T> implements IComponent {

    private final LazyOptional<HatchComponentHandler<T>> componentHandler = LazyOptional
            .of(() -> new HatchComponentHandler(this));

    public TileEntityHatch(Machine<?> type) {
        super(type);
        if (type.has(ENERGY)) {
            energyHandler.set(() -> new MachineEnergyHandler<T>((T) this, 0, getMachineTier().getVoltage() * 66L,
                    type.getOutputCover() == COVERENERGY ? tier.getVoltage() : 0,
                    type.getOutputCover() == COVERDYNAMO ? tier.getVoltage() : 0,
                    type.getOutputCover() == COVERENERGY ? 2 : 0, type.getOutputCover() == COVERDYNAMO ? 1 : 0) {
                @Override
                public boolean canInput(Direction direction) {
                    ICover out = tile.coverHandler.map(MachineCoverHandler::getOutputCover).orElse(null);
                    if (out == null)
                        return false;
                    return out.isEqual(COVERENERGY) && direction == out.side();
                }

                @Override
                protected boolean checkVoltage(GTTransaction.TransferData data) {
                    boolean flag = true;
                    if (type.getOutputCover() == COVERDYNAMO) {
                        flag = data.getVoltage() <= getOutputVoltage();
                    } else if (type.getOutputCover() == COVERENERGY) {
                        flag = data.getVoltage() <= getInputVoltage();
                    }
                    if (!flag) {
                        Utils.createExplosion(tile.getLevel(), tile.getBlockPos(), 4.0F, Explosion.Mode.BREAK);
                    }
                    return flag;
                }

                @Override
                public boolean canOutput(Direction direction) {
                    ICover out = tile.coverHandler.map(MachineCoverHandler::getOutputCover).orElse(null);
                    if (out == null)
                        return false;
                    return out.isEqual(COVERDYNAMO) && direction == out.side();
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
        if (isClientSide())
            return;
        super.onMachineEvent(event, data);
        if (event instanceof ContentEvent) {
            componentHandler.map(ComponentHandler::getControllers).orElse(Collections.emptyList())
                    .forEach(controller -> {
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
            componentHandler.map(ComponentHandler::getControllers).orElse(Collections.emptyList())
                    .forEach(controller -> {
                        switch ((MachineEvent) event) {
                            // Forward energy event to controller.
                            case ENERGY_DRAINED:
                            case ENERGY_INPUTTED:
                                controller.onMachineEvent(event, data);
                                break;
                            default:
                                break;
                        }
                    });
        }
    }

    @Override
    public void onFirstTick() {
        super.onFirstTick();
        coverHandler.ifPresent(t -> {
            ICover cover = t.getOutputCover();
            if (!(cover instanceof CoverOutput))
                return;
            ((CoverOutput) cover).setEjects(has(FLUID), has(ITEM));
        });
    }

    @Override
    public ResourceLocation getGuiTexture() {
        return new ResourceLocation(getMachineType().getDomain(), "textures/gui/machine/hatch.png");
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        if (cap == AntimatterCaps.COMPONENT_HANDLER_CAPABILITY)
            return componentHandler.cast();
        return super.getCapability(cap, side);
    }
}
