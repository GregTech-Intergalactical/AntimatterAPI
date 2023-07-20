package muramasa.antimatter.tile.pipe;

import com.mojang.blaze3d.vertex.PoseStack;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.Dispatch;
import muramasa.antimatter.capability.FluidHandler;
import muramasa.antimatter.capability.pipe.PipeFluidHandler;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.widget.InfoRenderWidget;
import muramasa.antimatter.integration.jeirei.renderer.IInfoRenderer;
import muramasa.antimatter.pipe.types.FluidPipe;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.IFluidHandler;
import tesseract.FluidPlatformUtils;
import tesseract.TesseractCapUtils;
import tesseract.TesseractGraphWrappers;
import tesseract.api.capability.TesseractFluidCapability;
import tesseract.api.fluid.FluidController;
import tesseract.api.fluid.PipeFluidHolder;
import tesseract.api.fluid.IFluidPipe;

import java.util.List;
import java.util.Optional;

public class TileEntityFluidPipe<T extends FluidPipe<T>> extends TileEntityPipe<T> implements IFluidPipe, Dispatch.Sided<IFluidHandler>, IInfoRenderer<InfoRenderWidget.TesseractFluidWidget> {

    protected Optional<PipeFluidHandler> fluidHandler;
    private PipeFluidHolder holder;

    public TileEntityFluidPipe(T type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        if (fluidHandler == null) {
            fluidHandler = FluidController.SLOOSH ? Optional.of(new PipeFluidHandler(this, 1000 * (getPipeSize().ordinal() + 1), 1000, 1, 0)) : Optional.empty();
        }
        pipeCapHolder.set(() -> this);
    }

    @Override
    public void onLoad() {
        holder = new PipeFluidHolder(this);
        super.onLoad();
    }

    @Override
    public void onBlockUpdate(BlockPos neighbour) {
        super.onBlockUpdate(neighbour);
        TesseractGraphWrappers.FLUID.blockUpdate(getLevel(), getBlockPos().asLong(), neighbour.asLong());
    }


    @Override
    protected void register() {
        TesseractGraphWrappers.FLUID.registerConnector(getLevel(), getBlockPos().asLong(), this, isConnector());
    }

    @Override
    protected boolean deregister() {
        return TesseractGraphWrappers.FLUID.remove(getLevel(), getBlockPos().asLong());
    }


    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(Ref.KEY_MACHINE_FLUIDS))
            fluidHandler.ifPresent(t -> t.deserialize(tag.getCompound(Ref.KEY_MACHINE_FLUIDS)));
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        fluidHandler.ifPresent(t -> tag.put(Ref.KEY_MACHINE_FLUIDS, t.serialize(new CompoundTag())));
    }

    @Override
    public void onRemove() {
        fluidHandler.ifPresent(FluidHandler::onRemove);
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
        return getPipeType().getCapacity(getPipeSize());
    }

    @Override
    public long getPressure() {
        return getPipeType().getPressure(getPipeSize());
    }

    @Override
    public int getTemperature() {
        return getPipeType().getTemperature();
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

    @Override
    protected void serverTick(Level level, BlockPos pos, BlockState state) {
        super.serverTick(level, pos, state);
        this.getHolder().tick(getLevel().getGameTime());
    }

    @Override
    public Class<?> getCapClass() {
        return IFluidHandler.class;
    }

    @Override
    public Optional<? extends IFluidHandler> forSide(Direction side) {
        if (FluidController.SLOOSH) {
            if (fluidHandler == null) {
                fluidHandler = Optional.of(new PipeFluidHandler(this, 1000 * (getPipeSize().ordinal() + 1), 1000, 1, 0));
            }
        } else {
            return Optional.of(new TesseractFluidCapability<>(this, side, !isConnector(), (stack, in, out, simulate) ->
            this.coverHandler.ifPresent(t -> t.onTransfer(stack, in, out, simulate))));
        }
        return fluidHandler;
    }

    @Override
    public Optional<? extends IFluidHandler> forNullSide() {
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
                list.add(FluidPlatformUtils.getFluidId(stack.getFluid()).toString() + " " + stack.getAmount() + " mb.");
            }
        });
        list.add("Pressure: " + getPipeType().getPressure(getPipeSize()));
        list.add("Capacity: " + getPipeType().getCapacity(getPipeSize()));
        list.add("Max temperature: " + getPipeType().getTemperature());
        list.add(getPipeType().isGasProof() ? "Gas proof." : "Cannot handle gas.");
        return list;
    }
}
