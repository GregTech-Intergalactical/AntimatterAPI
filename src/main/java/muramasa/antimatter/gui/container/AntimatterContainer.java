package muramasa.antimatter.gui.container;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.capability.item.TrackedItemHandler;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.slot.SlotFake;
import muramasa.antimatter.util.Utils;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.Set;

public abstract class AntimatterContainer extends Container implements IAntimatterContainer {

    protected PlayerInventory playerInv;
    protected int invSize;
    public final GuiInstance handler;
    public final Set<IContainerListener> listeners = new ObjectOpenHashSet<>();
    private final ContainerType<?> containerType;

    public AntimatterContainer(IGuiHandler handler, ContainerType<?> containerType, int windowId, PlayerInventory playerInv, int invSize) {
        super(containerType, windowId);
        this.playerInv = playerInv;
        this.invSize = invSize;
        this.handler = new GuiInstance(handler, this, handler.isRemote());
        this.containerType = containerType;
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

    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
        if (slotId >= 0 && this.getSlot(slotId) instanceof SlotFake && ((SlotFake)this.getSlot(slotId)).isSettable()){
            try {
                return this.clickSlot(slotId, dragType, clickTypeIn, player);
            } catch (Exception exception) {
                CrashReport crashreport = CrashReport.makeCrashReport(exception, "Container click");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Click info");
                crashreportcategory.addDetail("Menu Type", () -> {
                    return this.containerType != null ? Registry.MENU.getKey(this.containerType).toString() : "<no type>";
                });
                crashreportcategory.addDetail("Menu Class", () -> {
                    return this.getClass().getCanonicalName();
                });
                crashreportcategory.addDetail("Slot Count", this.inventorySlots.size());
                crashreportcategory.addDetail("Slot", slotId);
                crashreportcategory.addDetail("Button", dragType);
                crashreportcategory.addDetail("Type", clickTypeIn);
                throw new ReportedException(crashreport);
            }
        }
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    public ItemStack clickSlot(int slotID, int clickedButton, ClickType clickType, PlayerEntity playerEntity){
        PlayerInventory playerinventory = playerEntity.inventory;
        ItemStack itemstack = playerinventory.getItemStack().copy();
        if ((clickType == ClickType.PICKUP || clickType == ClickType.SWAP) && (clickedButton == 0 || clickedButton == 1)) {
            if (slotID < 0) {
                return ItemStack.EMPTY;
            }

            Slot slot6 = this.inventorySlots.get(slotID);

            ItemStack slotStack = slot6.getStack();
            ItemStack heldStack = playerinventory.getItemStack().copy();
            if (!slotStack.isEmpty()) {
                itemstack = slotStack.copy();
            }
            slot6.putStack(heldStack.isEmpty() ? ItemStack.EMPTY : Utils.ca(slot6.getItemStackLimit(heldStack), heldStack));
            slot6.onSlotChanged();
        }
        return itemstack;
        //return super.func_241440_b_(slotID, clickedButton, clickType, playerEntity);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            itemstack = slotStack.copy();

            if (index < invSize) {
                if (!this.mergeItemStack(slotStack, invSize, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(slotStack, 0, invSize, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.getCount() == 0) {
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
                boolean continueLoop = false;
                if (slot instanceof SlotFake || !slot.isItemValid(stack)){
                    continueLoop = true;
                }
                ItemStack itemstack = slot.getStack();
                if (!continueLoop && !itemstack.isEmpty() && areItemsAndTagsEqual(stack, itemstack)) {
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
                boolean continueLoop = false;
                if (slot1 instanceof SlotFake){
                    continueLoop = true;
                }
                ItemStack itemstack1 = slot1.getStack();
                if (!continueLoop && itemstack1.isEmpty() && slot1.isItemValid(stack)) {
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
