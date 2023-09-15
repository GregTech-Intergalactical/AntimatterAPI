package muramasa.antimatter.gui.container;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.capability.item.TrackedItemHandler;
import muramasa.antimatter.common.event.CommonEvents;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.slot.AbstractSlot;
import muramasa.antimatter.gui.slot.IClickableSlot;
import muramasa.antimatter.gui.slot.SlotFake;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import tesseract.api.item.ExtendedItemContainer;

import java.util.Set;

public abstract class AntimatterContainer extends AbstractContainerMenu implements IAntimatterContainer {

    protected Inventory playerInv;
    protected int invSize;
    public final GuiInstance handler;
    public final Set<ServerPlayer> listeners = new ObjectOpenHashSet<>();

    public final NonNullList<Slot> playerSlots = NonNullList.create();
    private final MenuType<?> containerType;

    public AntimatterContainer(IGuiHandler handler, MenuType<?> containerType, int windowId, Inventory playerInv, int invSize) {
        super(containerType, windowId);
        this.playerInv = playerInv;
        this.invSize = invSize;
        this.handler = new GuiInstance(handler, this, handler.isRemote());
        this.containerType = containerType;
        //TODO move this to event
        if (AntimatterPlatformUtils.isFabric()){
            CommonEvents.onContainerOpen(playerInv.player, this);
        }
    }

    @Override
    public Set<ServerPlayer> listeners() {
        return listeners;
    }

    protected void addPlayerSlots() {
        if (playerInv == null) return;
        for (int i = 0; i < 3; ++i) { //Inventory Slots
            for (int j = 0; j < 9; ++j) {
                Slot slot = new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18);
                this.addSlot(slot);
                playerSlots.add(slot);
            }
        }
        for (int k = 0; k < 9; ++k) { //HotBar Slots
            Slot slot = new Slot(playerInv, k, 8 + k * 18, 142);
            this.addSlot(slot);
            playerSlots.add(slot);
        }
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        //NOTE: event to add player listener is fired after first sync, so check if the player exists.
        if (listeners().size() == 0) return;
        source().update();
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
        if (slotId >= 0 && this.getSlot(slotId) instanceof IClickableSlot) {
            try {
                ((IClickableSlot) this.getSlot(slotId)).clickSlot(dragType, clickTypeIn, player, this);
            } catch (Exception exception) {
                CrashReport crashreport = CrashReport.forThrowable(exception, "Container click");
                CrashReportCategory crashreportcategory = crashreport.addCategory("Click info");
                crashreportcategory.setDetail("Menu Type", () -> {
                    return this.containerType != null ? Registry.MENU.getKey(this.containerType).toString() : "<no type>";
                });
                crashreportcategory.setDetail("Menu Class", () -> {
                    return this.getClass().getCanonicalName();
                });
                crashreportcategory.setDetail("Slot Count", this.slots.size());
                crashreportcategory.setDetail("Slot", slotId);
                crashreportcategory.setDetail("Button", dragType);
                crashreportcategory.setDetail("Type", clickTypeIn);
                throw new ReportedException(crashreport);
            }
        }
        super.clicked(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();

            if (index < invSize) {
                if (!this.moveItemStackTo(slotStack, invSize, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(slotStack, 0, invSize, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    //Because top level doesn't verify anything. Just a 1-1 copy but adds slot.isItemValid.
    @Override
    protected boolean moveItemStackTo(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        boolean flag = false;
        int i = startIndex;
        if (reverseDirection) {
            i = endIndex - 1;
        }

        if (stack.isStackable()) {
            while (!stack.isEmpty()) {
                if (reverseDirection) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }

                Slot slot = this.slots.get(i);
                boolean continueLoop = false;
                if (slot instanceof SlotFake || !slot.mayPlace(stack)) {
                    continueLoop = true;
                }
                ItemStack itemstack = slot.getItem();
                if (!continueLoop && !itemstack.isEmpty() && itemstack.sameItem(stack)) {
                    int j = itemstack.getCount() + stack.getCount();
                    int maxSize = Math.min(slot.getMaxStackSize(), stack.getMaxStackSize());
                    if (j <= maxSize) {
                        stack.setCount(0);
                        itemstack.setCount(j);
                        slot.setChanged();
                        if (slot instanceof AbstractSlot<?> abstractSlot) {
                            ExtendedItemContainer handle = abstractSlot.getContainer();
                            if (handle instanceof TrackedItemHandler<?>) {
                                ((TrackedItemHandler<?>) handle).onContentsChanged(slot.index);
                            }
                        }
                        flag = true;
                    } else if (itemstack.getCount() < maxSize) {
                        stack.shrink(maxSize - itemstack.getCount());
                        itemstack.setCount(maxSize);
                        slot.setChanged();
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

            while (true) {
                if (reverseDirection) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }

                Slot slot1 = this.slots.get(i);
                boolean continueLoop = false;
                if (slot1 instanceof SlotFake) {
                    continueLoop = true;
                }
                ItemStack itemstack1 = slot1.getItem();
                if (!continueLoop && itemstack1.isEmpty() && slot1.mayPlace(stack)) {
                    if (stack.getCount() > slot1.getMaxStackSize()) {
                        slot1.set(stack.split(slot1.getMaxStackSize()));
                    } else {
                        slot1.set(stack.split(stack.getCount()));
                    }

                    slot1.setChanged();
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

    public Inventory getPlayerInv() {
        return playerInv;
    }

    @Override
    public GuiInstance source() {
        return handler;
    }
}
