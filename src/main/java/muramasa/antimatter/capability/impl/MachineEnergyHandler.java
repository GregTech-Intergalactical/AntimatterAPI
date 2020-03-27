package muramasa.antimatter.capability.impl;

import muramasa.antimatter.tile.TileEntityMachine;

public class MachineEnergyHandler extends EnergyHandler {

    public MachineEnergyHandler(TileEntityMachine tile) {
        super(tile, 0, 1, tile.getTier().getVoltage() * 64, tile.getTier().getVoltage(), tile.getTier().getVoltage());
    }
}