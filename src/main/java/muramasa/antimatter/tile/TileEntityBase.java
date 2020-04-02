package muramasa.antimatter.tile;

import muramasa.antimatter.util.Utils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import java.util.ArrayList;
import java.util.List;

public abstract class TileEntityBase extends TileEntity {

    public TileEntityBase(TileEntityType<?> type) {
        super(type);
    }

    @Override //TODO needed in onLoad?
    public void onLoad() {
        if (isServerSide()) initCaps();
    }

    public void initCaps() {
        //NOOP
    }

    public boolean isClientSide() {
        return world.isRemote;
    }

    public boolean isServerSide() {
        return !world.isRemote;
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
