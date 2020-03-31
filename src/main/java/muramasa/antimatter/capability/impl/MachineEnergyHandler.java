package muramasa.antimatter.capability.impl;

import muramasa.antimatter.tile.TileEntityMachine;
import tesseract.TesseractAPI;
import tesseract.api.GraphWrapper;
import tesseract.api.electric.IElectricCable;
import tesseract.api.electric.IElectricEvent;
import tesseract.api.electric.IElectricNode;

public class MachineEnergyHandler extends EnergyHandler {

    protected GraphWrapper electric;

    public MachineEnergyHandler(TileEntityMachine tile) {
        super(0, 2, tile.getMachineTier().getVoltage() * 64, tile.getMachineTier().getVoltage(), tile.getMachineTier().getVoltage());
        electric = TesseractAPI.asElectricProducer(tile.getWorld().getDimension().getType().getId(), tile.getPos().toLong(), this, new IElectricEvent() {
            @Override
            public void onOverVoltage(IElectricNode node) {

            }

            @Override
            public void onOverAmperage(IElectricCable cable) {

            }
        });
    }

    public void update() {
        electric.update();
    }

    public void remove() {
        electric.remove();
    }
}