package muramasa.antimatter.capability.node;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.energy.EnergyStorage;
import tesseract.TesseractAPI;
import tesseract.api.electric.IElectricNode;
import tesseract.util.Dir;

import javax.annotation.Nonnull;

public class EnergyNode implements IElectricNode {

    private TileEntity tile;
    private EnergyStorage storage;

    public EnergyNode(TileEntity tile, EnergyStorage storage) {
        this.tile = tile;
        this.storage = storage;

        World world = tile.getWorld();
        if (world != null)
            TesseractAPI.registerElectricNode(world.getDimension().getType().getId(), tile.getPos().toLong(), this);
    }

    @Override
    public void remove() {
        World world = tile.getWorld();
        if (world != null)
            TesseractAPI.removeElectric(world.getDimension().getType().getId(), tile.getPos().toLong());
    }

    @Override
    public long insert(long maxReceive, boolean simulate) {
        return storage.receiveEnergy((int)maxReceive, simulate);
    }

    @Override
    public long extract(long maxExtract, boolean simulate) {
        return storage.extractEnergy((int)maxExtract, simulate);
    }

    @Override
    public long getEnergy() {
        return storage.getEnergyStored();
    }

    @Override
    public long getCapacity() {
        return storage.getMaxEnergyStored();
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
        return 32;
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
    public boolean canOutput(@Nonnull Dir direction) {
        return false;
    }

    @Override
    public boolean connects(@Nonnull Dir direction) {
        return true;
    }
}
