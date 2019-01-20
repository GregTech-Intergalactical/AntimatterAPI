package muramasa.itech.common.tileentities;

import net.minecraft.util.ITickable;

public class TileEntityTickable extends TileEntityBase implements ITickable {

    private boolean hadFirstTick;

    @Override
    public void update() {
        if (!hadFirstTick) {
            onFirstTick();
            hadFirstTick = true;
        }
    }

    public void onFirstTick() {
        //NOOP
    }

    public long getElapsedTicks() {
        return world.getTotalWorldTime();
    }
}
