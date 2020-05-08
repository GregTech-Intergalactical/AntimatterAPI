package muramasa.antimatter.capability.impl;

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
        TesseractAPI.registerElectricNode(tile.getDimention(), tile.getPos().toLong(), this);
    }

    public void onRemove() {
        TesseractAPI.removeElectric(tile.getDimention(), tile.getPos().toLong());
    }

    public void onUpdate() {
        if (controller != null) controller.tick();
    }

//    public List<String> getInfo(List<String> info, World world, BlockState state, BlockPos pos) {
//        ITickingController controller = TesseractAPI.getElectricController(world.getDimension().getType().getId(), pos.toLong());
//        if (controller != null) info.addAll(Arrays.asList(controller.getInfo()));
//        return info;
//    }

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