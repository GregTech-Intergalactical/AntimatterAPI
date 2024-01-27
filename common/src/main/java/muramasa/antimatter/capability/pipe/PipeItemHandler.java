package muramasa.antimatter.capability.pipe;

import muramasa.antimatter.blockentity.pipe.BlockEntityItemPipe;
import muramasa.antimatter.capability.CoverHandler;
import muramasa.antimatter.capability.item.SidedCombinedInvWrapper;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import tesseract.api.item.ExtendedItemContainer;
import tesseract.graph.Connectivity;

public class PipeItemHandler extends SidedCombinedInvWrapper {
    BlockEntityItemPipe<?> pipe;
    public PipeItemHandler(Direction side, BlockEntityItemPipe<?> pipe, CoverHandler<?> coverHandler, ExtendedItemContainer... itemHandler) {
        super(side, coverHandler, itemHandler);
        this.pipe = pipe;
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (Connectivity.has(pipe.mDisabledInputs, side.get3DDataValue())){
            return stack;
        }
        ItemStack superInsert = super.insertItem(slot, stack, simulate);
        if (superInsert.getCount() < stack.getCount() && !simulate){
            pipe.mLastReceivedFrom = (byte) side.get3DDataValue();
        }
        return superInsert;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        return ItemStack.EMPTY;
    }
}
