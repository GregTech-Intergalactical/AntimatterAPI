package muramasa.antimatter.capability;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.capability.machine.MachineEnergyHandler;
import muramasa.antimatter.capability.machine.MachineFluidHandler;
import muramasa.antimatter.capability.machine.MachineItemHandler;
import muramasa.antimatter.structure.StructureCache;
import muramasa.antimatter.tile.TileEntityBase;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public class ComponentHandler<T extends TileEntityBase<T>> implements IComponentHandler {

    protected String componentId;
    protected T componentTile;

    protected Set<TileEntityMultiMachine<?>> controllers = new ObjectOpenHashSet<>();

    public ComponentHandler(String componentId, T componentTile) {
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
    public T getTile() {
        return componentTile;
    }

    @Nonnull
    @Override
    public Optional<MachineItemHandler<?>> getItemHandler() {
        return componentTile instanceof TileEntityMachine ? Optional.of((((TileEntityMachine<?>) componentTile).itemHandler.get())) : Optional.empty();
    }

    @Nonnull
    @Override
    public Optional<MachineFluidHandler<?>> getFluidHandler() {
        return componentTile instanceof TileEntityMachine ? Optional.of((((TileEntityMachine<?>) componentTile).fluidHandler.get())) : Optional.empty();
    }

    @Nonnull
    @Override
    public Optional<MachineEnergyHandler<?>> getEnergyHandler() {
        return componentTile instanceof TileEntityMachine ? Optional.of((((TileEntityMachine<?>) componentTile).energyHandler.get())) : Optional.empty();
    }

    @Override
    public void onStructureFormed(@Nonnull TileEntityMultiMachine<?> controllerTile) {
        this.controllers.add(controllerTile);
    }

    @Override
    public void onStructureInvalidated(@Nonnull TileEntityMultiMachine<?> controllerTile) {
        this.controllers.remove(controllerTile);
    }

    @Override
    public boolean hasLinkedController() {
        return StructureCache.has(getTile().getLevel(), getTile().getBlockPos());
    }

    @Nonnull
    @Override
    public Collection<TileEntityMultiMachine<?>> getControllers() {
        return controllers;
    }
}
