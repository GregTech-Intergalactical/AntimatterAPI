package muramasa.antimatter.gui.slot;

import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.capability.item.FakeTrackedItemHandler;
import muramasa.antimatter.capability.machine.MachineItemHandler;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

//TODO: Should be used on filters
public class SlotFake extends AbstractSlot<SlotFake> implements IClickableSlot {
    final boolean settable;

    public SlotFake(SlotType<SlotFake> type, IGuiHandler tile, IItemHandler stackHandler, int index, int x, int y, boolean settable) {
        super(type, tile, stackHandler, index, x, y);
        this.settable = settable;
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
        return settable;
    }

    @Override
    public boolean mayPickup(PlayerEntity playerIn) {
        return settable;
    }

    @Override
    public int getMaxStackSize(@Nonnull ItemStack stack) {
        if (settable) {
            return 1;
        }
        return super.getMaxStackSize(stack);
    }

    @Override
    @Nonnull
    public ItemStack remove(int amount) {
        if (!settable || !(this.getItemHandler() instanceof FakeTrackedItemHandler)) return super.remove(amount);
        return MachineItemHandler.extractFromInput(this.getItemHandler(), index, amount, false);
    }

    @Override
    public void onQuickCraft(@Nonnull ItemStack p_75220_1_, @Nonnull ItemStack p_75220_2_) {
    }

    @Override
    public void setChanged() {

    }

    @Override
    public void set(@Nonnull ItemStack stack) {
        super.set(stack);
    }

    public boolean isSettable() {
        return settable;
    }

    public ItemStack clickSlot(int clickedButton, ClickType clickType, PlayerEntity playerEntity, Container container) {
        if (!settable) return ItemStack.EMPTY;
        PlayerInventory playerinventory = playerEntity.inventory;
        ItemStack itemstack = playerinventory.getCarried().copy();
        if ((clickType == ClickType.PICKUP || clickType == ClickType.SWAP) && (clickedButton == 0 || clickedButton == 1)) {

            ItemStack slotStack = this.getItem();
            ItemStack heldStack = playerinventory.getCarried().copy();
            if (!slotStack.isEmpty()) {
                itemstack = slotStack.copy();
            }
            this.set(heldStack.isEmpty() ? ItemStack.EMPTY : Utils.ca(this.getMaxStackSize(heldStack), heldStack));
            this.setChanged();
        }
        return itemstack;
    }
}
