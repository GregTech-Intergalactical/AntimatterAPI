package muramasa.antimatter.tile.single;

import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.tile.TileEntityStorage;


public class TileEntityBatteryBuffer<T extends TileEntityBatteryBuffer<T>> extends TileEntityStorage<T> {

    public TileEntityBatteryBuffer(Machine<?> type) {
        super(type);
    }

    @Override
    public ICover[] getValidCovers() {
        return new ICover[0];
    }
}