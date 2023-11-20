package muramasa.antimatter.blockentity.multi;

import com.google.common.collect.Lists;
import com.gtnewhorizon.structurelib.StructureLibAPI;
import com.gtnewhorizon.structurelib.alignment.IAlignment;
import com.gtnewhorizon.structurelib.alignment.IAlignmentLimits;
import com.gtnewhorizon.structurelib.alignment.enumerable.ExtendedFacing;
import com.gtnewhorizon.structurelib.alignment.enumerable.Flip;
import com.gtnewhorizon.structurelib.alignment.enumerable.Rotation;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.capability.IComponentHandler;
import muramasa.antimatter.client.scene.TrackedDummyWorld;
import muramasa.antimatter.machine.BlockMultiMachine;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.machine.event.MachineEvent;
import muramasa.antimatter.machine.types.BasicMultiMachine;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.structure.Structure;
import muramasa.antimatter.structure.StructureCache;
import muramasa.antimatter.structure.StructureHandle;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.blockentity.BlockEntityMachine;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Allows a MultiMachine to handle GUI recipes, instead of using Hatches
 **/
public class BlockEntityBasicMultiMachine<T extends BlockEntityBasicMultiMachine<T>> extends BlockEntityMachine<T>
        implements IAlignment {

    private final Set<StructureHandle<?>> allHandlers = new ObjectOpenHashSet<>();
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
    protected int checkingStructure = 0;
    /**
     * Used whenever a machine might be rotated and is checking structure, since the
     * facing is changed before checkStructure()
     * is called and it needs to properly offset in recipeStop
     */
    public BlockState oldState;
    private Direction facingOverride;


    public Object2ObjectMap<String, List<IComponentHandler>> components = new Object2ObjectOpenHashMap<>();

    public BlockEntityBasicMultiMachine(Machine<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        extendedFacing = ExtendedFacing.of(getFacing(state), Rotation.NORMAL, Flip.NONE);
    }

    @Override
    public void onRemove() {
        super.onRemove();
        // Remove handlers from the structure cache.
        allHandlers.forEach(StructureHandle::deregister);
        invalidateStructure();
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
        allHandlers.forEach(StructureHandle::register);
        // if INVALID_STRUCTURE was stored to disk don't bother rechecking on first
        // tick.
        // This is not only behavioural but if INVALID_STRUCTURE are checked then
        // maxShares
        // might misbehave.
        if (!validStructure && shouldCheckFirstTick) {
            checkStructure();
        }
        super.onFirstTick();
    }

    @Override
    public InteractionResult onInteractBoth(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, @Nullable AntimatterToolType type) {
        if (!validStructure && checkingStructure == 0){
            if (checkStructure()){
                return InteractionResult.SUCCESS;
            }
        }
        return super.onInteractBoth(state, world, pos, player, hand, hit, type);
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
        structure.getMinMaxMap().forEach((s, p) -> {
            int min = p.left();
            int max = p.right();
            int size = 0;
            if (components.containsKey(s)){
                size = components.get(s).size();
            }
            if (size < min || size > max){
                fail[0] = true;
            }
        });
        if (fail[0]) validStructure = false;
        if (validStructure){
            LongList positions = LongList.of(structurePositions.keySet().toLongArray());
            if (level instanceof TrackedDummyWorld) {
                StructureCache.add(level, worldPosition, positions);
                StructureCache.validate(level, worldPosition, positions, maxShares());
                checkingStructure--;
                return true;
            } else if (onStructureFormed() && StructureCache.validate(this.getLevel(), this.getBlockPos(), positions, maxShares())){
                if (isServerSide()){
                    afterStructureFormed();
                    if (machineState != MachineState.ACTIVE && machineState != MachineState.DISABLED) {
                        setMachineState(MachineState.IDLE);
                    }
                    this.recipeHandler.ifPresent(
                            t -> t.onMultiBlockStateChange(true, AntimatterConfig.INPUT_RESET_MULTIBLOCK.get()));
                } else {
                    this.components.forEach((k, v) -> v.forEach(c -> {
                        Utils.markTileForRenderUpdate(c.getTile());
                    }));
                }
                sidedSync(true);
                StructureCache.add(level, getBlockPos(), positions);
            } else {
                validStructure = false;
                structurePositions.forEach((l, e) -> {
                    BlockPos pos = BlockPos.of(l);
                    e.onStructureFail((T)this, this.getLevel(), pos.getX(), pos.getY(), pos.getZ());
                });
            }

        }
        if (!validStructure && !oldPositions.isEmpty()){
            oldPositions.forEach(p ->{
                p.right().onStructureFail((T) this, this.getLevel(), p.left().getX(), p.left().getY(), p.left().getZ());
            });
        }
        checkingStructure--;
        return validStructure;
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        super.serverTick(level, pos, state);
        if (level.getGameTime() % 100 == 0 && !validStructure && checkingStructure == 0 && !AntimatterPlatformUtils.isProduction()){
            //checkStructure();
        }
    }

    public boolean allowsFakeTiles(){
        return false;
    }

    @Override
    public void onBlockUpdate(BlockPos pos) {
        super.onBlockUpdate(pos);
        if (checkingStructure > 0)
            return;
        if (validStructure) {
            long longPos = pos.asLong();
            if (structurePositions.containsKey(longPos) && !structurePositions.get(longPos).check((T) this, this.getLevel(), pos.getX(), pos.getY(), pos.getZ())) {
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
        //result.updateState(this, result); //TODO changing state element
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
        if (!getFacing(old).equals(getFacing(newState))) {
            if (checkingStructure > 0) return;
            invalidateStructure();
            oldState = old;
            this.facingOverride = Utils.dirFromState(oldState);
            checkStructure();
            oldState = null;
            facingOverride = null;
        }
    }

    @Override
    public void onMachineStop() {
        super.onMachineStop();

    }

    @Override
    public void onMachineEvent(IMachineEvent event, Object... data) {
        super.onMachineEvent(event, data);
        if (event == MachineEvent.FLUIDS_OUTPUTTED || event == MachineEvent.ITEMS_OUTPUTTED) {
            components.values().forEach(l -> l.forEach(i -> {
                if (i.getTile() instanceof BlockEntityHatch<?> hatch) {
                    hatch.onMachineEvent(event, data);
                }
            }));
        }
    }

    protected void invalidateStructure() {
        if (this.getLevel() instanceof TrackedDummyWorld)
            return;
        if (!validStructure) {
            return;
        }
        if (isServerSide() && getMachineState() != getDefaultMachineState()) {
            resetMachine();
        }
        checkingStructure++;
        structurePositions.forEach((l,e) ->{
            BlockPos pos = BlockPos.of(l);
            e.onStructureFail((T) this, this.getLevel(), pos.getX(), pos.getY(), pos.getZ());
        });
        structurePositions.clear();
        validStructure = false;
        if (isServerSide()) {
            onStructureInvalidated();
            recipeHandler.ifPresent(
                    t -> t.onMultiBlockStateChange(false, AntimatterConfig.INPUT_RESET_MULTIBLOCK.get()));
            components.clear();
        } else {
            this.components.forEach((k, v) -> v.forEach(c -> {
                Utils.markTileForRenderUpdate(c.getTile());
            }));
            components.clear();
        }
        StructureCache.remove(level, worldPosition);
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

    public List<IComponentHandler> getComponentsByHandlerId(String id) {
        List<IComponentHandler> list = new ArrayList<>();
        components.forEach((s, l) -> l.forEach(c -> {
            if (c.getIdForHandlers().equals(id)) list.add(c);
        }));
        return list;
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

    public Texture getTextureForHatches(Direction dir, BlockPos hatchPos){
        Texture[] tex = this.getMachineType().getBaseTexture(this.getMachineTier(), this.getMachineState().getTextureState());
        if (tex.length == 1) return tex[0];
        return tex[dir.get3DDataValue()];
    }

    public ITextureProvider getHatchBlock(BlockPos pos){
        if (this.getMachineType() instanceof BasicMultiMachine<?> multiMachine && multiMachine.getTextureBlock() != null){
            return multiMachine.getTextureBlock().apply(tier);
        }
        return () -> this.getMachineType().getBaseTexture(this.getMachineTier(), this.getMachineState().getTextureState());
    }

    @Override
    public MachineState getDefaultMachineState() {
        // Has to be nullchecked because it can be called in a constructor.
        if (!validStructure)
            return MachineState.INVALID_STRUCTURE;
        return MachineState.IDLE;
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
        this.extendedFacing = ExtendedFacing.of(extendedFacing.getDirection(), Rotation.byIndex(tag.getByte("rotation")), Flip.byIndex(tag.getByte("flip")));
    }

    public void addStructureHandle(StructureHandle<?> handle) {
        this.allHandlers.add(handle);
    }
}
