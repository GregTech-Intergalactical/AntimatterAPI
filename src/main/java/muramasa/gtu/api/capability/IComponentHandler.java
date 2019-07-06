package muramasa.gtu.api.capability;

import muramasa.gtu.api.capability.impl.MachineEnergyHandler;
import muramasa.gtu.api.capability.impl.MachineFluidHandler;
import muramasa.gtu.api.capability.impl.MachineItemHandler;
import muramasa.gtu.api.tileentities.multi.TileEntityMultiMachine;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;

public interface IComponentHandler {

    String getId();

    TileEntity getTile();

    @Nullable
    MachineItemHandler getItemHandler();

    @Nullable
    MachineFluidHandler getFluidHandler();

    @Nullable
    MachineEnergyHandler getEnergyHandler();

    void onStructureFormed(TileEntityMultiMachine tile);

    void onStructureInvalidated(TileEntityMultiMachine tile);

    boolean hasLinkedController();

    @Nullable
    TileEntityMultiMachine getFirstController();
}
