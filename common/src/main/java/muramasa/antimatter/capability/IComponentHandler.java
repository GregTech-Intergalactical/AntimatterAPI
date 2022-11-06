package muramasa.antimatter.capability;

import muramasa.antimatter.capability.machine.MachineEnergyHandler;
import muramasa.antimatter.capability.machine.MachineFluidHandler;
import muramasa.antimatter.capability.machine.MachineItemHandler;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import tesseract.api.TesseractCaps;
import tesseract.api.heat.IHeatHandler;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IComponentHandler {

    String getId();

    BlockEntity getTile();

    Optional<MachineItemHandler<?>> getItemHandler();

    Optional<MachineFluidHandler<?>> getFluidHandler();

    Optional<MachineEnergyHandler<?>> getEnergyHandler();

    default Optional<IHeatHandler> getHeatHandler() {
        return getTile().getCapability(TesseractCaps.getHEAT_CAPABILITY()).resolve();
    }

    void onStructureFormed(TileEntityMultiMachine<?> tile);

    void onStructureInvalidated(TileEntityMultiMachine<?> tile);

    boolean hasLinkedController();

    Collection<TileEntityMultiMachine<?>> getControllers();
}
