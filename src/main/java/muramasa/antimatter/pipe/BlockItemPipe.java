package muramasa.antimatter.pipe;

import muramasa.antimatter.pipe.types.ItemPipe;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tesseract.Tesseract;
import tesseract.api.ITickingController;

import java.util.Arrays;
import java.util.List;

public class BlockItemPipe<T extends ItemPipe<T>> extends BlockPipe<T> {

    public BlockItemPipe(T type, PipeSize size) {
        super(type.getId(), type, size, 0);
    }

    @Override
    public List<String> getInfo(List<String> info, World world, BlockState state, BlockPos pos) {
        ITickingController<?, ?, ?> controller = Tesseract.ITEM.getController(world, pos.toLong());
        if (controller != null) controller.getInfo(pos.toLong(), info);
        info.add("Capacity: " + getType().getCapacity(getSize()));
        return info;
    }

//    @Override
//    public ITextComponent getDisplayName(ItemStack stack) {
//        boolean res = stack.getMetadata() > 7;
//        PipeSize size = PipeSize.VALUES[res ? stack.getMetadata() - 8 : stack.getMetadata()];
//        return (size == PipeSize.NORMAL ? "" : size.getDisplayName() + " ") + (res ? "Restrictive " : "") + material.getDisplayName() + " Item Pipe";
//    }
//
//    @Override
//    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
//        boolean res = stack.getMetadata() > 7;
//        PipeSize size = PipeSize.VALUES[res ? stack.getMetadata() - 8 : stack.getMetadata()];
//        tooltip.add("Item Capacity: " + TextFormatting.BLUE + getSlotCount(size) + " Stacks/s");
//        tooltip.add("Routing Value: " + TextFormatting.YELLOW + getStepSize(size, res));
//    }
}
