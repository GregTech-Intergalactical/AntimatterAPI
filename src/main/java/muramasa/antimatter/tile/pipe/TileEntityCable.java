package muramasa.antimatter.tile.pipe;

import muramasa.antimatter.Ref;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

public class TileEntityCable extends TileEntityPipe implements IGTCable {

    public TileEntityCable(PipeType<?> type) {
        super(type);
        SIDE_CAPS = Arrays.stream(Ref.DIRS).map(t -> LazyOptional.of(() -> new TesseractGTCapability(this, t))).toArray(LazyOptional[]::new);
    }

    @Override
    public void refreshConnection() {
        if (isServerSide()) {
            if (Tesseract.GT_ENERGY.remove(getWorld(), pos.toLong())) {
                Tesseract.GT_ENERGY.registerConnector(getWorld(), pos.toLong(), this); // this is connector class
            }
        }
        super.refreshConnection();
    }

    @Override
    protected void initTesseract() {
        if (isServerSide()) Tesseract.GT_ENERGY.registerConnector(getWorld(), pos.toLong(), this); // this is connector class
        super.initTesseract();
    }

    @Override
    public void onRemove() {
        if (isServerSide()) Tesseract.GT_ENERGY.remove(getWorld(), pos.toLong());
        super.onRemove();
    }

    @Override
    public void registerNode(BlockPos pos, Direction side, boolean remove) {
        if (!remove) {
            EnergyTileWrapper.wrap(this, getWorld(), pos, side, () -> world.getTileEntity(pos));
        } else {
           Tesseract.GT_ENERGY.remove(getWorld(), pos.toLong());
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
            return SIDE_CAPS[side.getIndex()].cast();
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
    public boolean connects(Direction direction) {
        return canConnect(direction.getIndex());
    }
}
