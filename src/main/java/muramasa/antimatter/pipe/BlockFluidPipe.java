package muramasa.antimatter.pipe;

import muramasa.antimatter.pipe.types.FluidPipe;
import muramasa.antimatter.pipe.types.PipeType;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import tesseract.api.fluid.IFluidPipe;
import tesseract.util.Dir;

import javax.annotation.Nonnull;

public class BlockFluidPipe extends BlockPipe<FluidPipe<?>> implements IFluidPipe {

    public BlockFluidPipe(PipeType<?> type, PipeSize size) {
        super(type.getId(), type, size);
    }

    @Override
    public boolean canConnect(IBlockReader world, BlockState state, BlockPos pos) {
        return state.getBlock() instanceof BlockFluidPipe;
    }

    @Override
    public boolean isGasProof() {
        return getType().isGasProof();
    }

    @Override
    public int getLoss() {
        return getType().getLoss();
    }

    @Override
    public int getCapacity() {
        return getType().getCapacity(getSize());
    }

    @Override
    public long getPressure() {
        return getType().getPressure();
    }

    @Override
    public long getTemp() {
        return getType().getTemp();
    }

    @Override
    public boolean connects(@Nonnull Dir direction) {
        return true;
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving) {
        //if (!world.isRemote())
    }

    @Override
    public void onReplaced(BlockState state, World world, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
        //if (!world.isRemote())
        super.onReplaced(state, world, pos, newState, isMoving);
    }

    @Override
    public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state) {
        //if (!worldIn.isRemote())
    }

    @Override
    public void onExplosionDestroy(World worldIn, BlockPos pos, Explosion explosionIn) {
        //if (!worldIn.isRemote())
    }

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
