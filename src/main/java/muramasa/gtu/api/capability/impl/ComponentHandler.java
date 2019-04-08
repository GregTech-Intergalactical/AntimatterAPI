package muramasa.gtu.api.capability.impl;

import muramasa.gtu.api.capability.IComponentHandler;
import muramasa.gtu.api.util.Utils;
import muramasa.gtu.api.tileentities.TileEntityBase;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.api.tileentities.multi.TileEntityMultiMachine;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ComponentHandler implements IComponentHandler {

    protected String componentId;
    protected TileEntityBase componentTile;
    protected List<BlockPos> controllers;

    public ComponentHandler(String componentId, TileEntityBase componentTile) {
        this.componentId = componentId;
        this.componentTile = componentTile;
        this.controllers = new ArrayList<>();
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
    @Nullable
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
    @Nullable
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
