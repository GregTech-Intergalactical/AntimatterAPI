package muramasa.antimatter.capability;

import muramasa.antimatter.capability.machine.MachineEnergyHandler;
import muramasa.antimatter.capability.machine.MachineFluidHandler;
import muramasa.antimatter.capability.machine.MachineItemHandler;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import tesseract.api.TesseractCaps;
import tesseract.api.heat.IHeatHandler;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IComponentHandler {

    String getId();

    BlockEntity getTile();

    LazyOptional<MachineItemHandler<?>> getItemHandler();

    LazyOptional<MachineFluidHandler<?>> getFluidHandler();

    LazyOptional<MachineEnergyHandler<?>> getEnergyHandler();

    default LazyOptional<IHeatHandler> getHeatHandler() {
        return getTile().getCapability(TesseractCaps.getHEAT_CAPABILITY());
    }

    void onStructureFormed(TileEntityMultiMachine<?> tile);

    void onStructureInvalidated(TileEntityMultiMachine<?> tile);

    boolean hasLinkedController();

    Collection<TileEntityMultiMachine<?>> getControllers();
}
