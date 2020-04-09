package muramasa.antimatter.capability.impl;

import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.world.World;
import tesseract.TesseractAPI;
import tesseract.api.electric.IElectricEvent;
import tesseract.graph.ITickingController;
import tesseract.util.Dir;

public class MachineEnergyHandler extends EnergyHandler implements IElectricEvent {
    protected ITickingController electric = null;
    TileEntityMachine tile;

    public MachineEnergyHandler(TileEntityMachine tile) {
        super(0, tile == null ? 0 : tile.getMachineTier().getVoltage() * 64, 1, 0, 1, 0);
        this.tile = tile;
        if (tile != null) {
            World world = tile.getWorld();
            if (world != null)
                TesseractAPI.addElectricNode(world.getDimension().getType().getId(), tile.getPos().toLong(), this, this);
        }
    }

    public void update() {
        if (electric != null)
            electric.tick();
    }

    public void remove() {
        if (tile != null && tile.getWorld() != null)
            TesseractAPI.removeNode(tile.getWorld().getDimension().getType().getId(), tile.getPos().toLong());
    }

    @Override
    public void onOverVoltage(long node) {
    }

    @Override
    public void onOverAmperage(long cable) {
    }

    @Override
    public boolean connects(Dir direction) {
        return false;
    }

    @Override
    public void reset(ITickingController oldController, ITickingController newController) {
        if (oldController == null || (electric == oldController && newController == null) || electric != oldController)
          electric = newController;
    }

    /**
     * Used to determine which sides can output energy (if any).
     * Output cannot be used as input.
     *
     * @param direction Direction to test
     * @return Returns true if the given direction is output side
     */
    @Override
    public boolean canOutput(Dir direction) {
        return false;
    }
}