package muramasa.gregtech.api.capability;

import muramasa.gregtech.api.capability.impl.MachineItemHandler;
import muramasa.gregtech.common.tileentities.base.multi.TileEntityMultiMachine;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public interface IComponentHandler {

    String getId();

    TileEntity getTile();

    List<BlockPos> getLinkedControllers();

    MachineItemHandler getItemHandler();

    void linkController(TileEntityMultiMachine tile);

    void unlinkController(TileEntityMultiMachine tile);

    boolean hasLinkedController();

    TileEntityMultiMachine getFirstController();

    void onComponentRemoved();
}
