package muramasa.gtu.api.capability;

import muramasa.gtu.api.capability.impl.MachineEnergyHandler;
import muramasa.gtu.api.capability.impl.MachineFluidHandler;
import muramasa.gtu.api.capability.impl.MachineItemHandler;
import muramasa.gtu.api.tileentities.multi.TileEntityMultiMachine;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.List;

public interface IComponentHandler {

    String getId();

    TileEntity getTile();

    List<BlockPos> getLinkedControllers();

    @Nullable
    MachineItemHandler getItemHandler();

    @Nullable
    MachineFluidHandler getFluidHandler();

    @Nullable
    MachineEnergyHandler getEnergyHandler();

    void linkController(TileEntityMultiMachine tile);

    void unlinkController(TileEntityMultiMachine tile);

    boolean hasLinkedController();

    @Nullable
    TileEntityMultiMachine getFirstController();

    void onComponentRemoved();
}
