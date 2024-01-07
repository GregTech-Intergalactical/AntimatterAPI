package muramasa.antimatter.capability.machine;

import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.blockentity.BlockEntityBase;
import muramasa.antimatter.blockentity.BlockEntityCache;
import muramasa.antimatter.capability.Dispatch;
import muramasa.antimatter.blockentity.BlockEntityMachine;
import muramasa.antimatter.capability.IMachineHandler;
import muramasa.antimatter.machine.event.MachineEvent;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import tesseract.TesseractCapUtils;
import tesseract.api.heat.IHeatHandler;

import java.util.Optional;

public class DefaultHeatHandler implements IHeatHandler, Dispatch.Sided<IHeatHandler> {

    public final int heatCap;
    public final int temperaturesize;
    public final int maxInput, maxOutput;
    protected int currentHeat;

    public final BlockEntityBase<?> tile;

    public DefaultHeatHandler(BlockEntityBase<?> tile, int heatCap, int maxInput, int maxOutput) {
        this.heatCap = heatCap;
        this.tile = tile;
        this.temperaturesize = 100;
        this.maxInput = maxInput;
        this.maxOutput = maxOutput;
    }

    @Override
    public int insert(int heat, boolean simulate) {
        if (!canInput()) return 0;
        int insert = Math.min(maxInput, Math.min(heatCap - currentHeat, heat));
        return insertInternal(insert, simulate);
    }

    @Override
    public int extract(int heat, boolean simulate) {
        if (!canOutput()) return 0;
        int extract = Math.min(maxOutput, Math.min(currentHeat, heat));
        return extractInternal(extract, simulate);
    }

    public int insertInternal(int heat, boolean simulate) {
        int insert = Math.min(heatCap - currentHeat, heat);
        if (!simulate) add(insert);
        return insert;
    }

    public int extractInternal(int heat, boolean simulate) {
        int extract = Math.min(currentHeat, heat);
        if (!simulate) sub(extract);
        return extract;
    }

    @Override
    public boolean canInput() {
        return maxInput > 0;
    }

    @Override
    public boolean canOutput() {
        return maxOutput > 0;
    }

    @Override
    public boolean canInput(Direction direction) {
        return canInput();
    }

    @Override
    public boolean canOutput(Direction direction) {
        return canOutput();
    }

    @Override
    public long getMaxInsert() {
        return maxInput;
    }

    @Override
    public long getMaxExtract() {
        return maxOutput;
    }

    protected void sub(int temp) {
        this.currentHeat -= temp;
        if (tile instanceof IMachineHandler machineHandler){
            machineHandler.onMachineEvent(MachineEvent.HEAT_DRAINED, temp);
        }
    }

    protected void add(int temp) {
        this.currentHeat += temp;
        if (tile instanceof IMachineHandler machineHandler){
            machineHandler.onMachineEvent(MachineEvent.HEAT_INPUTTED, temp);
        }
    }

    @Override
    public int getTemperature() {
        return this.currentHeat / temperaturesize;
    }

    public void update(boolean active) {

        for (Direction dir : Ref.DIRS) {
            if (canOutput(dir)) {
                BlockEntity tile = BlockEntityCache.getBlockEntity(this.tile.getLevel(),this.tile.getBlockPos().relative(dir));
                if (tile == null) continue;
                Optional<IHeatHandler> handle = TesseractCapUtils.getHeatHandler(tile, dir.getOpposite());
                if (handle.map(h -> !h.canInput(dir.getOpposite())).orElse(true)) continue;
                handle.ifPresent(eh -> Utils.transferHeat(this, eh));
            }
        }
        /*if (!active) {
            this.currentHeat -= temperaturesize / 40;
            this.currentHeat = Math.max(0, this.currentHeat);
        }*/
    }

    @Override
    public int getHeat() {
        return currentHeat;
    }

    @Override
    public int getHeatCap() {
        return heatCap;
    }

    @Override
    public Optional<? extends IHeatHandler> forSide(Direction side) {
        if (tile instanceof BlockEntityMachine<?> m) {
            if (side == null) return Optional.of(this);
            if (m.coverHandler.map(t -> t.get(side).getFactory() == Data.COVERHEAT).orElse(false)) {
                return Optional.of(this);
            } else {
                return Optional.empty();
            }
        }
        return Optional.of(this);
    }

    @Override
    public Optional<? extends IHeatHandler> forNullSide() {
        return forSide(null);
    }

    @Override
    public CompoundTag serialize(CompoundTag tag) {
        tag.putInt(Ref.TAG_MACHINE_HEAT, this.currentHeat);
        return tag;
    }

    @Override
    public void deserialize(CompoundTag nbt) {
        this.currentHeat = nbt.getInt(Ref.TAG_MACHINE_HEAT);
    }
}
