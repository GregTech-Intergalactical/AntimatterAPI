package muramasa.antimatter.capability.impl;

import muramasa.antimatter.pipe.BlockCable;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.world.Explosion;
import tesseract.electric.Electric;
import tesseract.electric.api.IElectricCable;
import tesseract.electric.api.IElectricEvent;
import tesseract.electric.api.IElectricNode;

public class MachineEnergyHandler extends EnergyHandler {

    protected Electric electric;

    public MachineEnergyHandler(TileEntityMachine tile) {
        super(0, 2, tile.getTier().getVoltage() * 64, tile.getTier().getVoltage(), tile.getTier().getVoltage());
        electric = Electric.ofProducer(tile.getWorld().getDimension().getType().getId(), tile.getPos().toLong(), this, new IElectricEvent() {
            @Override
            public void onOverVoltage(long position) {
                tile.onOverVoltage(position);
            }

            @Override
            public void onOverAmperage(long position) {
                tile.onOverAmperage(position);
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