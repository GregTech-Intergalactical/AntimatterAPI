package muramasa.antimatter.gui.slot;

import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.capability.item.FakeTrackedItemHandler;
import muramasa.antimatter.capability.machine.MachineItemHandler;
import muramasa.antimatter.gui.SlotType;
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
        return settable;
    }

    @Override
    public int getItemStackLimit(@Nonnull ItemStack stack) {
        if (settable){
            return stack.getMaxStackSize();
        }
        return super.getItemStackLimit(stack);
    }

    @Override
    @Nonnull
    public ItemStack decrStackSize(int amount) {
        if (!settable || !(this.getItemHandler() instanceof FakeTrackedItemHandler)) return super.decrStackSize(amount);
        return MachineItemHandler.extractFromInput(this.getItemHandler(), index, amount, false);
    }

    @Override
    public void onSlotChange(@Nonnull ItemStack p_75220_1_, @Nonnull ItemStack p_75220_2_) {
    }

    @Override
    public void putStack(@Nonnull ItemStack stack) {
        super.putStack(stack);
    }

    public boolean isSettable() {
        return settable;
    }
}
