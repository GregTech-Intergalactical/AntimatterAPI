package muramasa.antimatter.capability;

import mcp.MethodsReturnNonnullByDefault;
import muramasa.antimatter.capability.machine.MachineCapabilityHolder;
import muramasa.antimatter.capability.machine.MachineEnergyHandler;
import muramasa.antimatter.capability.machine.MachineFluidHandler;
import muramasa.antimatter.capability.machine.MachineItemHandler;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IComponentHandler {

    String getId();

    TileEntity getTile();

    MachineCapabilityHolder<MachineItemHandler<?>> getItemHandler();

    MachineCapabilityHolder<MachineFluidHandler<?>> getFluidHandler();

    MachineCapabilityHolder<MachineEnergyHandler<?>> getEnergyHandler();

    void onStructureFormed(TileEntityMultiMachine tile);

    void onStructureInvalidated(TileEntityMultiMachine tile);

    boolean hasLinkedController();

    Optional<TileEntityMultiMachine> getFirstController();
}
