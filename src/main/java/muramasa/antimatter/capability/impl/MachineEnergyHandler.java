package muramasa.antimatter.capability.impl;

import muramasa.antimatter.tile.TileEntityMachine;
import tesseract.electric.ElectricHandler;

public class MachineEnergyHandler extends EnergyHandler {

    protected ElectricHandler electricHandler;

    public MachineEnergyHandler(TileEntityMachine tile) {
        super(0, 1, tile.getTier().getVoltage() * 64, tile.getTier().getVoltage(), tile.getTier().getVoltage());
        electricHandler = new ElectricHandler(tile.getWorld().getDimension().getType().getId(), tile.getPos().toLong(), this);
    }

    public void update() {
        electricHandler.send();
    }

    public void remove() {
        electricHandler.remove();
    }
}