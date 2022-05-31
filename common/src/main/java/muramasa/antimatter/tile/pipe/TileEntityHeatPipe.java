package muramasa.antimatter.tile.pipe;

import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.machine.DefaultHeatHandler;
import muramasa.antimatter.pipe.types.HeatPipe;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import muramasa.antimatter.util.int3;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import tesseract.Tesseract;
import tesseract.TesseractPlatformUtils;
import tesseract.api.TesseractCaps;
import tesseract.api.heat.HeatTransaction;
import tesseract.api.heat.IHeatHandler;
import tesseract.api.heat.IHeatPipe;

public class TileEntityHeatPipe<T extends HeatPipe<T>> extends TileEntityPipe<T> implements IHeatPipe {

    public TileEntityHeatPipe(T type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        pipeCapHolder.set(() -> new DefaultHeatHandler(this, 800*type.conductivity, type.conductivity) {

            @Override
            public void update(boolean active) {
                boolean doTransfer = true;
                //Always called per frame
                if (doTransfer) {
                    //Transfer 1 degree of power each quarter second.
                    HeatTransaction tx = extract();
                    if (tx.isValid()) {
                        int3 mutPos = new int3();
                        for (Direction dir : Ref.DIRS) {
                            if (connects(dir) && validate(dir)) {
                                mutPos.set(pos);
                                mutPos = mutPos.offset(1,dir);
                                BlockEntity ent = level.getBlockEntity(mutPos);
                                if (ent == null) continue;
                                TesseractPlatformUtils.getCapability(ent, TesseractCaps.getHEAT_CAPABILITY(), dir.getOpposite()).ifPresent(t -> t.insert(tx));
                            }
                        }
                        tx.commit();
                        this.currentHeat = Math.max(0, this.currentHeat - (int)(((float)this.temperaturesize)*0.01));
                    }
                }
            }
        });
    }

    @Override
    protected Capability<?> getCapability() {
        return TesseractCaps.getHEAT_CAPABILITY();
    }

    @Override
    protected void register() {
        Tesseract.HEAT_CONTROLLER.registerConnector(getLevel(), getBlockPos().asLong(), this, isConnector());
    }

    @Override
    protected boolean deregister() {
        return Tesseract.HEAT_CONTROLLER.remove(level, getBlockPos().asLong());
    }

    @Override
    public HeatTransaction extract() {
        return ((LazyOptional<IHeatHandler>) pipeCapHolder.nullSide()).map(IHeatHandler::extract).orElse(null);
    }

    @Override
    public void insert(HeatTransaction transaction) {
        ((LazyOptional<IHeatHandler>) pipeCapHolder.nullSide()).ifPresent(t -> t.insert(transaction));
    }

    @Override
    public int getHeat() {
        return ((LazyOptional<IHeatHandler>) pipeCapHolder.nullSide()).map(IHeatHandler::getHeat).orElse(0);
    }

    @Override
    public int getHeatCap() {
        return ((LazyOptional<IHeatHandler>) pipeCapHolder.nullSide()).map(IHeatHandler::getHeatCap).orElse(0);
    }

    @Override
    public int getTemperature() {
        return ((LazyOptional<IHeatHandler>) pipeCapHolder.nullSide()).map(IHeatHandler::getTemperature).orElse(0);
    }

    @Override
    public void update(boolean active) {
        ((LazyOptional<IHeatHandler>) pipeCapHolder.nullSide()).ifPresent(t -> t.update(active));
    }

    @Override
    public int temperatureCoefficient() {
        return this.type.conductivity;
    }

    @Override
    public CompoundTag serializeNBT() {
        return ((LazyOptional<IHeatHandler>) pipeCapHolder.nullSide()).map(h -> h.serializeNBT()).orElse(null);
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        ((LazyOptional<IHeatHandler>) pipeCapHolder.nullSide()).ifPresent(h -> h.deserializeNBT(nbt));
    }
}
