package muramasa.antimatter.capability;

import muramasa.antimatter.capability.impl.MachineEnergyHandler;
import muramasa.antimatter.capability.impl.MachineFluidHandler;
import muramasa.antimatter.capability.impl.MachineItemHandler;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;
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
