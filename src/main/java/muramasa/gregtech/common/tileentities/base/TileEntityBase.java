package muramasa.gregtech.common.tileentities.base;

import muramasa.gregtech.api.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

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

    /** Syncs NBT between Client & Server **/
    public void markForNBTSync() {
        Utils.markTileForNBTSync(this);
    }

    /** Sends block update to clients **/
    public void markForRenderUpdate() {
        Utils.markTileForRenderUpdate(this);
    }

    public List<String> getInfo() {
        ArrayList<String> info = new ArrayList<>();
        info.add("");
        info.add(TextFormatting.AQUA + "[GregTech Debug Info]");
        return info;
    }
}
