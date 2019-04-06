package muramasa.gtu.api.gui.server;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerBase extends Container {

    public ContainerBase(IInventory playerInv) {
        if (playerInv != null) {
            addPlayerSlots(playerInv);
        }
    }

    private void addPlayerSlots(IInventory playerInv) {
        for (int row = 0; row < 3; ++row) { // Slots for the main inventory
            for (int col = 0; col < 9; ++col) {
                int x = col * 18 + 8;
                int y = row * 18 + 84;
                this.addSlotToContainer(new Slot(playerInv, col + row * 9 + 10, x, y));
            }
        }
        for (int row = 0; row < 9; ++row) { // Slots for the hotbar
            int x = row * 18 + 8;
            int y = 58 + 84;
            this.addSlotToContainer(new Slot(playerInv, row, x, y));
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < inventorySlots.size()) {
                if (!this.mergeItemStack(itemstack1, inventorySlots.size(), this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, inventorySlots.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }
        return itemstack;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        //return !tile.isInvalid() && playerIn.getDistanceSq(tile.getPos().add(0.5D, 0.5D, 0.5D)) <= 64D;
        return true;
    }
}
