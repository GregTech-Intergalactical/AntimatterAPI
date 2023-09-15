package muramasa.antimatter.capability;

import muramasa.antimatter.capability.machine.MachineEnergyHandler;
import muramasa.antimatter.capability.machine.MachineFluidHandler;
import muramasa.antimatter.capability.machine.MachineItemHandler;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.block.entity.BlockEntity;
import tesseract.TesseractCapUtils;
import tesseract.api.heat.IHeatHandler;

import java.util.Collection;
import java.util.Optional;

//@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IComponentHandler {

    String getId();

    BlockEntity getTile();

    String getIdForHandlers();

    Optional<MachineItemHandler<?>> getItemHandler();

    Optional<MachineFluidHandler<?>> getFluidHandler();

    Optional<MachineEnergyHandler<?>> getEnergyHandler();

    default Optional<IHeatHandler> getHeatHandler() {
        return TesseractCapUtils.getHeatHandler(getTile(), null);
    }

    void onStructureFormed(TileEntityMultiMachine<?> tile);

    void onStructureInvalidated(TileEntityMultiMachine<?> tile);

    boolean hasLinkedController();

    Collection<TileEntityMultiMachine<?>> getControllers();
}
