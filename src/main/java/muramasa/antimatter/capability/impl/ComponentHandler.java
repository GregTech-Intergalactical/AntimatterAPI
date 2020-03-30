package muramasa.antimatter.capability.impl;

import muramasa.antimatter.capability.IComponentHandler;
import muramasa.antimatter.structure.StructureCache;
import muramasa.antimatter.tile.TileEntityBase;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public class ComponentHandler implements IComponentHandler {

    protected String componentId;
    protected TileEntityBase componentTile;

    public ComponentHandler(String componentId, TileEntityBase componentTile) {
        this.componentId = componentId;
        this.componentTile = componentTile;
    }

    @Override
    public String getId() {
        return componentId;
    }

    @Override
    public TileEntityBase getTile() {
        return componentTile;
    }

    @Override
    public Optional<MachineItemHandler> getItemHandler() {
        return componentTile instanceof TileEntityMachine ? ((TileEntityMachine) componentTile).itemHandler : Optional.empty();
    }

    @Override
    public Optional<MachineFluidHandler> getFluidHandler() {
        return componentTile instanceof TileEntityMachine ? ((TileEntityMachine) componentTile).fluidHandler : Optional.empty();
    }

    @Override
    public Optional<MachineEnergyHandler> getEnergyHandler() {
        return componentTile instanceof TileEntityMachine ? ((TileEntityMachine) componentTile).energyHandler : Optional.empty();
    }

    @Override
    public void onStructureFormed(TileEntityMultiMachine controllerTile) {

    }

    @Override
    public void onStructureInvalidated(TileEntityMultiMachine controllerTile) {

    }

    @Override
    public boolean hasLinkedController() {
        return StructureCache.has(getTile().getWorld(), getTile().getPos());
    }

    @Override
    public Optional<TileEntityMultiMachine> getFirstController() {
//        int size = controllers.size();
//        TileEntity tile;
//        for (int i = 0; i < size; i++) {
//            tile = Utils.getTile(componentTile.getWorld(), controllers.get(i));
//            if (tile instanceof TileEntityMultiMachine) return (TileEntityMultiMachine) tile;
//        }
//        return null;
        //TODO support multiple controllers
        BlockPos controllerPos = StructureCache.get(getTile().getWorld(), getTile().getPos());
        if (controllerPos != null) {
            TileEntity tile = Utils.getTile(getTile().getWorld(), getTile().getPos());
            if (tile instanceof TileEntityMultiMachine) return Optional.of((TileEntityMultiMachine) tile);
        }
        return Optional.empty();
    }
}
