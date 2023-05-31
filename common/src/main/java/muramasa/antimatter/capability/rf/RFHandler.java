package muramasa.antimatter.capability.rf;

import earth.terrarium.botarium.common.energy.base.EnergySnapshot;
import earth.terrarium.botarium.util.Updatable;
import muramasa.antimatter.Ref;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import tesseract.api.gt.GTConsumer;
import tesseract.api.gt.GTTransaction;
import tesseract.api.rf.IRFNode;


public class RFHandler implements IRFNode, Updatable<BlockEntity> {

    protected final long capacity;

    protected long energy;
    protected long maxIn, maxOut;

    public RFHandler(long energy, long capacity, int maxIn, int maxOut) {
        this.energy = energy;
        this.capacity = capacity;
        this.maxIn = maxIn;
        this.maxOut = maxOut;
    }

    /**
     * Tesseract IGTNode Implementations
     **/

    public void setMaxOutput(long maxOutput) {
        this.maxOut = maxOutput;
    }

    public void setMaxInput(long maxInput) {
        this.maxIn = maxInput;
    }

    @Override
    public void setEnergy(long energy) {
        this.energy = energy;
    }

    @Override
    public long extractEnergy(long maxAmount, boolean simulate) {
        long maxExtract = Math.min(maxAmount, this.energy);
        if (!simulate) this.energy -= maxExtract;
        return maxExtract;
    }

    @Override
    public long insertEnergy(long maxAmount, boolean simulate) {
        long maxInsert = Mth.clamp(maxAmount, 0, capacity - energy);
        if (!simulate) this.energy += maxInsert;
        return maxInsert;
    }

    @Override
    public long getStoredEnergy() {
        return energy;
    }

    @Override
    public long getMaxCapacity() {
        return capacity;
    }
    @Override
    public long maxInsert() {
        return maxIn;
    }

    @Override
    public long maxExtract() {
        return maxOut;
    }

    @Override
    public boolean allowsInsertion() {
        return maxIn > 0;
    }

    @Override
    public boolean canInput(Direction direction) {
        return allowsInsertion();
    }

    @Override
    public boolean canOutput(Direction direction) {
        return allowsExtraction();
    }

    @Override
    public boolean allowsExtraction() {
        return maxOut > 0;
    }

    @Override
    public EnergySnapshot createSnapshot() {
        return new RFSnapshot(this);
    }

    @Override
    public CompoundTag serialize(CompoundTag tag) {
        tag.putLong(Ref.TAG_MACHINE_ENERGY, this.energy);
        tag.putLong(Ref.TAG_MACHINE_VOLTAGE_IN, this.maxIn);
        tag.putLong(Ref.TAG_MACHINE_VOLTAGE_OUT, this.maxOut);
        return tag;
    }

    @Override
    public void deserialize(CompoundTag nbt) {
        this.energy = nbt.getLong(Ref.TAG_MACHINE_ENERGY);
        this.maxIn = nbt.getLong(Ref.TAG_MACHINE_VOLTAGE_IN);
        this.maxOut = nbt.getLong(Ref.TAG_MACHINE_VOLTAGE_OUT);
    }

    @Override
    public void clearContent() {
        this.energy = 0;
    }

    @Override
    public void update(BlockEntity object) {

    }
}
