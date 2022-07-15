package muramasa.antimatter.tesseract.fabric;

import muramasa.antimatter.AntimatterConfig;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleSidedEnergyContainer;
import tesseract.api.gt.GTConsumer;
import tesseract.api.gt.GTTransaction;
import tesseract.api.gt.IEnergyHandler;

public class EnergyTileWrapper implements IEnergyHandler {

    private final BlockEntity tile;
    private final EnergyStorage storage;

    private final GTConsumer.State state = new GTConsumer.State(this);

    public EnergyTileWrapper(BlockEntity tile, EnergyStorage storage) {
        this.tile = tile;
        this.storage = storage;
    }

    @Override
    public boolean insert(GTTransaction transaction) {
        if (storage.getAmount() >= transaction.voltageOut * AntimatterConfig.GAMEPLAY.EU_TO_TRE_RATIO) {
            transaction.addData(1, 0, this::extractEnergy);
            return true;
        }
        return false;
    }

    @Override
    public boolean extractEnergy(GTTransaction.TransferData data) {
        return storage.extract((long) (data.getEnergy(1, false)* AntimatterConfig.GAMEPLAY.EU_TO_TRE_RATIO), Transaction.openNested(null)) > 0;
    }

    @Override
    public boolean addEnergy(GTTransaction.TransferData data) {
        return storage.insert((long) (data.getEnergy(1, true) * AntimatterConfig.GAMEPLAY.EU_TO_TRE_RATIO), Transaction.openNested(null)) > 0;
    }

    @Override
    public GTTransaction extract(GTTransaction.Mode mode) {
        return new GTTransaction(0, 0, a -> {
        });
    }

    @Override
    public long getEnergy() {
        return (long) (storage.getAmount() * AntimatterConfig.GAMEPLAY.EU_TO_TRE_RATIO);
    }

    @Override
    public long getCapacity() {
        return (long) (storage.getCapacity() * AntimatterConfig.GAMEPLAY.EU_TO_TRE_RATIO);
    }

    @Override
    public long getOutputAmperage() {
        return 1;
    }

    @Override
    public long getOutputVoltage() {
        if (storage instanceof SimpleSidedEnergyContainer limitingEnergyStorage){
            return limitingEnergyStorage.getMaxExtract(null);
        }
        return 32;
    }

    @Override
    public long getInputAmperage() {
        return 1;
    }

    @Override
    public long getInputVoltage() {
        if (storage instanceof SimpleSidedEnergyContainer limitingEnergyStorage){
            return limitingEnergyStorage.getMaxExtract(null);
        }
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean canOutput() {
        return AntimatterConfig.GAMEPLAY.ENABLE_FE_OR_TRE_INPUT && storage.supportsExtraction();
    }

    @Override
    public boolean canInput() {
        return storage.supportsInsertion();
    }

    @Override
    public boolean canInput(Direction dir) {
        return canInput();
    }

    @Override
    public boolean canOutput(Direction direction) {
        return canOutput();
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
    public CompoundTag serializeNBT() {
        return null;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {

    }
}
