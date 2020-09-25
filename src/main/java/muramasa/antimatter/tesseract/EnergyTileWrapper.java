package muramasa.antimatter.tesseract;

import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.cover.CoverInstance;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import tesseract.Tesseract;
import tesseract.api.gt.IGTNode;
import tesseract.util.Dir;

import javax.annotation.Nullable;

public class EnergyTileWrapper extends TileWrapper<IEnergyStorage> implements IGTNode {

    public EnergyTileWrapper(TileEntity tile) {
        super(tile, CapabilityEnergy.ENERGY);
    }
    
    @Override
    public void onInit() {
        Tesseract.GT_ENERGY.registerNode(tile.getWorld().getDimension().getType().getId(), tile.getPos().toLong(), this);
    }

    @Override
    public void onRemove() {
        Tesseract.GT_ENERGY.remove(tile.getWorld().getDimension().getType().getId(), tile.getPos().toLong());
    }

    @Override
    public long insert(long maxReceive, boolean simulate) {
        return handler.receiveEnergy((int)(maxReceive * AntimatterConfig.GAMEPLAY.EU_TO_FE_RATIO), simulate);
    }

    @Override
    public long extract(long maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public long getEnergy() {
        return handler.getEnergyStored();
    }

    @Override
    public long getCapacity() {
        return handler.getMaxEnergyStored();
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
        return handler.canReceive();
    }

    @Override
    public boolean canOutput(Dir direction) {
        return false;
    }

    @Override
    public boolean connects(Dir direction) {
        return true;
    }
}
