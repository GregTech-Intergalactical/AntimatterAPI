package muramasa.antimatter.tile;

import muramasa.antimatter.capability.Dispatch;
import muramasa.antimatter.util.Utils;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import speiger.src.collections.objects.lists.ObjectArrayList;

import javax.annotation.Nullable;
import java.util.List;

public abstract class TileEntityBase<T extends TileEntityBase<T>> extends TileEntity {

    protected final Dispatch dispatch;

    public TileEntityBase(TileEntityType<?> type) {
        super(type);
        dispatch = new Dispatch();
    }

    @Override
    public void remove() {
        onRemove();
        super.remove();
    }

    public void onRemove() {
        //NOOP
    }

    public boolean isClientSide() {
        return world.isRemote;
    }

    public boolean isServerSide() {
        return !world.isRemote;
    }


    //TODO pass constant StringBuilder
    public List<String> getInfo() {
        List<String> info = new ObjectArrayList<>();
        info.add("Tile: " + getClass().getSimpleName());
        return info;
    }

    public void sidedSync(boolean renderUpdate) {
        if (this.getWorld() == null) return;
        if (!this.getWorld().isRemote) {
            this.markDirty();
            Utils.markTileForNBTSync(this);
        } else if(renderUpdate) {
            Utils.markTileForRenderUpdate(this);
        }
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 3, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        super.onDataPacket(net, pkt);
        handleUpdateTag(getBlockState(),pkt.getNbtCompound());
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
