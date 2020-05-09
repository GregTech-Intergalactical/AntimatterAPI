package muramasa.antimatter.tile;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.util.Utils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import java.util.List;

public abstract class TileEntityBase extends TileEntity {

    public TileEntityBase(TileEntityType<?> type) {
        super(type);
    }

    @Override
    public void remove() {
        super.remove();
        onRemove();
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

    public int getDimention() {
        return world.getDimension().getType().getId();
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
        List<String> info = new ObjectArrayList<>();
        info.add("Tile: " + getClass().getName());
        return info;
    }
}
