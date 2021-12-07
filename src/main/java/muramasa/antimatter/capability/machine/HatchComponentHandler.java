package muramasa.antimatter.capability.machine;

import muramasa.antimatter.capability.ComponentHandler;
import muramasa.antimatter.tile.multi.TileEntityHatch;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;
import muramasa.antimatter.util.Utils;

import javax.annotation.Nonnull;

public class HatchComponentHandler<T extends TileEntityHatch<T>> extends ComponentHandler<T> {

    public HatchComponentHandler(T componentTile) {
        super(componentTile.getMachineType().getId(), componentTile);
    }

    @Override
    public void onStructureFormed(@Nonnull TileEntityMultiMachine<?> controllerTile) {
        super.onStructureFormed(controllerTile);
    }

    @Override
    public void onStructureInvalidated(@Nonnull TileEntityMultiMachine<?> controllerTile) {
        super.onStructureInvalidated(controllerTile);
    }

}
