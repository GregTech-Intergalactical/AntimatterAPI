package muramasa.itech.api.capability.implementations;

import muramasa.itech.api.capability.IComponent;
import muramasa.itech.common.tileentities.multi.TileEntityMultiMachine;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class Component implements IComponent {

    private String id;
    private BlockPos pos;
    private ArrayList<BlockPos> controllers = new ArrayList<>();

    public Component(String id, BlockPos pos) {
        this.id = id;
        this.pos = pos;
    }

//    public void notifyOfRemoval() {
//        if (controllers.size() > 0) {
//            for (int i = 0; i < controllers.size(); i++) {
//                TileEntity tile = world.getTileEntity(controllers.get(i));
//                if (tile != null && tile instanceof TileEntityMultiMachine) {
//                    ((TileEntityMultiMachine) tile).onComponentRemoved();
//                }
//            }
//        }
//    }

    public ArrayList<BlockPos> getLinkedControllers() {
        return controllers;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public BlockPos getPos() {
        return pos;
    }

    @Override
    public void linkController(TileEntityMultiMachine tile) {
        controllers.add(tile.getPos());
    }

    @Override
    public void unlinkController(TileEntityMultiMachine tile) {
        controllers.remove(tile.getPos());
    }
}
