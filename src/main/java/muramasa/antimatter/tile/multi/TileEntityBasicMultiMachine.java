package muramasa.antimatter.tile.multi;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.IComponentHandler;
import muramasa.antimatter.capability.machine.ControllerComponentHandler;
import muramasa.antimatter.cover.CoverDynamo;
import muramasa.antimatter.cover.CoverEnergy;
import muramasa.antimatter.cover.CoverInput;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.structure.*;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import tesseract.api.capability.TesseractGTCapability;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static net.minecraftforge.fluids.capability.CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
import static net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;

/**
 * Allows a MultiMachine to handle GUI recipes, instead of using Hatches
 **/
public class TileEntityBasicMultiMachine<T extends TileEntityBasicMultiMachine<T>> extends TileEntityMachine<T> implements IComponent {

    private final Set<StructureHandle<?>> allHandlers = new ObjectOpenHashSet<>();
    protected StructureResult result = null;

    /**
     * To ensure proper load from disk, do not check if INVALID_STRUCTURE is loaded from disk.
     **/
    protected boolean shouldCheckFirstTick = true;
    //Number of calls into checkStructure, invalidateStructure. if > 0 ignore callbacks from structurecache.
    private int checkingStructure = 0;
    /**
     * Used whenever a machine might be rotated and is checking structure, since the facing is changed before checkStructure()
     * is called and it needs to properly offset in recipeStop
     */
    public BlockState oldState;
    private Direction facingOverride;

    protected final LazyOptional<ControllerComponentHandler> componentHandler = LazyOptional.of(() -> new ControllerComponentHandler(this));

    public TileEntityBasicMultiMachine(Machine<?> type) {
        super(type);
    }

    @Override
    public void onRemove() {
        super.onRemove();
        //Remove handlers from the structure cache.
        allHandlers.forEach(StructureHandle::deregister);
        invalidateStructure();
        StructureCache.remove(world, pos);
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
        //Register handlers to the structure cache.
        allHandlers.forEach(StructureHandle::register);
        //if INVALID_STRUCTURE was stored to disk don't bother rechecking on first tick.
        //This is not only behavioural but if INVALID_STRUCTURE are checked then maxShares
        //might misbehave.
        Structure s = getMachineType().getStructure(getMachineTier());
        if (s == null) {
            super.onFirstTick();
            return;
        }
        StructureCache.add(world, pos, getMachineType().getStructure(getMachineTier()).allPositions(this));
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
        if (structure == null) return false;
        checkingStructure++;
        StructureResult result = structure.evaluate(this);
        if (result.evaluate()) {
            if (StructureCache.validate(world, pos, result.positions, maxShares())) {
                this.result = result;
                if (isServerSide()) {
                    result.build(this, result);
                    if (onStructureFormed()) {
                        afterStructureFormed();
                        setMachineState(MachineState.IDLE);
                        //Antimatter.LOGGER.info("[Structure Debug] Valid Structure");
                        this.recipeHandler.ifPresent(t -> t.onMultiBlockStateChange(true, AntimatterConfig.COMMON_CONFIG.INPUT_RESET_MULTIBLOCK.get()));
                        sidedSync(true);
                        checkingStructure--;
                        return true;
                    }
                } else {
                    this.result.components.forEach((k, v) -> v.forEach(c -> {
                        Utils.markTileForRenderUpdate(c.getTile());
                    }));
                    sidedSync(true);
                    checkingStructure--;
                    return true;
                }
            }
        } else {
            //Antimatter.LOGGER.info("[Structure Debug] Error " + result.getError());
        }
        //if we reached here something went wrong.
        invalidateStructure();
        checkingStructure--;
        return false;
    }

    @Override
    public void onServerUpdate() {
        super.onServerUpdate();
        if (result != null) result.tick(this);
    }

    public <T> LazyOptional<T> getCapabilityFromFake(Capability<T> cap, BlockPos pos, Direction side, ICover coverPresent) {
        if (cap == ITEM_HANDLER_CAPABILITY && itemHandler.isPresent() && (coverPresent instanceof CoverInput))
            return itemHandler.side(side).cast();
        else if (cap == FLUID_HANDLER_CAPABILITY && fluidHandler.isPresent() && (coverPresent instanceof CoverInput))
            return fluidHandler.side(side).cast();
        else if (cap == TesseractGTCapability.ENERGY_HANDLER_CAPABILITY && energyHandler.isPresent() && (coverPresent instanceof CoverDynamo || coverPresent instanceof CoverEnergy))
            return energyHandler.side(side).cast();
        return LazyOptional.empty();
    }

    @Override
    public void onBlockUpdate(BlockPos pos) {
        if (checkingStructure > 0) return;
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
        super.setMachineState(newState);
        if (result != null)
            result.updateState(this, result);
    }

    @Override
    public void updateContainingBlockInfo() {
        BlockState old = this.getBlockState();
        super.updateContainingBlockInfo();
        BlockState newState = this.getBlockState();
        if (!old.equals(newState)) {
            oldState = old;
            checkStructure();
            oldState = null;
            facingOverride = null;
        }
    }

    @Override
    public void onMachineStop() {
        super.onMachineStop();
        if (oldState != null) {
            this.facingOverride = Utils.dirFromState(oldState);
        }
    }

    protected void invalidateStructure() {
        if (removed) return;
        if (result == null) {
            if (isServerSide() && getMachineState() != getDefaultMachineState()) {
                resetMachine();
            }
            return;
        }
        checkingStructure++;
        StructureCache.invalidate(this.getWorld(), getPos(), result.positions);
        if (isServerSide()) {
            onStructureInvalidated();
            result.remove(this, result);
            result = null;
            recipeHandler.ifPresent(t -> t.onMultiBlockStateChange(false, AntimatterConfig.COMMON_CONFIG.INPUT_RESET_MULTIBLOCK.get()));
            //Hard mode, remove recipe progress.
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
        return StructureCache.has(world, pos);
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    /**
     * Events
     **/
    public boolean onStructureFormed() {
        return true;
    }

    public void afterStructureFormed() {
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

    public void addStructureHandle(StructureHandle<?> handle) {
        this.allHandlers.add(handle);
    }
}
