package muramasa.gtu.api.capability.impl;

import muramasa.gtu.api.capability.IComponentHandler;
import muramasa.gtu.api.structure.StructureCache;
import muramasa.gtu.api.tileentities.TileEntityBase;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.api.tileentities.multi.TileEntityMultiMachine;
import muramasa.gtu.api.util.Utils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

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

    @Nullable
    @Override
    public MachineItemHandler getItemHandler() {
        return componentTile instanceof TileEntityMachine ? ((TileEntityMachine) componentTile).getItemHandler() : null;
    }

    @Nullable
    @Override
    public MachineFluidHandler getFluidHandler() {
        return componentTile instanceof TileEntityMachine ? ((TileEntityMachine) componentTile).getFluidHandler() : null;
    }

    @Nullable
    @Override
    public MachineEnergyHandler getEnergyHandler() {
        return componentTile instanceof TileEntityMachine ? ((TileEntityMachine) componentTile).getEnergyHandler() : null;
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

    @Nullable
    @Override
    public TileEntityMultiMachine getFirstController() {
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
            if (tile instanceof TileEntityMultiMachine) return (TileEntityMultiMachine) tile;
        }
        return null;
    }
}
