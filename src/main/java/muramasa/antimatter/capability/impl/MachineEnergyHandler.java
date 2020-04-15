package muramasa.antimatter.capability.impl;

import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.world.World;
import tesseract.TesseractAPI;
import tesseract.graph.ITickingController;
import tesseract.util.Dir;

import javax.annotation.Nonnull;

public class MachineEnergyHandler extends EnergyHandler {

    protected TileEntityMachine tile;
    protected ITickingController controller;

    public MachineEnergyHandler(TileEntityMachine tile) {
        super(0, 0, 0, 0, 1, 0);
        this.tile = tile;
        if (tile != null) {
            this.capacity = tile.getMachineTier().getVoltage() * 64L;
            this.voltage_in = tile.getMachineTier().getVoltage();

            World world = tile.getWorld();
            if (world != null)
                TesseractAPI.registerElectricNode(world.getDimension().getType().getId(), tile.getPos().toLong(), this);
        }
    }

    public void onUpdate() {
        if (controller != null) controller.tick();
    }

    public void onRemove() {
        if (tile != null) {
            World world = tile.getWorld();
            if (world != null)
                TesseractAPI.removeElectric(world.getDimension().getType().getId(), tile.getPos().toLong());
        }
    }

    @Override
    public boolean connects(@Nonnull Dir direction) {
        return tile.getFacing().getIndex() != direction.getIndex();
    }

    @Override
    public void reset(ITickingController oldController, ITickingController newController) {
        if (oldController == null || (controller == oldController && newController == null) || controller != oldController)
            controller = newController;
    }

    @Override
    public boolean canOutput(@Nonnull Dir direction) {
        return false;
    }
}