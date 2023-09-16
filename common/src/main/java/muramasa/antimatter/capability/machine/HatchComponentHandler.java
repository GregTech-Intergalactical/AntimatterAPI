package muramasa.antimatter.capability.machine;

import muramasa.antimatter.blockentity.multi.BlockEntityHatch;
import muramasa.antimatter.blockentity.multi.BlockEntityMultiMachine;
import muramasa.antimatter.capability.ComponentHandler;
import org.jetbrains.annotations.NotNull;

public class HatchComponentHandler<T extends BlockEntityHatch<T>> extends ComponentHandler<T> {

    public HatchComponentHandler(T componentTile) {
        super(componentTile.getMachineType().getId(), componentTile.hatchMachine.getIdForHandlers(), componentTile);
    }

    @Override
    public void onStructureFormed(@NotNull BlockEntityMultiMachine<?> controllerTile) {
        super.onStructureFormed(controllerTile);
    }

    @Override
    public void onStructureInvalidated(@NotNull BlockEntityMultiMachine<?> controllerTile) {
        super.onStructureInvalidated(controllerTile);
    }

}
