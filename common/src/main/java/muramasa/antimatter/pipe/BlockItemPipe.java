package muramasa.antimatter.pipe;

import muramasa.antimatter.pipe.types.ItemPipe;
import muramasa.antimatter.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import tesseract.TesseractGraphWrappers;
import tesseract.api.ITickingController;

import java.util.List;
import java.util.Map;

public class BlockItemPipe<T extends ItemPipe<T>> extends BlockPipe<T> {
    final boolean restricted;

    public BlockItemPipe(T type, PipeSize size, boolean restricted) {
        super((restricted ? "restrictive_" : "") + type.getId(), type, size, 0);
        this.restricted = restricted;
    }

    @Override
    public List<String> getInfo(List<String> info, Level world, BlockState state, BlockPos pos) {
        if (world.isClientSide) return info;
        ITickingController<?, ?, ?> controller = TesseractGraphWrappers.ITEM.getController(world, pos.asLong());
        controller.getInfo(pos.asLong(), info);
        info.add("Capacity: " + getType().getCapacity(getSize()));
        return info;
    }

    @Override
    public void appendHoverText(ItemStack p_49816_, @Nullable BlockGetter p_49817_, List<Component> tooltip, TooltipFlag p_49819_) {
        super.appendHoverText(p_49816_, p_49817_, tooltip, p_49819_);
        tooltip.add(Utils.translatable("antimatter.tooltip.stepsize", (type.getStepsize(getSize()) * (restricted ? 100 : 1))).withStyle(ChatFormatting.AQUA));
        tooltip.add(Utils.translatable("antimatter.tooltip.capacity", Utils.literal(type.getCapacity(getSize()) + "/s").withStyle(ChatFormatting.WHITE)).withStyle(ChatFormatting.AQUA));
        if (!Screen.hasShiftDown()) {
            tooltip.add(Utils.translatable("antimatter.tooltip.more").withStyle(ChatFormatting.DARK_AQUA));
        } else {
            tooltip.add(Utils.literal("----------"));
            tooltip.add(Utils.translatable("antimatter.pipe.item.info").withStyle(ChatFormatting.DARK_AQUA));
            tooltip.add(Utils.literal("----------"));
        }
    }

    public boolean isRestricted() {
        return restricted;
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
