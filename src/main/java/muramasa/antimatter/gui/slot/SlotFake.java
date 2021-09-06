package muramasa.antimatter.gui.slot;

import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

//TODO: Should be used on filters
public class SlotFake extends AbstractSlot<SlotFake> {
    final boolean settable;
    public SlotFake(SlotType<SlotFake> type, IGuiHandler tile, IItemHandler stackHandler, int index, int x, int y, boolean settable) {
        super(type, tile, stackHandler, index, x, y);
        this.settable = settable;
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        return settable;
    }

    @Override
    public boolean canTakeStack(PlayerEntity playerIn) {
        return false;
    }

    @Override
    public void onSlotChange(@Nonnull ItemStack p_75220_1_, @Nonnull ItemStack p_75220_2_) {
    }

    @Override
    public void putStack(@Nonnull ItemStack stack) {
        ItemStack stack1 = settable ? stack.copy() : stack;
        super.putStack(stack1);
    }
}
