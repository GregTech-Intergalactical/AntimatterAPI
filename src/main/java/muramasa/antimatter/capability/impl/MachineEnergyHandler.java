package muramasa.antimatter.capability.impl;

import muramasa.antimatter.tile.TileEntityMachine;
import tesseract.electric.Electric;

public class MachineEnergyHandler extends EnergyHandler {

    protected Electric electric;

    public MachineEnergyHandler(TileEntityMachine tile) {
        super(0, 1, tile.getTier().getVoltage() * 64, tile.getTier().getVoltage(), tile.getTier().getVoltage());
        electric = Electric.ofProducer(tile.getWorld().getDimension().getType().getId(), tile.getPos().toLong(), this);
    }

    public void update() {
        electric.update();
    }

    public void remove() {
        electric.remove();
    }
}