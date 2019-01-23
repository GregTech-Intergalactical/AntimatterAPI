package muramasa.itech.common.tileentities.base;

import net.minecraft.util.ITickable;

public class TileEntityTickable extends TileEntityBase implements ITickable {

    private boolean hadFirstTick;

    @Override
    public void update() {
        if (!hadFirstTick) {
            onFirstTick();
            hadFirstTick = true;
        } else if (isClientSide()) {
            onClientUpdate();
        } else if (isServerSide()) {
            onServerUpdate();
        }
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

    public long getElapsedTicks() {
        return world.getTotalWorldTime();
    }
}
