package muramasa.antimatter.tile;

import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;

public class TileEntityTickable extends TileEntityBase implements ITickableTileEntity {

    private boolean hadFirstTick;

    public TileEntityTickable() {
        super(null);
    }

    public TileEntityTickable(TileEntityType type) {
        super(type);
    }

    @Override
    public void tick() {
        if (!hadFirstTick) {
            onFirstTick();
            hadFirstTick = true;
        } else if (isClientSide()) {
            onClientUpdate();
        } else if (isServerSide()) {
            onServerUpdate();
        }
        requestModelDataUpdate();
    }

    public boolean hadFirstTick() {
        return hadFirstTick;
    }

    public void onFirstTick() {
        //NOOP
    }

    public void onClientUpdate() {
        //NOOP
    }

    public void onServerUpdate() {
        //NOOP
    }
}
