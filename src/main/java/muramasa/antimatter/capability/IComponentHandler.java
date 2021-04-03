package muramasa.antimatter.capability;

import mcp.MethodsReturnNonnullByDefault;
import muramasa.antimatter.capability.machine.MachineEnergyHandler;
import muramasa.antimatter.capability.machine.MachineFluidHandler;
import muramasa.antimatter.capability.machine.MachineItemHandler;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;
import muramasa.antimatter.util.LazyHolder;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IComponentHandler {

    String getId();

    TileEntity getTile();

    LazyOptional<MachineItemHandler<?>> getItemHandler();

    LazyOptional<MachineFluidHandler<?>> getFluidHandler();

    LazyOptional<MachineEnergyHandler<?>> getEnergyHandler();

    void onStructureFormed(TileEntityMultiMachine tile);

    void onStructureInvalidated(TileEntityMultiMachine tile);

    boolean hasLinkedController();

    Optional<TileEntityMultiMachine> getFirstController();
}
