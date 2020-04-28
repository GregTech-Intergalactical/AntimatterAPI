package muramasa.antimatter.capability.node;

import it.unimi.dsi.fastutil.ints.IntList;
import muramasa.antimatter.capability.impl.ItemStackWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import tesseract.TesseractAPI;
import tesseract.api.item.IItemNode;
import tesseract.api.item.ItemData;
import tesseract.util.Dir;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemNode implements IItemNode {

    private TileEntity tile;
    private ItemStackWrapper handler;

    public ItemNode(TileEntity tile, ItemStackHandler handler) {
        this.tile = tile;
        this.handler = new ItemStackWrapper(handler);

        World world = tile.getWorld();
        if (world != null)
            TesseractAPI.registerItemNode(world.getDimension().getType().getId(), tile.getPos().toLong(), this);
    }

    @Override
    public void remove() {
        World world = tile.getWorld();
        if (world != null)
            TesseractAPI.removeItem(world.getDimension().getType().getId(), tile.getPos().toLong());
    }

    @Override
    public int insert(@Nonnull ItemData data, boolean simulate) {
        ItemStack stack = (ItemStack) data.getStack();
        ItemData item = handler.findItemInSlots(stack);
        if (item != null) return handler.insertItem(item.getSlot(), (ItemStack) item.getStack(), simulate).getCount();
        if (!simulate) return handler.setFirstEmptySlot(stack);
        int slot = handler.getFirstEmptySlot();
        return slot != -1 ? stack.getCount() : 0;
    }

    @Nullable
    @Override
    public ItemData extract(int slot, int amount, boolean simulate) {
        ItemStack stack = handler.extractItem(slot, amount, simulate);
        return stack.isEmpty() ? null : new ItemData(slot, stack, stack.getItem());
    }

    @Nonnull
    @Override
    public IntList getAvailableSlots(@Nonnull Dir direction) {
        return handler.getAvailableSlots(direction);
    }

    @Override
    public int getOutputAmount(@Nonnull Dir direction) {
        return 1;
    }

    @Override
    public int getPriority(@Nonnull Dir direction) {
        return 0;
    }

    @Override
    public boolean isEmpty(int slot) {
        return handler.getStackInSlot(slot).isEmpty();
    }

    @Override
    public boolean canOutput() {
        return handler != null;
    }

    @Override
    public boolean canInput() {
        return handler != null;
    }

    @Override
    public boolean canOutput(@Nonnull Dir direction) {
        return true; // TODO: Should depend on nearest pipe cover
    }

    @Override
    public boolean canInput(@Nonnull Object item, @Nonnull Dir direction) {
        return handler.isItemAvailable(item, direction) && (handler.findItemInSlots(item) != null || handler.getFirstEmptySlot() != -1);
    }

    @Override
    public boolean connects(@Nonnull Dir direction) {
        return true;
    }
}
