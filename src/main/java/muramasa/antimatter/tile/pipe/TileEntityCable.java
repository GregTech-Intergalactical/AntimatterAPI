package muramasa.antimatter.tile.pipe;

import muramasa.antimatter.pipe.types.Cable;
import muramasa.antimatter.pipe.types.PipeType;
import muramasa.antimatter.tesseract.EnergyTileWrapper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import tesseract.Tesseract;
import tesseract.api.capability.TesseractGTCapability;
import tesseract.api.gt.IGTCable;
import tesseract.util.Dir;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityCable extends TileEntityPipe implements IGTCable {

    public TileEntityCable(PipeType<?> type) {
        super(type);
    }

    @Override
    public void refreshConnection() {
        if (isServerSide()) {
            Tesseract.GT_ENERGY.remove(getDimension(), pos.toLong());
            Tesseract.GT_ENERGY.registerConnector(getDimension(), pos.toLong(), this); // this is connector class
        }
        super.refreshConnection();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (isServerSide()) Tesseract.GT_ENERGY.registerConnector(getDimension(), pos.toLong(), this); // this is connector class
    }

    @Override
    public void onRemove() {
        if (isServerSide()) Tesseract.GT_ENERGY.remove(getDimension(), pos.toLong());
        super.onRemove();
    }

    @Override
    public void cacheNode(BlockPos pos, Direction side, boolean remove) {
        if (!remove) {
                EnergyTileWrapper.wrap(getWorld(), pos, side, () -> world.getTileEntity(pos));
        } else {
            PipeReferenceCounter.remove(getWorld().getDimensionKey(), pos.toLong(), TileEntityCable.class, p -> Tesseract.GT_ENERGY.remove(getWorld().getDimensionKey(), p));
        }
    }

    @Override
    public boolean validateTile(TileEntity tile, Direction side) {
        return tile instanceof TileEntityCable || tile.getCapability(TesseractGTCapability.ENERGY_HANDLER_CAPABILITY, side).isPresent();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (side == null) return LazyOptional.empty();
        if (!this.canConnect(side.getIndex())) return LazyOptional.empty();
        if (cap == TesseractGTCapability.ENERGY_HANDLER_CAPABILITY) {
            return LazyOptional.of(() -> new TesseractGTCapability(this, side)).cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public int getVoltage() {
        return ((Cable<?>)getPipeType()).getTier().getVoltage();
    }

    @Override
    public int getLoss() {
        return ((Cable<?>)getPipeType()).getLoss();
    }

    @Override
    public int getAmps() {
        return ((Cable<?>)getPipeType()).getAmps(getPipeSize());
    }

    @Override
    public boolean connects(Dir direction) {
        return canConnect(direction.getIndex());
    }
}
