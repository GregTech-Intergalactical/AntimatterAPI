package muramasa.gregtech.api.capability.impl;

import muramasa.gregtech.api.capability.IComponent;
import muramasa.gregtech.api.util.Utils;
import muramasa.gregtech.common.tileentities.base.TileEntityBase;
import muramasa.gregtech.common.tileentities.base.TileEntityMachine;
import muramasa.gregtech.common.tileentities.base.multi.TileEntityMultiMachine;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class ComponentHandler implements IComponent {

    protected String componentId;
    protected TileEntityBase componentTile;
    protected ArrayList<BlockPos> controllers = new ArrayList<>();

    public ComponentHandler(String componentId, TileEntityBase componentTile) {
        this.componentId = componentId;
        this.componentTile = componentTile;
    }

    public void notifyOfRemoval() {
        //TODO
        if (controllers.size() > 0) {
            for (int i = 0; i < controllers.size(); i++) {
                TileEntity controller = componentTile.getWorld().getTileEntity(controllers.get(i));
                if (controller != null && controller instanceof TileEntityMultiMachine) {
                    ((TileEntityMultiMachine) controller).onComponentRemoved();
                }
            }
        }
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
    public ArrayList<BlockPos> getLinkedControllers() {
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
}
