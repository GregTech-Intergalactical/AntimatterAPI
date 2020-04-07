package muramasa.antimatter.tile;

import muramasa.antimatter.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import java.util.ArrayList;
import java.util.List;

public abstract class TileEntityBase extends TileEntity {

    public TileEntityBase() {
        super(null);
    }

    public TileEntityBase(TileEntityType<?> type) {
        super(type);
    }

    public boolean isClientSide() {
        return world.isRemote;
    }

    public boolean isServerSide() {
        return !world.isRemote;
    }

    public BlockState getState() {
        return world.getBlockState(pos);
    }

    public void setState(BlockState state) {
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

    //TODO pass constant StringBuilder
    public List<String> getInfo() {
        return new ArrayList<>();
    }
}
