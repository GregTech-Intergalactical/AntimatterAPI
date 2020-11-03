package muramasa.antimatter.tile;

import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;

public class TileEntityTickable extends TileEntityBase implements ITickableTileEntity {

    private boolean hadFirstTick;

    public TileEntityTickable(TileEntityType<?> type) {
        super(type);
    }

    @Override
    public void tick() {
        if (!hadFirstTick) {
            onFirstTick();
            hadFirstTick = true;
        } else if (isServerSide()) {
            onServerUpdate();
        } else {
            onClientUpdate();
        }
        //requestModelDataUpdate();
    }

    public boolean hadFirstTick() {
        return hadFirstTick;
    }

    /**
     * Override this to do any initialization that requires the World and/or BlockState reference.
     */
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
