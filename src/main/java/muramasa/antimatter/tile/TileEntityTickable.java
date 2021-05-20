package muramasa.antimatter.tile;

import muramasa.antimatter.capability.Dispatch;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

public class TileEntityTickable<T extends TileEntityTickable<T>> extends TileEntityBase<T> implements ITickableTileEntity {

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
