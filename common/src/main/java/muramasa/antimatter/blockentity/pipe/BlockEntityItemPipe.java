package muramasa.antimatter.blockentity.pipe;

import com.mojang.blaze3d.vertex.PoseStack;
import muramasa.antimatter.blockentity.BlockEntityCache;
import muramasa.antimatter.blockentity.IPreTickTile;
import muramasa.antimatter.capability.Dispatch;
import muramasa.antimatter.capability.FluidHandler;
import muramasa.antimatter.capability.item.ROCombinedInvWrapper;
import muramasa.antimatter.capability.item.SidedCombinedInvWrapper;
import muramasa.antimatter.capability.item.TrackedItemHandler;
import muramasa.antimatter.capability.pipe.PipeItemHandler;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.gui.widget.InfoRenderWidget;
import muramasa.antimatter.integration.jeirei.renderer.IInfoRenderer;
import muramasa.antimatter.pipe.BlockItemPipe;
import muramasa.antimatter.pipe.TileTicker;
import muramasa.antimatter.pipe.types.ItemPipe;
import muramasa.antimatter.util.CodeUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import tesseract.TesseractCapUtils;
import tesseract.TesseractGraphWrappers;
import tesseract.api.capability.TesseractItemCapability;
import tesseract.api.item.ExtendedItemContainer;
import tesseract.api.item.IItemPipe;
import tesseract.graph.Connectivity;
import tesseract.util.Pos;

import java.util.*;

