package muramasa.antimatter.tile.multi;

import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.IComponentHandler;
import muramasa.antimatter.capability.machine.ControllerComponentHandler;
import muramasa.antimatter.capability.machine.MachineRecipeHandler;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.structure.IComponent;
import muramasa.antimatter.structure.Structure;
import muramasa.antimatter.structure.StructureCache;
import muramasa.antimatter.structure.StructureResult;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

/** Allows a MultiMachine to handle GUI recipes, instead of using Hatches **/
public class TileEntityBasicMultiMachine extends TileEntityMachine implements IComponent {

    protected StructureResult result = null;

    /** To ensure proper load from disk, do not check if INVALID_STRUCTURE is loaded from disk. **/
    protected boolean shouldCheckFirstTick = true;

    protected final LazyOptional<ControllerComponentHandler> componentHandler = LazyOptional.of(() -> new ControllerComponentHandler(this));

    public TileEntityBasicMultiMachine(Machine<?> type) {
        super(type);
    }

    @Override
    public void onRemove() {
        super.onRemove();
        invalidateStructure();
    }

    /**
     * How many multiblocks you can share components with.
     * @return how many.
     */
    public int maxShares() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void onFirstTick() {
        //if INVALID_STRUCTURE was stored to disk don't bother rechecking on first tick.
        //This is not only behavioural but if INVALID_STRUCTURE are checked then maxShares
        //might misbehave.
        if (!isStructureValid() && shouldCheckFirstTick) {
            checkStructure();
        }
        super.onFirstTick();
    }

    public boolean checkStructure() {
        Structure structure = getMachineType().getStructure(getMachineTier());
        if (structure == null) return false;
        StructureResult result = structure.evaluate(this);
        if (result.evaluate()) {
            if (StructureCache.add(world, pos, result.positions, maxShares())) {
                this.result = result;
                if (isServerSide()) {
                    if (onStructureFormed()) {
                        afterStructureFormed();
                        setMachineState(MachineState.IDLE);
                        Antimatter.LOGGER.info("[Structure Debug] Valid Structure");
                        if (hadFirstTick()) this.recipeHandler.ifPresent(t -> {
                            if (t.hasRecipe())
                                setMachineState(MachineState.NO_POWER);
                            else {
                                t.checkRecipe();
                            }
                        });
                        sidedSync(true);
                        return true;
                    }
                } else {
                    this.result.components.forEach((k, v) -> v.forEach(c -> {
                        Utils.markTileForRenderUpdate(c.getTile());
                    }));
                    sidedSync(true);
                    return true;
                }
            }
        } else {
            invalidateStructure();
        }
        return false;
    }

    @Override
    public boolean setFacing(Direction side) {
        boolean ok = super.setFacing(side);
        if (ok) {
            checkStructure();
        }
        return ok;
    }

    public void invalidateStructure() {
        if (removed) return;
        if (result == null) return;
        StructureCache.remove(this.getWorld(), getPos());
        if (isServerSide()) {
            onStructureInvalidated();
            result = null;
            resetMachine();
            //Hard mode, remove recipe progress.
            if (AntimatterConfig.COMMON_CONFIG.INPUT_RESET_MULTIBLOCK.get()) {
                recipeHandler.ifPresent(MachineRecipeHandler::resetRecipe);
            }
        } else {
            this.result.components.forEach((k, v) -> v.forEach(c -> {
                Utils.markTileForRenderUpdate(c.getTile());
            }));
            result = null;
        }
    }

    @Override
    public void onServerUpdate() {
        super.onServerUpdate();
        if (result == null && world != null && world.getGameTime() % 200 == 0) {
            //Uncomment to periodically check structure.
            // checkStructure();
        }
    }

    /** Returns a list of Components **/
    public List<IComponentHandler> getComponents(IAntimatterObject object) {
        return getComponents(object.getId());
    }

    public List<IComponentHandler> getComponents(String id) {
        if (result != null) {
            List<IComponentHandler> list = result.components.get(id);
            return list != null ? list : Collections.emptyList();
        }
        return Collections.emptyList();
    }

    public List<BlockState> getStates(String id) {
        if (result != null) {
            List<BlockState> list = result.states.get(id);
            return list != null ? list : Collections.emptyList();
        }
        return Collections.emptyList();
    }

    public boolean isStructureValid() {
        return StructureCache.has(world, pos);
    }

    /** Events **/
    public boolean onStructureFormed() {
        return true;
    }

    public void afterStructureFormed(){
        //NOOP
    }

    public void onStructureInvalidated() {
        //NOOP
    }

    @Override
    public MachineState getDefaultMachineState() {
        //Has to be nullchecked because it can be called in a constructor.
        if (result == null) return MachineState.INVALID_STRUCTURE;
        return MachineState.IDLE;
    }

    @Override
    public LazyOptional<ControllerComponentHandler> getComponentHandler() {
        return componentHandler;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        if (cap == AntimatterCaps.COMPONENT_HANDLER_CAPABILITY && componentHandler.isPresent()) {
            return componentHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void read(BlockState state, CompoundNBT tag) {
        super.read(state, tag);
        if (getMachineState() == MachineState.INVALID_STRUCTURE) {
            shouldCheckFirstTick = false;
        }
    }
}
