package muramasa.antimatter.pipe;

import muramasa.antimatter.pipe.types.FluidPipe;
import muramasa.antimatter.pipe.types.PipeType;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import tesseract.TesseractAPI;
import tesseract.graph.ITickingController;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class BlockFluidPipe extends BlockPipe<FluidPipe<?>> {

    public BlockFluidPipe(PipeType<?> type, PipeSize size) {
        super(type.getId(), type, size);
    }

    @Override
    public boolean canConnect(IBlockReader world, BlockState state, @Nullable TileEntity tile, BlockPos pos) {
        return state.getBlock() instanceof BlockFluidPipe || tile != null && tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).isPresent();
    }

    @Override
    public List<String> getInfo(List<String> info, World world, BlockState state, BlockPos pos) {
        ITickingController controller = TesseractAPI.getFluidController(world.getDimension().getType().getId(), pos.toLong());
        if (controller != null) info.addAll(Arrays.asList(controller.getInfo()));
        return info;
    }

//    @Override
//    protected void onNeighborCatch(World world, Direction direction, TileEntity neighbour) {
//         PipeCache.setFluid(world, direction, neighbour);
//    }

//    @Override
//    public String getDisplayName(ItemStack stack) {
//        //TODO add prefix and suffix for local
//        PipeSize size = PipeSize.VALUES[stack.getMetadata()];
//        return (size == PipeSize.NORMAL ? "" : size.getDisplayName() + " ") + material.getDisplayName() + " Fluid Pipe";
//    }
//
//    @Override
//    @SideOnly(Side.CLIENT)
//    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
//        PipeSize size = PipeSize.VALUES[stack.getMetadata()];
//        //TODO localize
//        tooltip.add("Fluid Capacity: " + TextFormatting.BLUE + (capacities[size.ordinal()] * 20) + "L/s");
//        tooltip.add("Heat Limit: " + TextFormatting.RED + heatResistance + " K");
//    }
}