public class BlockEntityItemPipe<T extends ItemPipe<T>> extends BlockEntityPipe<T>
        implements IItemPipe, Dispatch.Sided<ExtendedItemContainer>, IPreTickTile {

    private int holder;
    private boolean restricted;
    private TrackedItemHandler<BlockEntityItemPipe<?>> inventory;

    public byte mLastReceivedFrom = 6, oLastReceivedFrom = 6, mRenderType = 0, mDisabledOutputs = 0, mDisabledInputs = 0;

    public BlockEntityItemPipe(T type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        inventory = new TrackedItemHandler<>(this, SlotType.STORAGE, type.getCapacity(getPipeSize()), true, true, (g, i) -> true){
            @Override
            public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                ItemStack superInsert = super.insertItem(slot, stack, simulate);
                if (!simulate && superInsert.getCount() < stack.getCount()){
                    addTicker();
                }
                return superInsert;
            }
        };
        pipeCapHolder.set(() -> this);
        this.holder = 0;
        if (state.getBlock() instanceof BlockItemPipe<?> itemPipe){
            restricted = itemPipe.isRestricted();
        }
    }

    @Override
    protected void register() {
        //TesseractGraphWrappers.ITEM.registerConnector(getLevel(), getBlockPos().asLong(), this, isConnector());
    }

    @Override
    protected boolean deregister() {
        return true;
        //return TesseractGraphWrappers.ITEM.remove(getLevel(), getBlockPos().asLong());
    }

    @Override
    public int getCapacity() {
        return getPipeType().getCapacity(getPipeSize());
    }

    @Override
    public int getStepsize() {
        return getPipeType().getStepsize(getPipeSize()) * (restricted ? 100 : 1);
    }

    @Override
    public boolean connects(Direction direction) {
        return canConnect(direction.get3DDataValue());
    }

    @Override
    public boolean validate(Direction dir) {
        if (!super.validate(dir))
            return false;
        BlockEntity tile = getCachedBlockEntity(dir);
        if (tile == null)
            return false;
        return TesseractCapUtils.getItemHandler(tile, dir.getOpposite()).isPresent();
    }

    @Override
    public Class<?> getCapClass() {
        return ExtendedItemContainer.class;
    }

    @Override
    public Optional<ExtendedItemContainer> forSide(Direction side) {
        return Optional.of(new PipeItemHandler(side, this, coverHandler.orElse(null), inventory));
    }

    @Override
    public Optional<? extends ExtendedItemContainer> forNullSide() {
        return Optional.of(new ROCombinedInvWrapper(inventory));
    }

    @Override
    public void onUnregisterPre() {

    }

    @Override
    public void onServerTickPre(Level level, BlockPos pos, boolean aFirst) {
        if (aFirst) {
            if (level.getGameTime() % 20 == 0) holder = 0;
        } else if (level.getGameTime() % 10 == 0) {
            if (oLastReceivedFrom == mLastReceivedFrom && mLastReceivedFrom < 6) {
                boolean tUpdate = false;
                ArrayList<BlockEntityItemPipe<?>> tPipeList = new ArrayList<>();
                for (boolean temp = true; temp && !inventory.isEmpty() && getHolder() < getCapacity();) {
                    temp = false;
                    tPipeList.clear();
                    Set<BlockEntityItemPipe<?>> sortedPipeList = CodeUtils.sortByValuesAcending(scanPipes(this, new HashMap<>(), 0, false, false)).keySet();
                    for (BlockEntityItemPipe<?> tTileEntity : sortedPipeList) {
                        if (temp) break;
                        tPipeList.add(tTileEntity);
                        while (!temp && !inventory.isEmpty() && tTileEntity.sendItemStack(this)) {
                            tUpdate = true;
                            for (BlockEntityItemPipe<?> tPipe : tPipeList) if (!tPipe.incrementTransferCounter(1)) temp = true;
                        }
                    }
                }
                if (tUpdate) {
                    /*BlockEntity tDelegator = getCachedBlockEntity(Direction.values()[mLastReceivedFrom]);
                    if (tDelegator instanceof BlockEntityItemPipe<?> itemPipe) {
                        itemPipe.adjacentInventoryUpdated(tDelegator.mSideOfTileEntity, this);
                    }*/
                }
            }

            if (inventory.isEmpty()) {
                mLastReceivedFrom = 6;
                TileTicker.addTickFunction(() -> {
                    TileTicker.SERVER_TICK_PRE.remove(this);
                    TileTicker.SERVER_TICK_PR2.remove(this);
                });
            }
            oLastReceivedFrom = mLastReceivedFrom;
        }
    }

    @Override
    public int getHolder() {
        return holder;
    }

    @Override
    public void setHolder(int holder) {
        this.holder = holder;        
    }

    public boolean insertItemStackIntoTileEntity(BlockEntityItemPipe<?> aSender, byte aSide) {
        if (aSide > 5) return false;
        Direction side = Direction.values()[aSide];
        if (!Connectivity.has(mDisabledOutputs, aSide) && canEmitItemsTo(side, aSender)) {
            BlockEntity tDelegator = getCachedBlockEntity(side);
            if (!(tDelegator instanceof BlockEntityPipe<?>)) {
                if (!(tDelegator instanceof HopperBlockEntity || tDelegator instanceof DispenserBlockEntity)) {
                    // special cases for the win...
                    ICover cover = coverHandler.map(c -> c.get(side)).orElse(ICover.empty);
                    if (!cover.isEmpty()){

                    }

                    /*CoverData tCovers = getCoverData();
                    if (tCovers != null && tCovers.mBehaviours[aSide] instanceof CoverFilterItem && tCovers.mNBTs[aSide] != null) {
                        ItemStack tStack = ST.load(tCovers.mNBTs[aSide], "gt.filter.item");
                        return ST.valid(tStack) && ST.move(new DelegatorTileEntity<>((TileEntity)aSender, SIDE_ANY), tDelegator, ST.hashset(tStack), F, F, tCovers.mVisuals[aSide] != 0, T, 64, 1, 64, 1) > 0;
                    }
                    // well normal case is this.
                    return ST.move(new DelegatorTileEntity<>((TileEntity)aSender, SIDE_ANY), tDelegator) > 0;*/
                }
            }
        }
        return false;
    }
    public boolean sendItemStack(BlockEntityItemPipe<?> aSender) {
        if (getHolder() < getCapacity()) {
            for (byte i = 0, j = (byte)level.random.nextInt(6); i < 6; i++) {
                if (insertItemStackIntoTileEntity(aSender, (byte)((i+j)%6))) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void addInventoryDrops(List<ItemStack> drops) {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) drops.add(stack);
        }
    }

    public boolean incrementTransferCounter(int amount){
        setHolder(getHolder() + amount);
        return getHolder() < getCapacity();
    }

    boolean canAcceptItemsFrom(Direction side, BlockEntityItemPipe<?> sender){
        return Connectivity.has(virtualConnection, side.get3DDataValue());
    }

    boolean canEmitItemsTo(Direction side, BlockEntityItemPipe<?> sender){
        return (sender != this || side.get3DDataValue() != mLastReceivedFrom) && Connectivity.has(virtualConnection, side.get3DDataValue());
    }

    private void addTicker(){
        if (!TileTicker.SERVER_TICK_PRE.contains(this)) {
            TileTicker.SERVER_TICK_PRE.add(this);
        }
        if (!TileTicker.SERVER_TICK_PR2.contains(this)) {
            TileTicker.SERVER_TICK_PR2.add(this);
        }
    }

    @Override
    public void onRemove() {
        TileTicker.SERVER_TICK_PR2.remove(this);
        TileTicker.SERVER_TICK_PRE.remove(this);
        super.onRemove();
    }

    /**
     * @return a List of connected Item Pipes
     */
    public static Map<BlockEntityItemPipe<?>, Long> scanPipes(BlockEntityItemPipe<?> aPipe, Map<BlockEntityItemPipe<?>, Long> aMap, long aStep, boolean aSuckItems, boolean aIgnoreCapacity) {
        aStep += aPipe.getStepsize();
        // TODO Make this iterative instead of recursive.
        if (aIgnoreCapacity || aPipe.getHolder() < aPipe.getCapacity()) if (aMap.get(aPipe) == null || aMap.get(aPipe) > aStep) {
            aMap.put(aPipe, aStep);
            for (Direction aSide : Direction.values()) {
                if (aSuckItems) {
                    if (aPipe.canAcceptItemsFrom(aSide, null)) {
                        BlockEntity tDelegator = aPipe.getCachedBlockEntity(aSide);
                        if (tDelegator instanceof BlockEntityItemPipe<?> pipe && pipe.connects(aSide.getOpposite()) && pipe.canEmitItemsTo(aSide.getOpposite(), null)) {
                            scanPipes(pipe, aMap, aStep, aSuckItems, aIgnoreCapacity);
                        }
                    }
                } else {
                    if (aPipe.canEmitItemsTo(aSide, null)) {
                        BlockEntity tDelegator = aPipe.getCachedBlockEntity(aSide);
                        if (tDelegator instanceof BlockEntityItemPipe<?> pipe) {
                            if (pipe.connects(aSide.getOpposite())) {
                                if (pipe.canAcceptItemsFrom(aSide.getOpposite(), null)) {
                                    scanPipes(aPipe, aMap, aStep, aSuckItems, aIgnoreCapacity);
                                }
                            }
                        }
                    }
                }
            }
        }
        return aMap;
    }
}
