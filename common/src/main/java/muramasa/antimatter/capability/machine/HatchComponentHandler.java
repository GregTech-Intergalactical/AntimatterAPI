package muramasa.antimatter.capability.machine;

import muramasa.antimatter.capability.ComponentHandler;
import muramasa.antimatter.tile.multi.TileEntityHatch;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;
import org.jetbrains.annotations.NotNull;

public class HatchComponentHandler<T extends TileEntityHatch<T>> extends ComponentHandler<T> {

    public HatchComponentHandler(T componentTile) {
        super(componentTile.getMachineType().getId(), componentTile.hatchMachine.getIdForHandlers(), componentTile);
    }

    @Override
    public void onStructureFormed(@NotNull TileEntityMultiMachine<?> controllerTile) {
        super.onStructureFormed(controllerTile);
    }

    @Override
    public void onStructureInvalidated(@NotNull TileEntityMultiMachine<?> controllerTile) {
        super.onStructureInvalidated(controllerTile);
    }

}
