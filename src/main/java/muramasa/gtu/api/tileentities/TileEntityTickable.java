package muramasa.gtu.api.tileentities;

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
