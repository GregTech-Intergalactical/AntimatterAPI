package muramasa.itech.common.tileentities.multi;

import muramasa.itech.api.capability.IComponent;
import muramasa.itech.common.tileentities.TileEntityBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class TileEntityComponent extends TileEntityBase implements IComponent {

    private ArrayList<BlockPos> controllers = new ArrayList<>();

    public void notifyOfRemoval() {
        if (controllers.size() > 0) {
            for (int i = 0; i < controllers.size(); i++) {
                TileEntity tile = world.getTileEntity(controllers.get(i));
                if (tile != null && tile instanceof TileEntityMultiMachine) {
                    ((TileEntityMultiMachine) tile).onComponentRemoved();
                }
            }
        }
    }

    public ArrayList<BlockPos> getLinkedControllers() {
        return controllers;
    }

    @Override
    public String getId() {
        return "";
    }

    @Override
    public void linkController(TileEntityMultiMachine tile) {
        controllers.add(tile.getPos());
    }

    @Override
    public void unlinkController(TileEntityMultiMachine tile) {
        controllers.remove(tile.getPos());
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        return super.writeToNBT(compound);
    }
}
