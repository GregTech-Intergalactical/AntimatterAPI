package muramasa.antimatter.blockentity.pipe;

import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.machine.DefaultHeatHandler;
import muramasa.antimatter.pipe.types.HeatPipe;
import muramasa.antimatter.util.int3;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import tesseract.TesseractCapUtils;
import tesseract.TesseractGraphWrappers;
import tesseract.api.heat.HeatTransaction;
import tesseract.api.heat.IHeatHandler;
import tesseract.api.heat.IHeatPipe;

import java.util.Optional;

public class BlockEntityHeatPipe<T extends HeatPipe<T>> extends BlockEntityPipe<T> implements IHeatPipe {

    public BlockEntityHeatPipe(T type, BlockPos pos, BlockState state) {
        super(type, pos, state);

    }

    @Override
    public Class<?> getCapClass() {
        return IHeatHandler.class;
    }

    @Override
    protected void register() {
        TesseractGraphWrappers.HEAT_CONTROLLER.registerConnector(getLevel(), getBlockPos().asLong(), this, isConnector());
    }

    @Override
    protected boolean deregister() {
        return TesseractGraphWrappers.HEAT_CONTROLLER.remove(level, getBlockPos().asLong());
    }

    @Override
    public int temperatureCoefficient() {
        return this.type.conductivity;
    }
}
