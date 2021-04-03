package muramasa.antimatter.capability;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.capability.machine.MachineEnergyHandler;
import muramasa.antimatter.capability.machine.MachineFluidHandler;
import muramasa.antimatter.capability.machine.MachineItemHandler;
import muramasa.antimatter.structure.StructureCache;
import muramasa.antimatter.tile.TileEntityBase;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;
import muramasa.antimatter.util.LazyHolder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.Set;

public class ComponentHandler implements IComponentHandler {

    protected String componentId;
    protected TileEntityBase componentTile;

    protected Set<TileEntityMultiMachine> controllers = new ObjectOpenHashSet<>();

    public ComponentHandler(String componentId, TileEntityBase componentTile) {
        this.componentId = componentId;
        this.componentTile = componentTile;
    }

    @Nonnull
    @Override
    public String getId() {
        return componentId;
    }

    @Nonnull
    @Override
    public TileEntityBase getTile() {
        return componentTile;
    }

    @Nonnull
    @Override
    public LazyOptional<MachineItemHandler<?>> getItemHandler() {
        return componentTile instanceof TileEntityMachine ? ((TileEntityMachine) componentTile).itemHandler : LazyOptional.empty();
    }

    @Nonnull
    @Override
    public LazyOptional<MachineFluidHandler<?>> getFluidHandler() {
        return componentTile instanceof TileEntityMachine ? ((TileEntityMachine) componentTile).fluidHandler : LazyOptional.empty();
    }

    @Nonnull
    @Override
    public LazyOptional<MachineEnergyHandler<?>> getEnergyHandler() {
        return componentTile instanceof TileEntityMachine ? ((TileEntityMachine) componentTile).energyHandler : LazyOptional.empty();
    }

    @Override
    public void onStructureFormed(@Nonnull TileEntityMultiMachine controllerTile) {
        this.controllers.add(controllerTile);
    }

    @Override
    public void onStructureInvalidated(@Nonnull TileEntityMultiMachine controllerTile) {
        this.controllers.remove(controllerTile);
    }

    @Override
    public boolean hasLinkedController() {
        return StructureCache.has(getTile().getWorld(), getTile().getPos());
    }

    @Nonnull
    @Override
    public Optional<TileEntityMultiMachine> getFirstController() {
        if (this.controllers.size() != 0) return Optional.of(this.controllers.iterator().next());
        BlockPos controllerPos = StructureCache.get(getTile().getWorld(), getTile().getPos());
        if (controllerPos != null) {
            TileEntity tile = getTile().getWorld().getTileEntity(controllerPos);
            if (tile instanceof TileEntityMultiMachine) return Optional.of((TileEntityMultiMachine) tile);
        }
        return Optional.empty();
    }
}
