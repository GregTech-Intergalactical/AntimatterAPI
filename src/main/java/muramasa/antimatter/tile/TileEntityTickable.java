package muramasa.antimatter.tile;

import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;

public class TileEntityTickable<T extends TileEntityTickable<T>> extends TileEntityBase<T> implements ITickableTileEntity {

    private boolean hadFirstTick;

    public TileEntityTickable(TileEntityType<?> type) {
        super(type);
    }

    @Override
    public void tick() {
        level.getProfiler().push("AntimatterTileTick");
        if (!hadFirstTick) {
            onFirstTick();
            hadFirstTick = true;
        } else if (isServerSide()) {
            onServerUpdate();
        } else {
            onClientUpdate();
        }
        level.getProfiler().pop();
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
