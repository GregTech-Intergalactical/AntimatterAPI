package muramasa.antimatter.pipe;

import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.machine.MachineFlag;
import muramasa.antimatter.pipe.types.FluidPipe;
import muramasa.antimatter.pipe.types.PipeType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import tesseract.TesseractAPI;
import tesseract.api.fluid.IFluidPipe;
import tesseract.graph.ITickingController;
import tesseract.util.Dir;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class BlockFluidPipe extends BlockPipe<FluidPipe<?>> implements IFluidPipe {

    public BlockFluidPipe(PipeType<?> type, PipeSize size) {
        super(type.getId(), type, size);
    }

    @Override
    public boolean canConnect(IBlockReader world, BlockState state, BlockPos pos) {
        Block block = state.getBlock();
        return block instanceof BlockMachine ? ((BlockMachine)block).getType().has(MachineFlag.FLUID) : block instanceof BlockFluidPipe;
    }

    @Override
    public boolean isGasProof() {
        return getType().isGasProof();
    }

    @Override
    public int getCapacity() {
        return getType().getCapacity(getSize());
    }

    @Override
    public int getPressure() {
        return getType().getPressure(getSize());
    }

    @Override
    public int getTemperature() {
        return getType().getTemperature();
    }

    @Override
    public boolean connects(@Nonnull Dir direction) {
        return true;
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!world.isRemote()) TesseractAPI.registerFluidPipe(world.getDimension().getType().getId(), pos.toLong(), this);
    }

    @Override
    public void onReplaced(BlockState state, World world, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
        if (!world.isRemote()) TesseractAPI.removeFluid(world.getDimension().getType().getId(), pos.toLong());
        super.onReplaced(state, world, pos, newState, isMoving);
    }

    @Override
    public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state) {
        if (!worldIn.isRemote()) TesseractAPI.removeFluid(worldIn.getDimension().getType().getId(), pos.toLong());
    }

    @Override
    public void onExplosionDestroy(World worldIn, BlockPos pos, Explosion explosionIn) {
        if (!worldIn.isRemote()) TesseractAPI.removeFluid(worldIn.getDimension().getType().getId(), pos.toLong());
    }

    @Override
    public List<String> getInfo(List<String> info, World world, BlockState state, BlockPos pos) {
        ITickingController controller = TesseractAPI.getFluidController(world.getDimension().getType().getId(), pos.toLong());
        if (controller != null) info.addAll(Arrays.asList(controller.getInfo()));
        return info;
    }

    /*@Override
    public void updateNeighbors(@Nonnull BlockState stateIn, @Nonnull IWorld worldIn, @Nonnull BlockPos pos, int flags) {
        if (worldIn.isRemote()) return;
    }*/

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
