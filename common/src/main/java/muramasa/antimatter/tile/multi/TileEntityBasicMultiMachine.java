package muramasa.antimatter.tile.multi;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.capability.IComponentHandler;
import muramasa.antimatter.capability.machine.ControllerComponentHandler;
import muramasa.antimatter.client.scene.TrackedDummyWorld;
import muramasa.antimatter.cover.CoverDynamo;
import muramasa.antimatter.cover.CoverEnergy;
import muramasa.antimatter.cover.CoverInput;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.machine.BlockMultiMachine;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.structure.IComponent;
import muramasa.antimatter.structure.Structure;
import muramasa.antimatter.structure.StructureCache;
import muramasa.antimatter.structure.StructureHandle;
import muramasa.antimatter.structure.StructureResult;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import tesseract.api.gt.IEnergyHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Allows a MultiMachine to handle GUI recipes, instead of using Hatches
 **/
public class TileEntityBasicMultiMachine<T extends TileEntityBasicMultiMachine<T>> extends TileEntityMachine<T>
        implements IComponent {

    private final Set<StructureHandle<?>> allHandlers = new ObjectOpenHashSet<>();
    protected StructureResult result = null;

    /**
     * To ensure proper load from disk, do not check if INVALID_STRUCTURE is loaded
     * from disk.
     **/
    protected boolean shouldCheckFirstTick = true;
    // Number of calls into checkStructure, invalidateStructure. if > 0 ignore
    // callbacks from structurecache.
    private int checkingStructure = 0;
    /**
     * Used whenever a machine might be rotated and is checking structure, since the
     * facing is changed before checkStructure()
     * is called and it needs to properly offset in recipeStop
     */
    public BlockState oldState;
    private Direction facingOverride;

    public final LazyOptional<ControllerComponentHandler> componentHandler = LazyOptional
            .of(() -> new ControllerComponentHandler(this));

    public TileEntityBasicMultiMachine(Machine<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void onRemove() {
        super.onRemove();
        // Remove handlers from the structure cache.
        allHandlers.forEach(StructureHandle::deregister);
        //invalidateStructure();
        StructureCache.remove(level, worldPosition);
    }

    @Nullable
    public StructureResult getResult() {
        return result;
    }

    /**
     * How many multiblocks you can share components with.
     *
     * @return how many.
     */
    public int maxShares() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void onFirstTick() {
        // Register handlers to the structure cache.
        allHandlers.forEach(StructureHandle::register);
        // if INVALID_STRUCTURE was stored to disk don't bother rechecking on first
        // tick.
        // This is not only behavioural but if INVALID_STRUCTURE are checked then
        // maxShares
        // might misbehave.
        Structure s = getMachineType().getStructure(getMachineTier());
        if (s == null) {
            super.onFirstTick();
            return;
        }
        if (!isStructureValid() && shouldCheckFirstTick) {
            checkStructure();
        }
        super.onFirstTick();
    }

    @Override
    public Direction getFacing() {
        return facingOverride != null ? facingOverride : super.getFacing();
    }

    public boolean checkStructure() {
        Structure structure = getMachineType().getStructure(getMachineTier());
        if (structure == null)
            return false;
        checkingStructure++;
        StructureResult result = structure.evaluate(this);
        if (result.evaluate()) {
            if (level instanceof TrackedDummyWorld) {
                this.result = result;
                StructureCache.add(level, worldPosition, getMachineType().getStructure(getMachineTier()).allPositions(this));
                StructureCache.validate(level, worldPosition, result.positions, maxShares());
                return true;
            } else if (StructureCache.validate(level, worldPosition, result.positions, maxShares())) {
                this.result = result;
                result.build(this, result);
                if (isServerSide()) {
                    if (onStructureFormed()) {
                        afterStructureFormed();
                        if (machineState != MachineState.ACTIVE && machineState != MachineState.DISABLED) {
                            setMachineState(MachineState.IDLE);
                        }
                        // Antimatter.LOGGER.info("[Structure Debug] Valid Structure");
                        this.recipeHandler.ifPresent(
                                t -> t.onMultiBlockStateChange(true, AntimatterConfig.COMMON_CONFIG.INPUT_RESET_MULTIBLOCK.get()));
                        sidedSync(true);
                        checkingStructure--;
                        return true;
                    } else {
                        invalidateStructure();
                        checkingStructure--;
                        return false;
                    }
                } else if (onStructureFormed()) {
                    this.result.components.forEach((k, v) -> v.forEach(c -> {
                        Utils.markTileForRenderUpdate(c.getTile());
                    }));
                    sidedSync(true);
                    checkingStructure--;
                    return true;
                } else {
                    invalidateStructure();
                    checkingStructure--;
                    return false;
                }
            }
        } else {
            // Antimatter.LOGGER.info("[Structure Debug] Error " + result.getError());
        }
        // if we reached here something went wrong.
        invalidateStructure();
        checkingStructure--;
        return false;
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        super.serverTick(level, pos, state);
        if (result != null)
            result.tick(this);
    }

    public <T> LazyOptional<T> getCapabilityFromFake(Class<T> cap, BlockPos pos, Direction side,
                                                     ICover coverPresent) {
        if (!allowsFakeTiles()) return LazyOptional.empty();
        if (cap == IItemHandler.class && itemHandler.isPresent() && (coverPresent instanceof CoverInput))
            return itemHandler.side(side).cast();
        else if (cap == IFluidHandler.class && fluidHandler.isPresent() && (coverPresent instanceof CoverInput))
            return fluidHandler.side(side).cast();
        else if (cap == IEnergyHandler.class && energyHandler.isPresent()
                && (coverPresent instanceof CoverDynamo || coverPresent instanceof CoverEnergy))
            return energyHandler.side(side).cast();
        return LazyOptional.empty();
    }

    protected boolean allowsFakeTiles(){
        return false;
    }

    @Override
    public void onBlockUpdate(BlockPos pos) {
        if (checkingStructure > 0)
            return;
        if (result != null) {
            if (!getMachineType().getStructure(getMachineTier()).evaluatePosition(result, this, pos)) {
                invalidateStructure();
            }
        } else {
            checkStructure();
        }
    }

    @Override
    public void setMachineState(MachineState newState) {
        if (this.remove)
            return;
        super.setMachineState(newState);
        if (result != null)
            result.updateState(this, result);
    }

    @Override
    protected void setMachineStateBlockState(MachineState newState){
        BlockState state = getBlockState();
        if (newState == MachineState.ACTIVE || newState == MachineState.IDLE || newState == MachineState.INVALID_STRUCTURE){
            state = state.setValue(BlockMultiMachine.MACHINE_STATE, newState);
            getLevel().setBlockAndUpdate(getBlockPos(), state);
        }
    }

    @Override
    public void setBlockState(BlockState p_155251_) {
        BlockState old = this.getBlockState();
        super.setBlockState(p_155251_);
        BlockState newState = this.getBlockState();
        if (!old.equals(newState)) {

            if (result != null) {
                StructureCache.invalidate(this.getLevel(), getBlockPos(), result.positions);
            }
            StructureCache.remove(level, getBlockPos());
            oldState = old;
            this.facingOverride = Utils.dirFromState(oldState);
            StructureCache.add(level, getBlockPos(), this.getMachineType().getStructure(getMachineTier()).allPositions(this));
            checkStructure();
            oldState = null;
            facingOverride = null;
        }
    }

    @Override
    public void onMachineStop() {
        super.onMachineStop();

    }

    protected void invalidateStructure() {
        if (this.getLevel() instanceof TrackedDummyWorld)
            return;
        if (result == null) {
            if (isServerSide() && getMachineState() != getDefaultMachineState()) {
                resetMachine();
            }
            return;
        }
        checkingStructure++;
        StructureCache.invalidate(this.getLevel(), getBlockPos(), result.positions);
        if (isServerSide()) {
            onStructureInvalidated();
            result.remove(this, result);
            result = null;
            recipeHandler.ifPresent(
                    t -> t.onMultiBlockStateChange(false, AntimatterConfig.COMMON_CONFIG.INPUT_RESET_MULTIBLOCK.get()));
            // Hard mode, remove recipe progress.
        } else {
            this.result.components.forEach((k, v) -> v.forEach(c -> {
                Utils.markTileForRenderUpdate(c.getTile());
            }));
            result = null;
        }
        checkingStructure--;
    }

    /**
     * Returns a list of Components
     **/
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
        return StructureCache.has(level, worldPosition);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        Structure struc = getMachineType().getStructure(getMachineTier());
        if (struc != null) {
            StructureCache.add(level, worldPosition, struc.allPositions(this));
        }
    }

    /**
     * Events
     **/
    public boolean onStructureFormed() {
        return true;
    }

    public void afterStructureFormed() {
        // NOOP
    }

    public void onStructureInvalidated() {
        // NOOP
    }

    @Override
    public MachineState getDefaultMachineState() {
        // Has to be nullchecked because it can be called in a constructor.
        if (result == null)
            return MachineState.INVALID_STRUCTURE;
        return MachineState.IDLE;
    }

    @Override
    public LazyOptional<ControllerComponentHandler> getComponentHandler() {
        return componentHandler;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (getMachineState() == MachineState.INVALID_STRUCTURE) {
            shouldCheckFirstTick = false;
        }
    }

    public void addStructureHandle(StructureHandle<?> handle) {
        this.allHandlers.add(handle);
    }
}
