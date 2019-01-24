package muramasa.itech.api.capability.implementations;

import muramasa.itech.api.capability.IComponent;
import muramasa.itech.common.tileentities.base.TileEntityBase;
import muramasa.itech.common.tileentities.base.multi.TileEntityMultiMachine;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class ComponentHandler implements IComponent {

    private String componentId = "null";
    private TileEntityBase componentTile;
    private ArrayList<BlockPos> controllers = new ArrayList<>();

    public ComponentHandler(String componentId, TileEntityBase componentTile) {
        this.componentId = componentId;
        this.componentTile = componentTile;
    }

    public void notifyOfRemoval() {
        if (controllers.size() > 0) {
            for (int i = 0; i < controllers.size(); i++) {
                TileEntity controller = componentTile.getWorld().getTileEntity(controllers.get(i));
                if (controller != null && controller instanceof TileEntityMultiMachine) {
                    ((TileEntityMultiMachine) controller).onComponentRemoved();
                }
            }
        }
    }

    public ArrayList<BlockPos> getLinkedControllers() {
        return controllers;
    }

    public String getComponentId() {
        return componentId;
    }

    public TileEntityBase getTile() {
        return componentTile;
    }

    @Override
    public String getId() {
        return componentId;
    }

    @Override
    public BlockPos getPos() {
        return componentTile.getPos();
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
