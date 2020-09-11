package muramasa.antimatter.capability.machine;

import muramasa.antimatter.capability.CapabilityType;
import muramasa.antimatter.capability.ComponentHandler;
import muramasa.antimatter.capability.ICapabilityHandler;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;

public class HatchComponentHandler extends ComponentHandler implements ICapabilityHandler {

    public HatchComponentHandler(TileEntityMachine componentTile, CompoundNBT tag) {
        super(componentTile.getMachineType().getId(), componentTile);
    }

    @Override
    public void onStructureFormed(@Nonnull TileEntityMultiMachine controllerTile) {
        super.onStructureFormed(controllerTile);
        //((TileEntityHatch) getTile()).setTextureOverride((controllerTile.getMachineTypeId() * 1000) + controllerTile.getTierId());
        Utils.markTileForRenderUpdate(getTile());
    }

    @Override
    public void onStructureInvalidated(@Nonnull TileEntityMultiMachine controllerTile) {
        super.onStructureInvalidated(controllerTile);
        //((TileEntityHatch) getTile()).setTextureOverride(-1);
        Utils.markTileForRenderUpdate(getTile());
    }

    @Override
    public CapabilityType getCapabilityType() {
        return CapabilityType.COMPONENT;
    }
}
