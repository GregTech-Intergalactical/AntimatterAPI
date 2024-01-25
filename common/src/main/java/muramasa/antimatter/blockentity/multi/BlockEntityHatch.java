package muramasa.antimatter.blockentity.multi;

import lombok.Getter;
import lombok.Setter;
import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.blockentity.BlockEntityMachine;
import muramasa.antimatter.capability.ComponentHandler;
import muramasa.antimatter.capability.machine.HatchComponentHandler;
import muramasa.antimatter.capability.machine.MachineCoverHandler;
import muramasa.antimatter.capability.machine.MachineEnergyHandler;
import muramasa.antimatter.capability.machine.MachineFluidHandler;
import muramasa.antimatter.cover.CoverDynamo;
import muramasa.antimatter.cover.CoverEnergy;
import muramasa.antimatter.cover.CoverOutput;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.machine.event.MachineEvent;
import muramasa.antimatter.machine.types.HatchMachine;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.structure.IComponent;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;

import static muramasa.antimatter.Data.COVERDYNAMO;
import static muramasa.antimatter.Data.COVERENERGY;
import static muramasa.antimatter.machine.MachineFlag.*;

public class BlockEntityHatch<T extends BlockEntityHatch<T>> extends BlockEntityMachine<T> implements IComponent {

    public final Optional<HatchComponentHandler<T>> componentHandler;
    public final HatchMachine hatchMachine;
    @Getter
    @Setter
    private ITextureProvider textureBlock = null;

    public BlockEntityHatch(HatchMachine type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.hatchMachine = type;
        componentHandler = Optional
                .of(new HatchComponentHandler<>((T)this));
        if (type.has(FLUID)) {
            fluidHandler.set(() -> new MachineFluidHandler<>((T) this, 16000 * (getMachineTier().getIntegerId()), 1000 * (250 + getMachineTier().getIntegerId())));
        }
        if (type.has(EU)) {
            energyHandler.set(() -> new MachineEnergyHandler<T>((T) this, 0, getMachineTier().getVoltage() * 66L,
                    type.getOutputCover().getId().contains("energy") ? tier.getVoltage() : 0,
                    type.getOutputCover().getId().contains("dynamo") ? tier.getVoltage() : 0,
                    type.getOutputCover().getId().contains("energy") ? 2 : 0, type.getOutputCover().getId().contains("dynamo") ? 1 : 0) {
                @Override
                public boolean canInput(Direction direction) {
                    ICover out = tile.coverHandler.map(MachineCoverHandler::getOutputCover).orElse(null);
                    if (out == null)
                        return false;
                    return out instanceof CoverEnergy && direction == out.side();
                }

                @Override
                protected boolean checkVoltage(long voltage) {
                    boolean flag = true;
                    if (type.getOutputCover().getId().contains("energy")) {
                        flag = voltage <= getInputVoltage();
                    }
                    if (!flag && AntimatterConfig.MACHINES_EXPLODE.get()) {
                        Utils.createExplosion(tile.getLevel(), tile.getBlockPos(), 4.0F, Explosion.BlockInteraction.BREAK);
                    }
                    return flag;
                }

                @Override
                public boolean canOutput(Direction direction) {
                    ICover out = tile.coverHandler.map(MachineCoverHandler::getOutputCover).orElse(null);
                    if (out == null)
                        return false;
                    return out instanceof CoverDynamo && direction == out.side();
                }
            });
        }
    }

    @Override
    public boolean wrenchMachine(Player player, BlockHitResult res, boolean crouch) {
        return setFacing(player, Utils.getInteractSide(res));
    }

    @Override
    protected boolean setFacing(Player player, Direction side) {
        boolean setFacing = super.setFacing(player, side);
        if (setFacing){
            setOutputFacing(player, side);
        }
        return setFacing;
    }

    @Override
    public Optional<HatchComponentHandler<T>> getComponentHandler() {
        return componentHandler;
    }

    public Texture getBaseTexture(Direction side){
        if (textureBlock == null || textureBlock.getTextures().length == 0) return null;
        if (textureBlock.getTextures().length >= 6){
            return textureBlock.getTextures()[side.get3DDataValue()];
        }
        return textureBlock.getTextures()[0];
    }

    @Override
    public Function<Direction, Texture> getMultiTexture() {
        if (textureBlock == null || textureBlock.getTextures().length == 0) return null;
        return this::getBaseTexture;
    }

    @Override
    public void onMachineEvent(IMachineEvent event, Object... data) {
        if (isClientSide())
            return;
        super.onMachineEvent(event, data);
        if (event instanceof SlotType<?>) {
            componentHandler.map(ComponentHandler::getControllers).orElse(Collections.emptyList())
                    .forEach(controller -> {
                        if (event == SlotType.IT_IN || event == SlotType.IT_OUT || event == SlotType.CELL_IN || event == SlotType.CELL_OUT || event == SlotType.FL_IN || event == SlotType.FL_OUT) {
                            controller.onMachineEvent(event, data);
                        }
                    });
        } else if (event instanceof MachineEvent) {
            componentHandler.map(ComponentHandler::getControllers).orElse(Collections.emptyList())
                    .forEach(controller -> {
                        switch ((MachineEvent) event) {
                            // Forward energy event to controller.
                            case ENERGY_DRAINED, ENERGY_INPUTTED, HEAT_INPUTTED, HEAT_DRAINED -> controller.onMachineEvent(event, data);
                            default -> {
                            }
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
}
