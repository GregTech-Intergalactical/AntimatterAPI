package muramasa.antimatter.pipe;

import muramasa.antimatter.pipe.types.FluidPipe;
import muramasa.antimatter.pipe.types.PipeType;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import tesseract.Tesseract;
import tesseract.api.ITickingController;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

import static muramasa.antimatter.pipe.PipeType.FLUID;

public class BlockFluidPipe extends BlockPipe<FluidPipe<?>> {

    public BlockFluidPipe(PipeType<?> type, PipeSize size) {
        super(type.getId(), type, size);
    }

    @Override
    public List<String> getInfo(List<String> info, World world, BlockState state, BlockPos pos) {
        ITickingController controller = Tesseract.FLUID.getController(world.getDimension().getType().getId(), pos.toLong());
        if (controller != null) info.addAll(Arrays.asList(controller.getInfo()));
        return info;
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
