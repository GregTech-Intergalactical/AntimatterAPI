package muramasa.antimatter.tile.pipe;

import muramasa.antimatter.pipe.types.ItemPipe;
import muramasa.antimatter.pipe.types.PipeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import tesseract.Tesseract;
import tesseract.api.capability.TesseractItemCapability;
import tesseract.api.item.IItemPipe;
import tesseract.util.Dir;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityItemPipe extends TileEntityPipe implements IItemPipe {

    public TileEntityItemPipe(PipeType<?> type) {
        super(type);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (isServerSide()) Tesseract.ITEM.registerConnector(getDimension(), pos.toLong(), this); // this is connector class
    }

    @Override
    public void refreshConnection() {
        if (isServerSide()) {
            Tesseract.ITEM.remove(getDimension(), pos.toLong());
            Tesseract.ITEM.registerConnector(getDimension(), pos.toLong(), this); // this is connector class
        }
        super.refreshConnection();
    }

    @Override
    public boolean validateTile(TileEntity tile, Direction side) {
        return tile instanceof TileEntityItemPipe || tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side).isPresent();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (side == null) return LazyOptional.empty();
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return LazyOptional.of(() -> new TesseractItemCapability(this, side)).cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public void onRemove() {
        if (isServerSide()) Tesseract.ITEM.remove(getDimension(), pos.toLong());
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
