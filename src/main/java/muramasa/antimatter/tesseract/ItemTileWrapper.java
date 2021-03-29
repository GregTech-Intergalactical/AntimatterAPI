package muramasa.antimatter.tesseract;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import tesseract.Tesseract;
import tesseract.api.item.IItemNode;
import tesseract.util.Dir;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

public class ItemTileWrapper implements IItemNode {

    private final TileEntity tile;
    private final IItemHandler handler;

    private ItemTileWrapper(TileEntity tile, IItemHandler handler) {
        this.tile = tile;
        this.handler = handler;
    }

    @Nullable
    public static ItemTileWrapper wrap(World world, BlockPos pos, Direction side, Supplier<TileEntity> supplier) {
        Tesseract.ITEM.registerNode(world.getDimensionKey(),pos.toLong(), () -> {
            TileEntity tile = supplier.get();
            LazyOptional<IItemHandler> capability = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite());
            if (capability.isPresent()) {
                ItemTileWrapper node = new ItemTileWrapper(tile, capability.orElse(null));
                capability.addListener(o -> node.onRemove());
                return node;
            }
            throw new RuntimeException("invalid capability");
        });
        return null;
    }

    public void onRemove() {
        if (tile.isRemoved()) {
            Tesseract.ITEM.remove(tile.getWorld().getDimensionKey(), tile.getPos().toLong());
        }
    }


    @Override
    public int getPriority(Dir direction) {
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
    public boolean canOutput(Dir direction) {
        return true;
    }

    @Override
    public boolean connects(Dir direction) {
        return true;
    }

    @Override
    public int getSlots() {
        return handler.getSlots();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return handler.getStackInSlot(slot);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return handler.insertItem(slot, stack, simulate);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return handler.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return handler.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return handler.isItemValid(slot, stack);
    }
}
