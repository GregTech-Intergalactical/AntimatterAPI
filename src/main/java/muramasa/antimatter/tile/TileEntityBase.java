package muramasa.antimatter.tile;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.util.Utils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class TileEntityBase extends TileEntity {

    public TileEntityBase(TileEntityType<?> type) {
        super(type);
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

    public RegistryKey<World> getDimension() {
        return world.getDimensionKey();
    }

    //TODO pass constant StringBuilder
    public List<String> getInfo() {
        List<String> info = new ObjectArrayList<>();
        info.add("Tile: " + getClass().getName());
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

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tag = super.getUpdateTag();
        this.write(tag);
        return tag;
    }
}
