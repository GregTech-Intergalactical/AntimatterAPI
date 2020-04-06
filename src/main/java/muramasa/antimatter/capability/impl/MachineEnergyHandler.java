package muramasa.antimatter.capability.impl;

import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.world.World;
import tesseract.TesseractAPI;
import tesseract.api.GraphWrapper;
import tesseract.api.IConnectable;
import tesseract.api.electric.IElectricEvent;
import tesseract.graph.Connectivity;
import tesseract.util.Dir;

public class MachineEnergyHandler extends EnergyHandler implements IElectricEvent {

    protected GraphWrapper electric;
    protected byte output;

    public MachineEnergyHandler(TileEntityMachine tile, boolean storage) {
        super(0, tile.getMachineTier().getVoltage() * 64, 1, 0, 1, 0);
        output = new OutputEnergyHandler(tile).output; //TODO: refactor may be?
        World world = tile.getWorld();
        if (world != null) {
            electric = storage ?
                TesseractAPI.asElectricController(world.getDimension().getType().getId(), tile.getPos().toLong(), output,this, this) :
                TesseractAPI.asElectricNode(world.getDimension().getType().getId(), tile.getPos().toLong(), this);
        }
    }

    public void update() {
        electric.update();
    }

    public void remove() {
        electric.remove();
    }

    @Override
    public void onOverVoltage(long node) {
    }

    @Override
    public void onOverAmperage(long cable) {
    }

    /* Use like a additional connectivity handler for output sides */
    static class OutputEnergyHandler implements IConnectable {
        TileEntityMachine tile;
        byte output;

        public OutputEnergyHandler(TileEntityMachine tile) {
            this.tile = tile;
            output = Connectivity.of(this);
        }

        @Override
        public boolean connects(Dir direction) {
            return direction.getIndex() == tile.getFacing().getIndex();
        }
    }
}