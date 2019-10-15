//package muramasa.gtu.api.gui.server;
//
//import net.minecraft.client.Minecraft;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.entity.player.PlayerInventory;
//import net.minecraft.inventory.container.Container;
//import net.minecraft.inventory.container.ContainerType;
//import net.minecraft.inventory.container.Slot;
//import net.minecraft.item.ItemStack;
//import net.minecraft.network.PacketBuffer;
//import net.minecraft.tileentity.TileEntity;
//import net.minecraft.util.Hand;
//import net.minecraftforge.fml.DistExecutor;
//
//import javax.annotation.Nullable;
//
//public class ContainerBase extends Container {
//
//    protected int invSize;
//    protected PlayerInventory playerInv;
//
//    public ContainerBase(ContainerType<?> type, int windowId, @Nullable PlayerInventory playerInv, int invSize) {
//        super(type, windowId);
//        this.invSize = invSize;
//        this.playerInv = playerInv;
//    }
//
//    protected void addPlayerSlots() {
//        if (playerInv == null) return;
//        for (int i = 0; i < 3; ++i) { //Inventory Slots
//            for (int j = 0; j < 9; ++j) {
//                this.addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
//            }
//        }
//        for (int k = 0; k < 9; ++k) { //HotBar Slots
//            this.addSlot(new Slot(playerInv, k, 8 + k * 18, 142));
//        }
//    }
//
//    @Override
//    public boolean canInteractWith(PlayerEntity player) {
//        return true;
//    }
//
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
//
//    public static TileEntity getTileFromBuffer(PacketBuffer buf) {
//        return DistExecutor.runForDist(() -> () -> Minecraft.getInstance().world.getTileEntity(buf.readBlockPos()), () -> () -> {
//            throw new RuntimeException("Shouldn't be called on server!");
//        });
//    }
//
//    public static ItemStack getHeldStackFromBuffer(PacketBuffer buf) {
//        return DistExecutor.runForDist(() -> () -> Minecraft.getInstance().player.getHeldItem(buf.readBoolean() ? Hand.MAIN_HAND : Hand.OFF_HAND), () -> () -> {
//            throw new RuntimeException("Shouldn't be called on server!");
//        });
//    }
//}
