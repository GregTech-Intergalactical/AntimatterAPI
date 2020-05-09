package muramasa.antimatter.capability.impl;

import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.tile.TileEntityMachine;
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
        this.capacity = tile.getMachineTier().getVoltage() * 64L;
        this.voltage_in = tile.getMachineTier().getVoltage();
        if (tile.isServerSide()) TesseractAPI.registerElectricNode(tile.getDimention(), tile.getPos().toLong(), this);
    }

    public void onRemove() {
        if (tile.isServerSide()) TesseractAPI.removeElectric(tile.getDimention(), tile.getPos().toLong());
    }

    public void onUpdate() {
        if (controller != null) controller.tick();
    }

    /*public void onReset() {
        if (tile.isServerSide()) {
            TesseractAPI.removeElectric(tile.getDimention(), tile.getPos().toLong());
            TesseractAPI.registerElectricNode(tile.getDimention(), tile.getPos().toLong(), this);
        }
    }*/

    @Override
    public boolean connects(@Nonnull Dir direction) {
        return tile.getFacing().getIndex() != direction.getIndex() && tile.getCover(Ref.DIRECTIONS[direction.getIndex()]).isEqual(Data.COVER_NONE);
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