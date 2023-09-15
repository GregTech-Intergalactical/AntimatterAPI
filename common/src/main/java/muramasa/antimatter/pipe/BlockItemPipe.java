package muramasa.antimatter.pipe;

import muramasa.antimatter.pipe.types.ItemPipe;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import tesseract.TesseractGraphWrappers;
import tesseract.api.ITickingController;

import java.util.List;

public class BlockItemPipe<T extends ItemPipe<T>> extends BlockPipe<T> {

    public BlockItemPipe(T type, PipeSize size) {
        super(type.getId(), type, size, 0);
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
        tooltip.add(new TranslatableComponent("antimatter.tooltip.capacity", new TextComponent(type.getCapacity(getSize()) + "").append(" ").append(new TranslatableComponent("antimatter.tooltip.stacks").append("."))));
        if (!Screen.hasShiftDown()) {
            tooltip.add(new TranslatableComponent("antimatter.tooltip.more").withStyle(ChatFormatting.DARK_AQUA));
        } else {
            tooltip.add(new TextComponent("----------"));
            tooltip.add(new TranslatableComponent("antimatter.pipe.item.info").withStyle(ChatFormatting.DARK_AQUA));
            tooltip.add(new TextComponent("----------"));
        }
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
