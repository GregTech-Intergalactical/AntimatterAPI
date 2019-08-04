package muramasa.gtu.api.capability;

import muramasa.gtu.api.capability.impl.MachineEnergyHandler;
import muramasa.gtu.api.capability.impl.MachineFluidHandler;
import muramasa.gtu.api.capability.impl.MachineItemHandler;
import muramasa.gtu.api.tileentities.multi.TileEntityMultiMachine;
import net.minecraft.tileentity.TileEntity;

import java.util.Optional;

public interface IComponentHandler {

    String getId();

    TileEntity getTile();

    Optional<MachineItemHandler> getItemHandler();

    Optional<MachineFluidHandler> getFluidHandler();

    Optional<MachineEnergyHandler> getEnergyHandler();

    void onStructureFormed(TileEntityMultiMachine tile);

    void onStructureInvalidated(TileEntityMultiMachine tile);

    boolean hasLinkedController();

    Optional<TileEntityMultiMachine> getFirstController();
}
