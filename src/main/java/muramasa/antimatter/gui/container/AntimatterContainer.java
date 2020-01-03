package muramasa.antimatter.gui.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;

public abstract class AntimatterContainer extends Container {

    protected PlayerInventory playerInv;
    protected int invSize;

    public AntimatterContainer(ContainerType<?> containerType, int windowId, PlayerInventory playerInv, int invSize) {
        super(containerType, windowId);
        this.playerInv = playerInv;
        this.invSize = invSize;
    }

    protected void addPlayerSlots() {
        if (playerInv == null) return;
        for (int i = 0; i < 3; ++i) { //Inventory Slots
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int k = 0; k < 9; ++k) { //HotBar Slots
            this.addSlot(new Slot(playerInv, k, 8 + k * 18, 142));
        }
    }

//    @Override
//    public ItemStack transferStackInSlot(PlayerEntity player, int index) {
//        ItemStack itemstack = ItemStack.EMPTY;
//        Slot slot = this.inventorySlots.get(index);
//
//        if (slot != null && slot.getHasStack()) {
//            ItemStack itemstack1 = slot.getStack();
//            itemstack = itemstack1.copy();
//
//            if (index < invSize) {
//                if (!this.mergeItemStack(itemstack1, invSize, this.inventorySlots.size(), true)) {
//                    return ItemStack.EMPTY;
//                }
//            }
//            else if (!this.mergeItemStack(itemstack1, 0, invSize, false)) {
//                return ItemStack.EMPTY;
//            }
//
//            if (itemstack1.getCount() == 0) {
//                slot.putStack(ItemStack.EMPTY);
//            }
//            else {
//                slot.onSlotChanged();
//            }
//        }
//
//        return itemstack;
//    }
}
