package muramasa.antimatter.capability.node;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import tesseract.TesseractAPI;
import tesseract.api.ITickingNode;
import tesseract.api.fluid.FluidData;
import tesseract.api.fluid.IFluidNode;
import tesseract.graph.ITickingController;
import tesseract.util.Dir;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FluidNode implements IFluidNode, ITickingNode {

    private TileEntity tile;
    private FluidTank tank;
    private ITickingController controller;

    public FluidNode(TileEntity tile, FluidTank tank) {
        this.tile = tile;
        this.tank = tank;

        World world = tile.getWorld();
        if (world != null)
            TesseractAPI.registerFluidNode(world.getDimension().getType().getId(), tile.getPos().toLong(), this);
    }

    @Override
    public void remove() {
        World world = tile.getWorld();
        if (world != null)
            TesseractAPI.removeFluid(world.getDimension().getType().getId(), tile.getPos().toLong());
    }

    //TODO: Call tick from nearest pipe ?
    @Override
    public void tick() {
        if (controller != null) controller.tick();
    }

    //TODO: Finish this after testing of items
    @Override
    public int insert(@Nonnull FluidData data, boolean simulate) {
        return 0;
    }

    @Nullable
    @Override
    public FluidData extract(@Nonnull Object tank, int amount, boolean simulate) {
        return null;
    }

    @Nullable
    @Override
    public Object getAvailableTank(@Nonnull Dir direction) {
        return null;
    }

    @Override
    public int getOutputAmount(@Nonnull Dir direction) {
        return 0;
    }

    @Override
    public int getPriority(@Nonnull Dir direction) {
        return 0;
    }

    @Override
    public int getCapacity() {
        return 0
    }

    @Override
    public boolean canOutput() {
        return tank != null;
    }

    @Override
    public boolean canInput() {
        return tank != null;
    }

    @Override
    public boolean canOutput(@Nonnull Dir direction) {
        return true; // TODO: Should depend on neatrest pipe cover
    }

    @Override
    public boolean canInput(@Nonnull Object fluid, @Nonnull Dir direction) {
        return false;
    }

    @Override
    public boolean connects(@Nonnull Dir direction) {
        return true;
    }

    @Override
    public void reset(@Nullable ITickingController oldController, @Nullable ITickingController newController) {
        if (oldController == null || (controller == oldController && newController == null) || controller != oldController)
            controller = newController;
    }
}
