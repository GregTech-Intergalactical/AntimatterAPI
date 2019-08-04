package muramasa.gtu.api.capability.impl;

import muramasa.gtu.api.tileentities.TileEntityMachine;

public class MachineEnergyHandler extends EnergyHandler {

    public MachineEnergyHandler(TileEntityMachine tile) {
        super(0, tile.getTier().getVoltage() * 64, tile.getTier().getVoltage(), tile.getTier().getVoltage());
    }
}
