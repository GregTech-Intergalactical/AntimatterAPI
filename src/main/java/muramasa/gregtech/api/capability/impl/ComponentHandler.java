package muramasa.gregtech.api.capability.impl;

import muramasa.gregtech.api.capability.IComponent;
import muramasa.gregtech.common.tileentities.base.TileEntityBase;
import muramasa.gregtech.common.tileentities.base.multi.TileEntityMultiMachine;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.ArrayList;

public class ComponentHandler implements IComponent {

    //TODO special component handlers for different componentTile types

    private String componentId = "null";
    private TileEntityBase componentTile;
    private ArrayList<BlockPos> controllers = new ArrayList<>();

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
    public MachineStackHandler getStackHandler() {
        return (MachineStackHandler) componentTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
    }

    @Override
    public void linkController(TileEntityMultiMachine controllerTile) {
        controllers.add(controllerTile.getPos());
    }

    @Override
    public void unlinkController(TileEntityMultiMachine controllerTile) {
        controllers.remove(controllerTile.getPos());
    }
}
