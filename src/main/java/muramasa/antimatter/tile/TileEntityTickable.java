package muramasa.antimatter.tile;

import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;

public class TileEntityTickable extends TileEntityBase implements ITickableTileEntity {

    private byte state;

    public TileEntityTickable(TileEntityType<?> type) {
        super(type);
    }

    @Override
    public void tick() {
        switch (state) {
            case 0:
                onInit();
                state++;
                break;
            case 1:
                if (isServerSide()) {
                    onServerLoad();
                }
                else {
                    onClientLoad();
                }
                state++;
                break;
            default:
                if (isServerSide()) {
                    onServerUpdate();
                }
                else {
                    onClientUpdate();
                }
                break;
        }
        requestModelDataUpdate();
    }

    public boolean hadFirstTick() {
        return state != 0;
    }

    public void onInit() {
        //NOOP
    }

    public void onClientLoad() {
        //NOOP
    }

    public void onServerLoad() {
        //NOOP
    }

    public void onClientUpdate() {
        //NOOP
    }

    public void onServerUpdate() {
        //NOOP
    }
}
