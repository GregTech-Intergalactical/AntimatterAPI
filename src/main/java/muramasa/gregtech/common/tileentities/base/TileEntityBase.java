package muramasa.gregtech.common.tileentities.base;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class TileEntityBase extends TileEntity {

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(final NetworkManager net, final SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
        markForNBTSync();
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return (oldState.getBlock() != newState.getBlock());
    }

    public boolean isClientSide() {
        return world.isRemote;
    }

    public boolean isServerSide() {
        return !world.isRemote;
    }

    public IBlockState getState() {
        return world.getBlockState(pos);
    }

    public void setState(IBlockState state) {
        world.setBlockState(pos, state);
    }

    public void markForNBTSync() { //Syncs NBT between Client & Server
        world.notifyBlockUpdate(pos, getState(), getState(), 3);
    }

    public void markForRenderUpdate() { //Triggers the BakedModel to refresh
        markForNBTSync();
        world.markBlockRangeForRenderUpdate(pos, pos);
    }
}
