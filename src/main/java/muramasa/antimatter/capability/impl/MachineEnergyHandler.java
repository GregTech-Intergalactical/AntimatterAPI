package muramasa.antimatter.capability.impl;

import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.world.World;
import tesseract.TesseractAPI;
import tesseract.api.electric.IElectricEvent;
import tesseract.graph.ITickingController;
import tesseract.util.Dir;

import javax.annotation.Nonnull;

public class MachineEnergyHandler extends EnergyHandler {
    protected ITickingController electric;
    protected TileEntityMachine tile;

    public MachineEnergyHandler(TileEntityMachine tile) {
        super(0, 0, 0, 0, 1, 0);
        this.tile = tile;
        if (tile != null) {
            this.capacity = tile.getMachineTier().getVoltage() * 64L;
            this.voltage_in = tile.getMachineTier().getVoltage();

            World world = tile.getWorld();
            if (world != null)
                TesseractAPI.addElectricNode(world.getDimension().getType().getId(), tile.getPos().toLong(), this);
        }
    }

    public void update() {
        if (electric != null) electric.tick();
    }

    public void remove() {
        if (tile != null) {
            World world = tile.getWorld();
            if (world != null)
                TesseractAPI.removeElectric(world.getDimension().getType().getId(), tile.getPos().toLong());
        }
    }

    @Override
    public boolean connects(@Nonnull Dir direction) {
        return true;
    }

    @Override
    public void reset(ITickingController oldController, ITickingController newController) {
        if (oldController == null || (electric == oldController && newController == null) || electric != oldController)
            electric = newController;
    }

    @Override
    public boolean canOutput(@Nonnull Dir direction) {
        return false;
    }
}