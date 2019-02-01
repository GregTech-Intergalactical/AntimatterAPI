package muramasa.itech.api.capability;

import muramasa.itech.api.capability.impl.MachineStackHandler;
import muramasa.itech.common.tileentities.base.multi.TileEntityMultiMachine;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public interface IComponent {

    String getId();

    TileEntity getTile();

    ArrayList<BlockPos> getLinkedControllers();

    MachineStackHandler getStackHandler();

    void linkController(TileEntityMultiMachine tile);

    void unlinkController(TileEntityMultiMachine tile);
}
