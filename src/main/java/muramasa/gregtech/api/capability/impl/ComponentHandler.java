package muramasa.gregtech.api.capability.impl;

import muramasa.gregtech.api.capability.IComponentHandler;
import muramasa.gregtech.api.util.Utils;
import muramasa.gregtech.common.tileentities.base.TileEntityBase;
import muramasa.gregtech.common.tileentities.base.TileEntityMachine;
import muramasa.gregtech.common.tileentities.base.multi.TileEntityMultiMachine;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class ComponentHandler implements IComponentHandler {

    protected String componentId;
    protected TileEntityBase componentTile;
    protected List<BlockPos> controllers = new ArrayList<>();

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
    public List<BlockPos> getLinkedControllers() {
        return controllers;
    }

    @Override
    public MachineItemHandler getItemHandler() {
        if (componentTile instanceof TileEntityMachine) {
            return ((TileEntityMachine) componentTile).getItemHandler();
        }
        return null;
    }

    @Override
    public void linkController(TileEntityMultiMachine controllerTile) {
        controllers.add(controllerTile.getPos());
    }

    @Override
    public void unlinkController(TileEntityMultiMachine controllerTile) {
        controllers.remove(controllerTile.getPos());
    }

    @Override
    public boolean hasLinkedController() {
        return controllers.size() > 0;
    }

    @Override
    public TileEntityMultiMachine getFirstController() {
        int size = controllers.size();
        TileEntity tile;
        for (int i = 0; i < size; i++) {
            tile = Utils.getTile(componentTile.getWorld(), controllers.get(i));
            if (tile instanceof TileEntityMultiMachine) return (TileEntityMultiMachine) tile;
        }
        return null;
    }

    @Override
    public void onComponentRemoved() {
        if (controllers.size() > 0) {
            int size = controllers.size();
            for (int i = 0; i < size; i++) {
                TileEntity tile = Utils.getTile(componentTile.getWorld(), controllers.get(i));
                if (tile instanceof TileEntityMultiMachine) {
                    ((TileEntityMultiMachine) tile).onComponentRemoved();
                }
            }
        }
    }
}
