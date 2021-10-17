package muramasa.antimatter.tesseract;

import muramasa.antimatter.AntimatterConfig;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.energy.IEnergyStorage;
import tesseract.api.gt.GTConsumer;
import tesseract.api.gt.IEnergyHandler;

public class EnergyTileWrapper implements IEnergyHandler {

    private final TileEntity tile;
    private final IEnergyStorage storage;

    private final GTConsumer.State state = new GTConsumer.State(this);

    public EnergyTileWrapper(TileEntity tile, IEnergyStorage storage) {
        this.tile = tile;
        this.storage = storage;
    }

    @Override
    public long insert(long maxReceive, boolean simulate) {
        if (state.receive(simulate, getInputAmperage(), maxReceive)) {
            return storage.receiveEnergy((int)(maxReceive * AntimatterConfig.GAMEPLAY.EU_TO_FE_RATIO), simulate);
        }
        return 0;
    }

    @Override
    public long extract(long maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public long getEnergy() {
        return (long) (storage.getEnergyStored()* AntimatterConfig.GAMEPLAY.EU_TO_FE_RATIO);
    }

    @Override
    public long getCapacity() {
        return (long)(storage.getMaxEnergyStored()* AntimatterConfig.GAMEPLAY.EU_TO_FE_RATIO);
    }

    @Override
    public int getOutputAmperage() {
        return 0;
    }

    @Override
    public int getOutputVoltage() {
        return 0;
    }

    @Override
    public int getInputAmperage() {
        return 1;
    }

    @Override
    public int getInputVoltage() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean canOutput() {
        return false;
    }

    @Override
    public boolean canInput() {
        return storage.canReceive();
    }

    @Override
    public boolean canInput(Direction dir) {
        return canInput();
    }

    @Override
    public boolean canOutput(Direction direction) {
        return false;
    }

    @Override
    public GTConsumer.State getState() {
        return state;
    }

    @Override
    public void tesseractTick() {
        getState().onTick();
    }


    @Override
    public CompoundNBT serializeNBT() {
        return null;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {

    }
}
