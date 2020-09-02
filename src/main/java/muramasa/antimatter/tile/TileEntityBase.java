package muramasa.antimatter.tile;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import java.util.List;

public abstract class TileEntityBase extends TileEntity {

    public TileEntityBase(TileEntityType<?> type) {
        super(type);
    }

    @Override
    public void remove() {
        if (isServerSide()) {
            onServerRemove();
        }
        else {
            onClientRemove();
        }
        super.remove();
    }

    public boolean isClientSide() {
        return world.isRemote;
    }

    public boolean isServerSide() {
        return !world.isRemote;
    }

    public int getDimension() {
        return world.getDimension().getType().getId();
    }

    //TODO pass constant StringBuilder
    public List<String> getInfo() {
        List<String> info = new ObjectArrayList<>();
        info.add("Tile: " + getClass().getName());
        return info;
    }

    public void onClientRemove() {
        //NOOP
    }

    public void onServerRemove() {
        //NOOP
    }
}
