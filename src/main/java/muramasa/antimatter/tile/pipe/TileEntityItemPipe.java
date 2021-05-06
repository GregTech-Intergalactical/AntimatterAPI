package muramasa.antimatter.tile.pipe;

import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.pipe.PipeCoverHandler;
import muramasa.antimatter.pipe.types.ItemPipe;
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

import java.util.Arrays;

public class TileEntityItemPipe<T extends ItemPipe<T>> extends TileEntityPipe<T> implements IItemPipe {

    public TileEntityItemPipe(T type) {
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
    public void registerNode(BlockPos pos, Direction side, boolean remove) {
        if (!remove) {
            ItemTileWrapper.wrap(this, getWorld(), pos, side, () -> world.getTileEntity(pos));
        } else {
            Tesseract.ITEM.remove(getWorld(), pos.toLong());
        }
    }

    @Override
    public void onRemove() {
        if (isServerSide()) Tesseract.ITEM.remove(getWorld(), pos.toLong());
        super.onRemove();
    }

    @Override
    public int getCapacity() {
        return getPipeType().getCapacity(getPipeSize());
    }

    @Override
    public boolean connects(Direction direction) {
        return canConnect(direction.getIndex());
    }

    @Override
    protected Capability<?> getCapability() {
        return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    }

    public static class TileEntityCoveredItemPipe<T extends ItemPipe<T>> extends TileEntityItemPipe<T> implements ITickablePipe {

        public TileEntityCoveredItemPipe(T type) {
            super(type);
        }

        @Override
        public LazyOptional<PipeCoverHandler<?>> getCoverHandler() {
            return this.coverHandler;
        }

    }

    @Override
    protected LazyOptional<?> buildCapForSide(Direction side) {
        return LazyOptional.of(() -> new TesseractItemCapability(this, side));
    }
}
