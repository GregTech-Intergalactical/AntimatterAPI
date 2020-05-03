package muramasa.antimatter.capability.enet;

import muramasa.antimatter.Configs;
import muramasa.antimatter.capability.INodeHandler;
import muramasa.antimatter.cover.Cover;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import tesseract.TesseractAPI;
import tesseract.api.electric.IElectricNode;
import tesseract.util.Dir;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EnergyNode implements IElectricNode, INodeHandler {

    private TileEntity tile;
    private IEnergyStorage storage;
    private int voltage;

    private EnergyNode() {
    }

    @Nullable
    public static EnergyNode of(TileEntity tile) {
        LazyOptional<IEnergyStorage> energy = tile.getCapability(CapabilityEnergy.ENERGY);
        EnergyNode node = new EnergyNode();
        if (energy.isPresent()) {
            energy.addListener(x -> node.onRemove(null));
        } else {
            return null;
        }
        node.tile = tile;
        node.storage = energy.orElse(null);
        TesseractAPI.registerElectricNode(tile.getWorld().getDimension().getType().getId(), tile.getPos().toLong(), node);
    }

    @Override
    public void onRemove(Direction side) {
        TesseractAPI.removeElectric(tile.getWorld().getDimension().getType().getId(), tile.getPos().toLong());
    }

    @Override
    public void onUpdate(Direction side, Cover cover) {

        //if (cover instanceof CoverFilter)
    }

    @Override
    public long insert(long maxReceive, boolean simulate) {
        return storage.receiveEnergy((int)(maxReceive * Configs.GAMEPLAY.EU_TO_RF), simulate);
    }

    @Override
    public long extract(long maxExtract, boolean simulate) {
        return 0;
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
        return voltage;
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
    public boolean canInput(int voltage) {
        this.voltage = voltage;
        return true; // Accept any voltage
    }

    @Override
    public boolean connects(@Nonnull Dir direction) {
        return true;
    }
}
