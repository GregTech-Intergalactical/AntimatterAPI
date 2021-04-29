package muramasa.antimatter.tile.pipe;

import muramasa.antimatter.Ref;
import muramasa.antimatter.pipe.types.ItemPipe;
import muramasa.antimatter.pipe.types.PipeType;
import muramasa.antimatter.tesseract.ItemTileWrapper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import tesseract.Tesseract;
import tesseract.api.capability.TesseractItemCapability;
import tesseract.api.item.IItemPipe;
import tesseract.util.Dir;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

public class TileEntityItemPipe extends TileEntityPipe implements IItemPipe {

    public TileEntityItemPipe(PipeType<?> type) {
        super(type);
        SIDE_CAPS = Arrays.stream(Ref.DIRS).map(t -> LazyOptional.of(() -> new TesseractItemCapability(this, t))).toArray(LazyOptional[]::new);
    }

    @Override
    protected void initTesseract() {
        if (isServerSide()) Tesseract.ITEM.registerConnector(getWorld(), pos.toLong(), this); // this is connector class
        super.initTesseract();
    }

    @Override
    public void refreshConnection() {
        if (isServerSide()) {
            if (Tesseract.ITEM.remove(getWorld(), pos.toLong())) {
                Tesseract.ITEM.registerConnector(getWorld(), pos.toLong(), this); // this is connector class
            }
        }
        super.refreshConnection();
    }

    @Override
    public boolean validateTile(TileEntity tile, Direction side) {
        return tile instanceof TileEntityItemPipe || tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side).isPresent();
    }

    @Override
    public void cacheNode(BlockPos pos, Direction side, boolean remove) {
        if (!remove) {
            ItemTileWrapper.wrap(this, getWorld(), pos, side, () -> world.getTileEntity(pos));
        } else {
            Tesseract.ITEM.remove(getWorld(), pos.toLong());
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (side == null) return LazyOptional.empty();
        if (!this.canConnect(side.getIndex())) return LazyOptional.empty();
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return SIDE_CAPS[side.getIndex()].cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public void onRemove() {
        if (isServerSide()) Tesseract.ITEM.remove(getWorld(), pos.toLong());
        super.onRemove();
    }

    @Override
    public int getCapacity() {
        return ((ItemPipe<?>)getPipeType()).getCapacity(getPipeSize());
    }

    @Override
    public boolean connects(Dir direction) {
        return canConnect(direction.getIndex());
    }
}
