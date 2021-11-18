package muramasa.antimatter.tile;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.capability.Dispatch;
import muramasa.antimatter.util.Utils;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.Nullable;
import java.util.List;

public abstract class TileEntityBase<T extends TileEntityBase<T>> extends TileEntity {

    protected final Dispatch dispatch;

    public TileEntityBase(TileEntityType<?> type) {
        super(type);
        dispatch = new Dispatch();
    }

    @Override
    public void setRemoved() {
        onRemove();
        super.setRemoved();
    }

    public void onRemove() {
        //NOOP
    }

    public boolean isClientSide() {
        return level.isClientSide;
    }

    public boolean isServerSide() {
        return !level.isClientSide;
    }


    //TODO pass constant StringBuilder
    public List<String> getInfo() {
        List<String> info = new ObjectArrayList<>();
        info.add("Tile: " + getClass().getSimpleName());
        return info;
    }

    public void sidedSync(boolean renderUpdate) {
        if (this.getLevel() == null) return;
        if (!this.getLevel().isClientSide) {
            this.setChanged();
            Utils.markTileForNBTSync(this);
        } else if (renderUpdate) {
            Utils.markTileForRenderUpdate(this);
        }
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.worldPosition, 3, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        super.onDataPacket(net, pkt);
        handleUpdateTag(getBlockState(), pkt.getTag());
        sidedSync(true);
    }

    //TODO: implications of this.
    /*@Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tag = super.getUpdateTag();
        this.write(tag);
        return tag;
    }*/
}
