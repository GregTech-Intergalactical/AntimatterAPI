package muramasa.antimatter.gui.container;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.capability.item.TrackedItemHandler;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.slot.SlotFake;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.Set;

public abstract class AntimatterContainer extends Container implements IAntimatterContainer {

    protected PlayerInventory playerInv;
    protected int invSize;
    public final GuiInstance handler;
    public final Set<IContainerListener> listeners = new ObjectOpenHashSet<>();

    public AntimatterContainer(IGuiHandler handler, ContainerType<?> containerType, int windowId, PlayerInventory playerInv, int invSize) {
        super(containerType, windowId);
        this.playerInv = playerInv;
        this.invSize = invSize;
        this.handler = new GuiInstance(handler, this, handler.isRemote());
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        this.listeners.add(listener);
    }

    @Override
    public void removeListener(IContainerListener listener) {
        super.removeListener(listener);
        this.listeners.remove(listener);
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

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        source().update();
    }

    public ItemStack func_241440_b_(int slotID, int clickedButton, ClickType clickType, PlayerEntity playerEntity){
        PlayerInventory playerinventory = playerEntity.inventory;
        ItemStack itemstack = ItemStack.EMPTY;
        if ((clickType == ClickType.PICKUP || clickType == ClickType.QUICK_MOVE) && (clickedButton == 0 || clickedButton == 1)) {
            if (clickType == ClickType.QUICK_MOVE) {
                if (slotID < 0) {
                    return ItemStack.EMPTY;
                }

                Slot slot5 = this.inventorySlots.get(slotID);
                if (slot5 == null || !slot5.canTakeStack(playerEntity)) {
                    return ItemStack.EMPTY;
                }

                if (!(slot5 instanceof SlotFake) || !((SlotFake)slot5).isSettable()){
                    return super.func_241440_b_(slotID, clickedButton, clickType, playerEntity);
                }

                for(ItemStack itemstack8 = this.transferStackInSlot(playerEntity, slotID); !itemstack8.isEmpty() && ItemStack.areItemsEqual(slot5.getStack(), itemstack8); itemstack8 = this.transferStackInSlot(playerEntity, slotID)) {
                    itemstack = itemstack8.copy();
                }
            } else {
                if (slotID < 0) {
                    return ItemStack.EMPTY;
                }

                Slot slot6 = this.inventorySlots.get(slotID);

                if (!(slot6 instanceof SlotFake) || !((SlotFake)slot6).isSettable()){
                    return super.func_241440_b_(slotID, clickedButton, clickType, playerEntity);
                }
                ItemStack itemstack9 = slot6.getStack();
                ItemStack itemstack11 = playerinventory.getItemStack();
                if (!itemstack9.isEmpty()) {
                    itemstack = itemstack9.copy();
                }

                if (itemstack9.isEmpty()) {
                    if (!itemstack11.isEmpty() && slot6.isItemValid(itemstack11)) {
                        int j2 = clickedButton == 0 ? itemstack11.getCount() : 1;
                        if (j2 > slot6.getItemStackLimit(itemstack11)) {
                            j2 = slot6.getItemStackLimit(itemstack11);
                        }

                        slot6.putStack(itemstack11.split(j2));
                    }
                } else if (slot6.canTakeStack(playerEntity)) {
                    if (itemstack11.isEmpty()) {
                        if (itemstack9.isEmpty()) {
                            slot6.putStack(ItemStack.EMPTY);
                            playerinventory.setItemStack(ItemStack.EMPTY);
                        } else {
                            int k2 = clickedButton == 0 ? itemstack9.getCount() : (itemstack9.getCount() + 1) / 2;
                            playerinventory.setItemStack(slot6.decrStackSize(k2));
                            if (itemstack9.isEmpty()) {
                                slot6.putStack(ItemStack.EMPTY);
                            }

                            slot6.onTake(playerEntity, playerinventory.getItemStack());
                        }
                    } else if (slot6.isItemValid(itemstack11)) {
                        if (areItemsAndTagsEqual(itemstack9, itemstack11)) {
                            int l2 = clickedButton == 0 ? itemstack11.getCount() : 1;
                            if (l2 > slot6.getItemStackLimit(itemstack11) - itemstack9.getCount()) {
                                l2 = slot6.getItemStackLimit(itemstack11) - itemstack9.getCount();
                            }

                            if (l2 > itemstack11.getMaxStackSize() - itemstack9.getCount()) {
                                l2 = itemstack11.getMaxStackSize() - itemstack9.getCount();
                            }

                            itemstack11.shrink(l2);
                            itemstack9.grow(l2);
                        } else if (itemstack11.getCount() <= slot6.getItemStackLimit(itemstack11)) {
                            slot6.putStack(itemstack11);
                            playerinventory.setItemStack(itemstack9);
                        }
                    } else if (itemstack11.getMaxStackSize() > 1 && areItemsAndTagsEqual(itemstack9, itemstack11) && !itemstack9.isEmpty()) {
                        int i3 = itemstack9.getCount();
                        if (i3 + itemstack11.getCount() <= itemstack11.getMaxStackSize()) {
                            itemstack11.grow(i3);
                            itemstack9 = slot6.decrStackSize(i3);
                            if (itemstack9.isEmpty()) {
                                slot6.putStack(ItemStack.EMPTY);
                            }

                            slot6.onTake(playerEntity, playerinventory.getItemStack());
                        }
                    }
                }

                slot6.onSlotChanged();
                return itemstack;
            }
        } /*else if (clickType == ClickType.SWAP) {
            Slot slot = this.inventorySlots.get(slotID);
            ItemStack itemstack1 = playerinventory.getStackInSlot(clickedButton);
            ItemStack itemstack2 = slot.getStack();
            if (!itemstack1.isEmpty() || !itemstack2.isEmpty()) {
                if (itemstack1.isEmpty()) {
                    if (slot.canTakeStack(playerEntity)) {
                        playerinventory.setInventorySlotContents(clickedButton, itemstack2);
                        slot.onSwapCraft(itemstack2.getCount());
                        slot.putStack(ItemStack.EMPTY);
                        slot.onTake(playerEntity, itemstack2);
                    }
                } else if (itemstack2.isEmpty()) {
                    if (slot.isItemValid(itemstack1)) {
                        int i = slot.getItemStackLimit(itemstack1);
                        if (itemstack1.getCount() > i) {
                            slot.putStack(itemstack1.split(i));
                        } else {
                            slot.putStack(itemstack1);
                            playerinventory.setInventorySlotContents(clickedButton, ItemStack.EMPTY);
                        }
                    }
                } else if (slot.canTakeStack(playerEntity) && slot.isItemValid(itemstack1)) {
                    int l1 = slot.getItemStackLimit(itemstack1);
                    if (itemstack1.getCount() > l1) {
                        slot.putStack(itemstack1.split(l1));
                        slot.onTake(playerEntity, itemstack2);
                        if (!playerinventory.addItemStackToInventory(itemstack2)) {
                            playerEntity.dropItem(itemstack2, true);
                        }
                    } else {
                        slot.putStack(itemstack1);
                        playerinventory.setInventorySlotContents(clickedButton, itemstack2);
                        slot.onTake(playerEntity, itemstack2);
                    }
                }
            }
        }*/
        return super.func_241440_b_(slotID, clickedButton, clickType, playerEntity);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < invSize) {
                if (!this.mergeItemStack(itemstack1, invSize, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 0, invSize, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            }
            else {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }

    //Because top level doesn't verify anything. Just a 1-1 copy but adds slot.isItemValid.
    @Override
    protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        boolean flag = false;
        int i = startIndex;
        if (reverseDirection) {
            i = endIndex - 1;
        }

        if (stack.isStackable()) {
            while(!stack.isEmpty()) {
                if (reverseDirection) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }

                Slot slot = this.inventorySlots.get(i);
                if (!slot.isItemValid(stack))
                    break;
                ItemStack itemstack = slot.getStack();
                if (!itemstack.isEmpty() && areItemsAndTagsEqual(stack, itemstack)) {
                    int j = itemstack.getCount() + stack.getCount();
                    int maxSize = Math.min(slot.getSlotStackLimit(), stack.getMaxStackSize());
                    if (j <= maxSize) {
                        stack.setCount(0);
                        itemstack.setCount(j);
                        slot.onSlotChanged();
                        if (slot instanceof SlotItemHandler) {
                            SlotItemHandler handler = (SlotItemHandler) slot;
                            IItemHandler handle = handler.getItemHandler();
                            if (handle instanceof TrackedItemHandler<?>) {
                                ((TrackedItemHandler<?>)handle).onContentsChanged(slot.slotNumber);
                            }
                        }
                        flag = true;
                    } else if (itemstack.getCount() < maxSize) {
                        stack.shrink(maxSize - itemstack.getCount());
                        itemstack.setCount(maxSize);
                        slot.onSlotChanged();
                        flag = true;
                    }
                }

                if (reverseDirection) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        if (!stack.isEmpty()) {
            if (reverseDirection) {
                i = endIndex - 1;
            } else {
                i = startIndex;
            }

            while(true) {
                if (reverseDirection) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }

                Slot slot1 = this.inventorySlots.get(i);
                ItemStack itemstack1 = slot1.getStack();
                if (itemstack1.isEmpty() && slot1.isItemValid(stack)) {
                    if (stack.getCount() > slot1.getSlotStackLimit()) {
                        slot1.putStack(stack.split(slot1.getSlotStackLimit()));
                    } else {
                        slot1.putStack(stack.split(stack.getCount()));
                    }

                    slot1.onSlotChanged();
                    flag = true;
                    break;
                }

                if (reverseDirection) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        return flag;
    }

    public PlayerInventory getPlayerInv() {
        return playerInv;
    }

    @Override
    public GuiInstance source() {
        return handler;
    }
}
