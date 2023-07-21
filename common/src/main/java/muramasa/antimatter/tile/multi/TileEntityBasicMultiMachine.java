package muramasa.antimatter.tile.multi;

import com.google.common.collect.Lists;
import com.gtnewhorizon.structurelib.StructureLibAPI;
import com.gtnewhorizon.structurelib.alignment.IAlignment;
import com.gtnewhorizon.structurelib.alignment.IAlignmentLimits;
import com.gtnewhorizon.structurelib.alignment.IAlignmentProvider;
import com.gtnewhorizon.structurelib.alignment.enumerable.ExtendedFacing;
import com.gtnewhorizon.structurelib.alignment.enumerable.Flip;
import com.gtnewhorizon.structurelib.alignment.enumerable.Rotation;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.capability.IComponentHandler;
import muramasa.antimatter.capability.machine.ControllerComponentHandler;
import muramasa.antimatter.client.scene.TrackedDummyWorld;
import muramasa.antimatter.machine.BlockMultiMachine;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.structure.*;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Allows a MultiMachine to handle GUI recipes, instead of using Hatches
 **/
public class TileEntityBasicMultiMachine<T extends TileEntityBasicMultiMachine<T>> extends TileEntityMachine<T>
        implements IComponent, IAlignment {

    private final Set<StructureHandle<?>> allHandlers = new ObjectOpenHashSet<>();
    protected StructureResult result = null;
    protected boolean validStructure = false;

    public Long2ObjectOpenHashMap<IStructureElement<T>> structurePositions = new Long2ObjectOpenHashMap<>();

    private ExtendedFacing extendedFacing;
    private IAlignmentLimits limits = getInitialAlignmentLimits();
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

    public final Optional<ControllerComponentHandler> componentHandler = Optional
            .of(new ControllerComponentHandler(this));


    public Object2ObjectMap<String, List<IComponentHandler>> components = new Object2ObjectOpenHashMap<>();

    public TileEntityBasicMultiMachine(Machine<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        extendedFacing = ExtendedFacing.of(getFacing(), Rotation.NORMAL, Flip.NONE);
    }

    @Override
    public void onRemove() {
        super.onRemove();
        // Remove handlers from the structure cache.
        //allHandlers.forEach(StructureHandle::deregister);
        invalidateStructure();
        //StructureCache.remove(level, worldPosition);
    }

    @Override
    public IAlignmentLimits getAlignmentLimits() {
        return limits;
    }

    @Override
    public ExtendedFacing getExtendedFacing() {
        return extendedFacing;
    }

    @Override
    public void setExtendedFacing(ExtendedFacing extendedFacing) {
        if (this.extendedFacing != extendedFacing && extendedFacing.getDirection() == this.getFacing()){
            this.extendedFacing = extendedFacing;
            invalidateCaps();
            if (isServerSide()) {
                StructureLibAPI.sendAlignment(
                        this,
                        getBlockPos(), 1.0, (ServerLevel) level);
            }
        }

    }

    @Override
    public boolean setFacing(Direction side) {
        boolean facingSet = super.setFacing(side);
        if (facingSet){
            extendedFacing = ExtendedFacing.of(side, extendedFacing.getRotation(), extendedFacing.getFlip());
            if (isServerSide()) {
                StructureLibAPI.sendAlignment(
                        this,
                        getBlockPos(), 1.0, (ServerLevel) level);
            }
        }
        return facingSet;
    }

    @Nullable
    public StructureResult getResult() {
        return result;
    }

    protected IAlignmentLimits getInitialAlignmentLimits() {
        return (d, r, f) -> !f.isVerticallyFliped();
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
        if (isClientSide()){
            StructureLibAPI.queryAlignment(this);
        }
        // Register handlers to the structure cache.
        //allHandlers.forEach(StructureHandle::register);
        // if INVALID_STRUCTURE was stored to disk don't bother rechecking on first
        // tick.
        // This is not only behavioural but if INVALID_STRUCTURE are checked then
        // maxShares
        // might misbehave.
        Structure<?> s = getMachineType().getStructure(getMachineTier());
        if (s == null) {
            super.onFirstTick();
            return;
        }
        if (!validStructure && shouldCheckFirstTick) {
            checkStructure();
        }
        super.onFirstTick();
    }

    @Override
    public Direction getFacing() {
        return facingOverride != null ? facingOverride : super.getFacing();
    }

    public boolean checkStructure() {
        Structure<T> structure = getMachineType().getStructure(getMachineTier());
        if (structure == null)
            return false;
        checkingStructure++;
        List<Pair<BlockPos, IStructureElement<T>>> oldPositions = structurePositions.long2ObjectEntrySet().stream().map(e -> Pair.of(BlockPos.of(e.getLongKey()), e.getValue())).toList();
        structurePositions.clear();
        components.clear();
        boolean oldValidStructure = validStructure;
        validStructure = structure.check((T)this);
        boolean[] fail = new boolean[1];
        fail[0] = false;
        components.forEach((s, l) -> {
            if (!structure.getMinMaxMap().containsKey(s)){
                fail[0] = true;
                return;
            }
            int min = structure.getMinMaxMap().get(s).left();
            int max = structure.getMinMaxMap().get(s).right();
            if (l.size() < min || l.size() > max){
                fail[0] = true;
            }
        });
        if (fail[0]) validStructure = false;
        if (validStructure){
            if (machineState != MachineState.ACTIVE && machineState != MachineState.DISABLED) {
                setMachineState(MachineState.IDLE);
            }
            onStructureFormed();
        }
        if (!validStructure && !oldPositions.isEmpty()){
            oldPositions.forEach(p ->{
                p.right().onStructureFail((T) this, this.getLevel(), p.left().getX(), p.left().getY(), p.left().getZ());
            });
        }
        /*StructureResult result = structure.evaluate(this);
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
        invalidateStructure();*/
        checkingStructure--;
        return validStructure;
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        super.serverTick(level, pos, state);
        if (level.getGameTime() % 100 == 0 && !validStructure){
            checkStructure();
        }
        if (result != null)
            result.tick(this);
    }

    public boolean allowsFakeTiles(){
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
            invalidateStructure();
            //StructureCache.remove(level, getBlockPos());
            oldState = old;
            this.facingOverride = Utils.dirFromState(oldState);
            //StructureCache.add(level, getBlockPos(), this.getMachineType().getStructure(getMachineTier()).allPositions(this));
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
        if (!validStructure) return;
        checkingStructure++;
        structurePositions.forEach((l,e) ->{
            BlockPos pos = BlockPos.of(l);
            e.onStructureFail((T) this, this.getLevel(), pos.getX(), pos.getY(), pos.getZ());
        });
        structurePositions.clear();
        validStructure = false;
        if (isServerSide()) onStructureInvalidated();
        /*if (result == null) {
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
        }*/
        checkingStructure--;
    }

    /**
     * Returns a list of Components
     **/
    public List<IComponentHandler> getComponents(IAntimatterObject object) {
        return getComponents(object.getId());
    }

    public List<IComponentHandler> getComponents(String id) {
        List<IComponentHandler> list = components.get(id);
        return list != null ? list : Collections.emptyList();
    }

    public void addComponent(String elementId, IComponentHandler component) {
        List<IComponentHandler> existing = components.get(component.getId());
        if (existing == null) components.put(component.getId(), Lists.newArrayList(component));
        else existing.add(component);
        if (!elementId.isEmpty() && !elementId.equals(component.getId())) {
            existing = components.get(elementId);
            if (existing == null) components.put(elementId, Lists.newArrayList(component));
            else existing.add(component);
        }
    }

    public boolean isStructureValid() {
        return validStructure;
        //return StructureCache.has(level, worldPosition);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        Structure struc = getMachineType().getStructure(getMachineTier());
        if (struc != null) {
            //StructureCache.add(level, worldPosition, struc.allPositions(this));
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
        if (!validStructure)
            return MachineState.INVALID_STRUCTURE;
        return MachineState.IDLE;
    }

    @Override
    public Optional<ControllerComponentHandler> getComponentHandler() {
        return componentHandler;
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putByte("rotation", (byte) extendedFacing.getRotation().getIndex());
        tag.putByte("flip", (byte) extendedFacing.getFlip().getIndex());
    }


    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (getMachineState() == MachineState.INVALID_STRUCTURE) {
            shouldCheckFirstTick = false;
        }
        this.extendedFacing = ExtendedFacing.of(this.getFacing(), Rotation.byIndex(tag.getByte("rotation")), Flip.byIndex(tag.getByte("flip")));
    }

    public void addStructureHandle(StructureHandle<?> handle) {
        this.allHandlers.add(handle);
    }
}
