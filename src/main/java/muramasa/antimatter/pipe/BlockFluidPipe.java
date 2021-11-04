package muramasa.antimatter.pipe;

import muramasa.antimatter.pipe.types.FluidPipe;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import tesseract.Tesseract;
import tesseract.api.ITickingController;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class BlockFluidPipe<T extends FluidPipe<T>> extends BlockPipe<T> {

    public BlockFluidPipe(T type, PipeSize size) {
        super(type.getId(), type, size, 0);
    }

    @Override
    public List<String> getInfo(List<String> info, World world, BlockState state, BlockPos pos) {
        ITickingController<?,?,?> controller = Tesseract.FLUID.getController(world, pos.toLong());
        if (controller != null) controller.getInfo(pos.toLong(), info);
        info.add("Pressure: " + getType().getPressure(getSize()));
        info.add("Capacity: " + getType().getCapacity(getSize()));
        info.add("Max temperature: " + getType().getTemperature());
        info.add(getType().isGasProof() ? "Gas proof." : "Cannot handle gas.");
        return info;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(new StringTextComponent("Pressure: " + getType().getPressure(getSize())));
        tooltip.add(new StringTextComponent("Capacity: " + getType().getCapacity(getSize())));
        tooltip.add(new StringTextComponent("Max temperature: " + getType().getTemperature()));
    }

    //    @Override
//    public ITextComponent getDisplayName(ItemStack stack) {
//        //TODO add prefix and suffix for local
//        PipeSize size = PipeSize.VALUES[stack.getMetadata()];
//        return (size == PipeSize.NORMAL ? "" : size.getDisplayName() + " ") + material.getDisplayName() + " Fluid Pipe";
//    }
//
//    @Override
//    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
//        PipeSize size = PipeSize.VALUES[stack.getMetadata()];
//        //TODO localize
//        tooltip.add("Fluid Capacity: " + TextFormatting.BLUE + (capacities[size.ordinal()] * 20) + "L/s");
//        tooltip.add("Heat Limit: " + TextFormatting.RED + heatResistance + " K");
//    }
}
