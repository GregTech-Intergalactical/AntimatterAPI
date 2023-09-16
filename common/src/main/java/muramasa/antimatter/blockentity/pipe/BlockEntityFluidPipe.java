package muramasa.antimatter.blockentity.pipe;

import com.mojang.blaze3d.vertex.PoseStack;
import earth.terrarium.botarium.common.fluid.base.FluidContainer;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.base.PlatformFluidHandler;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.Dispatch;
import muramasa.antimatter.capability.FluidHandler;
import muramasa.antimatter.capability.fluid.FluidHandlerNullSideWrapper;
import muramasa.antimatter.capability.fluid.FluidTank;
import muramasa.antimatter.capability.fluid.PipeFluidHandlerSidedWrapper;
import muramasa.antimatter.capability.pipe.PipeFluidHandler;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.data.AntimatterTags;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.widget.InfoRenderWidget;
import muramasa.antimatter.integration.jeirei.renderer.IInfoRenderer;
import muramasa.antimatter.pipe.FluidPipeTicker;
import muramasa.antimatter.pipe.PipeSize;
import muramasa.antimatter.pipe.types.FluidPipe;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import tesseract.FluidPlatformUtils;
import tesseract.TesseractCapUtils;
import tesseract.TesseractGraphWrappers;
import tesseract.api.fluid.IFluidPipe;
import tesseract.api.fluid.PipeFluidHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BlockEntityFluidPipe<T extends FluidPipe<T>> extends BlockEntityPipe<T> implements IFluidPipe, Dispatch.Sided<FluidContainer>, IInfoRenderer<InfoRenderWidget.TesseractFluidWidget> {

    protected Optional<PipeFluidHandler> fluidHandler;
    public static byte[] SBIT = {1, 2, 4, 8, 16, 32};
    private PipeFluidHolder holder;
    byte[] lastSide;
    long transferredAmount = 0;
    long mTemperature = 293;

    public BlockEntityFluidPipe(T type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        int count = getPipeSize() == PipeSize.QUADRUPLE ? 4 : getPipeSize() == PipeSize.NONUPLE ? 9 : 1;
        fluidHandler = Optional.of(new PipeFluidHandler(this, type.getPressure(getPipeSize()) * 2, type.getPressure(getPipeSize()), count, 0));
        pipeCapHolder.set(() -> this);
        lastSide = new byte[count];
        for (int i = 0; i < count; i++){
            lastSide[i] = 0;
        }
    }

    @Override
    public void onLoad() {
        holder = new PipeFluidHolder(this);
        super.onLoad();
        if (even(this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ())) {
            FluidPipeTicker.SERVER_TICK_PRE.add(this);
        } else {
            FluidPipeTicker.SERVER_TICK_PR2.add(this);
        }
    }

    @Override
    public void onFirstTick() {
        super.onFirstTick();
    }

    @Override
    public void onBlockUpdate(BlockPos neighbour) {
        super.onBlockUpdate(neighbour);
    }

    @Override
    protected void register() {
    }

    @Override
    protected boolean deregister() {
        return true;
    }


    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(Ref.KEY_MACHINE_FLUIDS))
            fluidHandler.ifPresent(t -> t.deserialize(tag.getCompound(Ref.KEY_MACHINE_FLUIDS)));
        ListTag tags = tag.getList("lastSide", Tag.TAG_BYTE);
        for (int i = 0; i < tags.size(); i++){
            lastSide[i] = ((ByteTag)tags.get(i)).getAsByte();
        }
        mTemperature = tag.getLong("temperature");
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        fluidHandler.ifPresent(t -> tag.put(Ref.KEY_MACHINE_FLUIDS, t.serialize(new CompoundTag())));
        ListTag tags = new ListTag();
        for (int i = 0; i < lastSide.length; i++){
            tags.add(ByteTag.valueOf(lastSide[i]));
        }
        tag.put("lastSide", tags);
        tag.putLong("temperature", mTemperature);
    }

    @Override
    public void onRemove() {
        fluidHandler.ifPresent(FluidHandler::onRemove);
        FluidPipeTicker.SERVER_TICK_PR2.remove(this);
        FluidPipeTicker.SERVER_TICK_PRE.remove(this);
        super.onRemove();
    }

    @Override
    public void addWidgets(GuiInstance instance, IGuiElement parent) {
        super.addWidgets(instance, parent);
        instance.addWidget(InfoRenderWidget.TesseractFluidWidget.build().setPos(10, 10));
    }


    @Override
    public boolean isGasProof() {
        return getPipeType().isGasProof();
    }

    @Override
    public PipeFluidHolder getHolder() {
        return holder;
    }

    @Override
    public int getCapacity() {
        return 1;
    }

    @Override
    public long getPressure() {
        return getPipeType().getPressure(getPipeSize());
    }

    @Override
    public int getTemperature() {
        return getPipeType().getTemperature();
    }

    @SuppressWarnings("ConstantValue")
    public long getCurrentTemperature(){
        return fluidHandler.map(f -> {
            long currentTemp = -1;
            for (int i = 0; i < f.getSize(); i++){
                FluidHolder fluid = f.getFluidInTank(i);
                if (fluid.isEmpty()){
                    continue;
                }
                currentTemp = Math.max(FluidPlatformUtils.getFluidTemperature(fluid.getFluid()), currentTemp);
            }
            return currentTemp == -1 ? 293L : currentTemp;
        }).orElse(293L);
    }

    @Override
    public boolean connects(Direction direction) {
        return canConnect(direction.get3DDataValue());
    }

    @Override
    public boolean validate(Direction dir) {
        if (!super.validate(dir)) return false;
        return TesseractCapUtils.getFluidHandler(level, getBlockPos().relative(dir), dir.getOpposite()).isPresent();
    }

    public void setLastSide(Direction lastSide, int tank) {
        this.lastSide[tank] |= SBIT[lastSide.get3DDataValue()];
    }

    @Override
    protected void serverTick(Level level, BlockPos pos, BlockState state) {
        super.serverTick(level, pos, state);
        this.getHolder().tick(getLevel().getGameTime());
    }

    private boolean mHasToAddTimer = true;

    public void onUnregisterPre() {mHasToAddTimer = true;}

    public void onServerTickPre(Level level, BlockPos pos, boolean aFirst) {
        transferredAmount = 0;

        PlatformFluidHandler adjacentFluidHandlers[] = new PlatformFluidHandler[6];
        PipeFluidHandler pipeFluidHandler = fluidHandler.orElse(null);
        if (pipeFluidHandler == null) return;

        for (Direction tSide : Direction.values()) {
            if (connects(tSide)) {
                PlatformFluidHandler fluidHandler1 = TesseractCapUtils.getFluidHandler(level, pos.relative(tSide), tSide.getOpposite()).orElse(null);
                if (fluidHandler1 != null) {
                    adjacentFluidHandlers[tSide.get3DDataValue()] = fluidHandler1;
                }
            }
        }

        boolean tCheckTemperature = true;

        for (int i = 0; i < pipeFluidHandler.getInputTanks().getSize(); i++){
            FluidTank tTank = pipeFluidHandler.getInputTanks().getTank(i);
            FluidHolder tFluid = tTank.getStoredFluid();
            if (!tFluid.isEmpty()){
                mTemperature = (tCheckTemperature ? FluidPlatformUtils.getFluidTemperature(tFluid.getFluid()) : Math.max(mTemperature, FluidPlatformUtils.getFluidTemperature(tFluid.getFluid())));
                tCheckTemperature = false;


                if (!isGasProof() && FluidPlatformUtils.isFluidGaseous(tFluid.getFluid())) {
                    transferredAmount += tTank.extractFluid(tFluid.copyWithAmount(8 * TesseractGraphWrappers.dropletMultiplier), false).getFluidAmount();
                    level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0f, 1.0f);
                    /*try {
                        for (Entity tEntity : (List<Entity>)worldObj.getEntitiesWithinAABB(Entity.class, box(-2, -2, -2, +3, +3, +3))) {
                            UT.Entities.applyTemperatureDamage(tEntity, mTemperature, 2.0F, 10.0F);
                        }
                    } catch(Throwable e) {e.printStackTrace(ERR);}*/
                }

                if (!type.isAcidProof() && tFluid.getFluid().is(AntimatterTags.ACID)){
                    transferredAmount += tTank.extractFluid(tFluid.copyWithAmount(16 * TesseractGraphWrappers.dropletMultiplier), false).getFluidAmount();
                    level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0f, 1.0f);
                    if (level.random.nextInt(100) == 0){
                        tTank.clearContent();
                        level.setBlock(pos, Blocks.FIRE.defaultBlockState(), 3);
                        return;
                    }
                }
            }
            if (mTemperature > getTemperature()) {
                burn(level, pos.getX(), pos.getY(), pos.getZ());
                if (level.random.nextInt(100) == 0) {
                    tTank.clearContent();
                    level.setBlock(pos, Blocks.FIRE.defaultBlockState(), 3);
                    return;
                }
            }

            if (!tTank.getStoredFluid().isEmpty()) distribute(level, tTank, i, adjacentFluidHandlers);

            lastSide[i] = 0;
        }
    }

    @SuppressWarnings("rawtypes")
    public void distribute(Level level, FluidTank aTank, int i, PlatformFluidHandler[] fluidHandlers) {
        // Check if we are empty.
        if (aTank.isEmpty()) return;
        // Compile all possible Targets into one List.
        List<PlatformFluidHandler> tTanks = new ArrayList<>();
        List<PlatformFluidHandler> tPipes = new ArrayList<>();
        // Amount to check for Distribution
        long tAmount = aTank.getStoredFluid().getFluidAmount();
        // Count all Targets. Also includes THIS for even distribution, thats why it starts at 1.
        int tTargetCount = 1;
        // Put Targets into Lists.
        for (Direction tSide : Direction.values()) {

            // Don't you dare flow backwards!
            if ((lastSide[i] & SBIT[tSide.get3DDataValue()]) != 0) continue;
            // Are we even connected to this Side? (Only gets checked due to the Cover check being slightly expensive)
            if (!connects(tSide)) continue;
            // Covers let distribution happen, right?
            ICover cover = coverHandler.map(c -> c.get(tSide)).orElse(ICover.empty);
            if (!cover.isEmpty() && cover.blocksOutput(FluidContainer.class, tSide)) continue;
            // No Tank? Nothing to do then.
            if (fluidHandlers[tSide.get3DDataValue()] == null) continue;
            // Check if the Tank can be filled with this Fluid.
            long insert = fluidHandlers[tSide.get3DDataValue()].insertFluid(aTank.getStoredFluid().copyWithAmount(Integer.MAX_VALUE), true);
            if (insert > 0) {
                if (fluidHandlers[tSide.get3DDataValue()] instanceof PipeFluidHandlerSidedWrapper){
                    tPipes.add(level.random.nextInt(tTanks.size()+1), fluidHandlers[tSide.get3DDataValue()]);
                } else {
                    // Add to a random Position in the List.
                    tTanks.add(level.random.nextInt(tTanks.size()+1), fluidHandlers[tSide.get3DDataValue()]);
                }
                // One more Target.
                tTargetCount++;
                // Done everything.
                continue;
            }
        }
        // No Targets? Nothing to do then.
        if (tTargetCount <= 1) return;
        // Amount to distribute normally.
        tAmount = divup(tAmount, tTargetCount);
        // Distribute to Pipes first.
        for (PlatformFluidHandler tPipe : tPipes) transferredAmount += aTank.extractFluid(aTank.getStoredFluid().copyWithAmount(tPipe.insertFluid(aTank.getStoredFluid().copyWithAmount(tAmount), false)), false).getFluidAmount();
        // Check if we are empty.
        if (aTank.isEmpty()) return;
        // Distribute to Tanks afterwards.
        for (PlatformFluidHandler tTank : tTanks) transferredAmount += aTank.extractFluid(aTank.getStoredFluid().copyWithAmount(tTank.insertFluid(aTank.getStoredFluid().copyWithAmount(tAmount), false)), false).getFluidAmount();
        // Check if we are empty.
        if (aTank.isEmpty()) return;
        // No Targets? Nothing to do then.
        if (tPipes.isEmpty()) return;
        // And then if there still is pressure, distribute to Pipes again.
        tAmount = (aTank.getStoredFluid().getFluidAmount() - aTank.getCapacity()/2) / tPipes.size();
        if (tAmount > 0) for (PlatformFluidHandler tPipe : tPipes) transferredAmount += aTank.extractFluid(aTank.getStoredFluid().copyWithAmount(tPipe.insertFluid(aTank.getStoredFluid().copyWithAmount(tAmount), false)), false).getFluidAmount();
    }

    /** Divides but rounds up. */
    public static long divup(long aNumber, long aDivider) {
        return aNumber / aDivider + (aNumber % aDivider == 0 ? 0 : 1);
    }

    public static void burn(Level aWorld, int aX, int aY, int aZ) {
        BlockPos pos = new BlockPos(aX, aY, aZ);
        for (Direction tSide : Direction.values()) {
            fire(aWorld, pos.relative(tSide), false);
        }
    }

    public static boolean fire(Level aWorld, BlockPos pos, boolean aCheckFlammability) {
        BlockState tBlock = aWorld.getBlockState(pos);
        if (tBlock.getMaterial() == Material.LAVA || tBlock.getMaterial() == Material.FIRE) return false;
        if (tBlock.getMaterial() == Material.CLOTH_DECORATION || tBlock.getCollisionShape(aWorld, pos).isEmpty()) {
            if (AntimatterPlatformUtils.getFlammability(tBlock, aWorld, pos, Direction.NORTH) > 0) return aWorld.setBlock(pos, Blocks.FIRE.defaultBlockState(), 3);
            if (aCheckFlammability) {
                for (Direction tSide : Direction.values()) {
                    BlockState tAdjacent = aWorld.getBlockState(pos.relative(tSide));
                    if (tAdjacent.getBlock() == Blocks.CHEST || tAdjacent.getBlock() == Blocks.TRAPPED_CHEST) return aWorld.setBlock(pos, Blocks.FIRE.defaultBlockState(), 3);
                    if (AntimatterPlatformUtils.getFlammability(tAdjacent, aWorld, pos.relative(tSide), tSide.getOpposite()) > 0) return aWorld.setBlock(pos, Blocks.FIRE.defaultBlockState(), 3);
                }
            } else {
                return aWorld.setBlock(pos, Blocks.FIRE.defaultBlockState(), 3);
            }
        }
        return false;
    }

    @Override
    public Class<?> getCapClass() {
        return FluidContainer.class;
    }

    @Override
    public Optional<? extends FluidContainer> forSide(Direction side) {
        if (fluidHandler.isEmpty()) {
            fluidHandler = Optional.of(new PipeFluidHandler(this, type.getPressure(getPipeSize()) * 2, type.getPressure(getPipeSize()), 1, 0));
        }
        if (side == null){
            return Optional.of(new FluidHandlerNullSideWrapper(fluidHandler.get()));
        }
        return Optional.of(new PipeFluidHandlerSidedWrapper(fluidHandler.get(), this, side));
        /*if (FluidController.SLOOSH) {

        } else {
            return Optional.of(new PipeFluidHandlerSidedWrapper(new TesseractFluidCapability<>(this, side, !isConnector(), (stack, in, out, simulate) ->
            this.coverHandler.ifPresent(t -> t.onTransfer(stack, in, out, simulate))), this, side));
        }*/
    }

    @Override
    public Optional<? extends FluidContainer> forNullSide() {
        return forSide(null);
    }

    @Override
    public int drawInfo(InfoRenderWidget.TesseractFluidWidget instance, PoseStack stack, Font renderer, int left, int top) {
        renderer.draw(stack, "Pressure used: " + instance.stack.getFluidAmount(), left, top, 16448255);
        renderer.draw(stack, "Pressure total: " + getPressure()*20, left, top + 8, 16448255);
        renderer.draw(stack, "Fluid: " + FluidPlatformUtils.getFluidId(instance.stack.getFluid()).toString(), left, top + 16, 16448255);
        renderer.draw(stack, "(Above only in intersection)", left, top + 24, 16448255);
        //renderer.draw(stack, "Frame average: " + instance.holderPressure / 20, left, top + 32, 16448255);
        return 32;
    }

    @Override
    public List<String> getInfo() {
        List<String> list = super.getInfo();
        fluidHandler.ifPresent(t -> {
            for (int i = 0; i < t.getSize(); i++) {
                FluidHolder stack = t.getFluidInTank(i);
                list.add(FluidPlatformUtils.getFluidId(stack.getFluid()).toString() + " " + (stack.getFluidAmount() / TesseractGraphWrappers.dropletMultiplier) + " mb.");
            }
        });
        list.add("Pressure: " + getPipeType().getPressure(getPipeSize()));
        list.add("Max temperature: " + getPipeType().getTemperature());
        list.add(getPipeType().isGasProof() ? "Gas proof." : "Cannot handle gas.");
        list.add(getPipeType().isAcidProof() ? "Acid proof." : "Cannot handle acids.");
        return list;
    }

    public static boolean even(int... aCoords) {
        int i = 0;
        for (int tCoord : aCoords) {
            if (tCoord % 2 == 0) i++;
        }
        return i % 2 == 0;
    }
}
