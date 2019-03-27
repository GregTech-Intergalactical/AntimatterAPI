package muramasa.gregtech.api.tileentities;

import net.minecraft.util.ITickable;

public class TileEntityTickable extends TileEntityBase implements ITickable {

    protected boolean hadFirstTick;

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
}
