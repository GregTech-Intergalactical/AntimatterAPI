package muramasa.antimatter.cover.pipe;

import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.util.Utils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import tesseract.TesseractAPI;
import tesseract.api.fluid.FluidData;
import tesseract.api.fluid.IFluidNode;
import tesseract.graph.ITickingController;
import tesseract.util.Dir;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CoverFluid extends Cover implements IFluidNode {

    @Override
    public String getId() {
        return "fluid";
    }

    /*@Override
    public void onSet(TileEntity tile, Direction side) {
        World world = tile.getWorld();
        if (world != null)
            TesseractAPI.registerFluidNode(world.getDimension().getType().getId(), tile.getPos().toLong(), this);
    }

    @Override
    public void onRemove() {

    }*/

    @Override
    public void onUpdate(TileEntity tile, Direction side) {

    }

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
        return 0;
    }

    @Override
    public boolean canOutput() {
        return false;
    }

    @Override
    public boolean canInput() {
        return false;
    }

    @Override
    public boolean canOutput(@Nonnull Dir direction) {
        return false;
    }

    @Override
    public boolean canInput(@Nonnull Object fluid, @Nonnull Dir direction) {
        return false;
    }

    @Override
    public boolean connects(@Nonnull Dir direction) {
        return false;
    }

    @Override
    public void reset(@Nullable ITickingController oldController, @Nullable ITickingController newController) {

    }
}
