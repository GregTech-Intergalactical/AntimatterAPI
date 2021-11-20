package muramasa.antimatter.tesseract;

import muramasa.antimatter.AntimatterConfig;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.energy.IEnergyStorage;
import tesseract.api.gt.GTConsumer;
import tesseract.api.gt.GTTransaction;
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
    public boolean insert(GTTransaction transaction) {
        if (storage.getEnergyStored() >= transaction.voltageOut * AntimatterConfig.GAMEPLAY.EU_TO_FE_RATIO) {
            transaction.addData(1, 0, this::extractEnergy, null);
            return true;
        }
        return false;
    }

    @Override
    public boolean extractEnergy(GTTransaction.TransferData data) {
        return storage.extractEnergy((int) (data.getEnergy(1, false) * AntimatterConfig.GAMEPLAY.EU_TO_FE_RATIO), false) > 0;
    }

    @Override
    public boolean addEnergy(GTTransaction.TransferData data) {
        return storage.receiveEnergy((int) (data.getEnergy(1, true) * AntimatterConfig.GAMEPLAY.EU_TO_FE_RATIO), false) > 0;
    }

    @Override
    public GTTransaction extract(GTTransaction.Mode mode) {
        return new GTTransaction(0, 0, a -> {
        });
    }

    @Override
    public long getEnergy() {
        return (long) (storage.getEnergyStored() * AntimatterConfig.GAMEPLAY.EU_TO_FE_RATIO);
    }

    @Override
    public long getCapacity() {
        return (long) (storage.getMaxEnergyStored() * AntimatterConfig.GAMEPLAY.EU_TO_FE_RATIO);
    }

    @Override
    public long getOutputAmperage() {
        return 0;
    }

    @Override
    public long getOutputVoltage() {
        return 0;
    }

    @Override
    public long getInputAmperage() {
        return 1;
    }

    @Override
    public long getInputVoltage() {
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
