package muramasa.antimatter.tile.pipe;

import com.mojang.blaze3d.vertex.PoseStack;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.Dispatch;
import muramasa.antimatter.capability.pipe.PipeCoverHandler;
import muramasa.antimatter.capability.pipe.PipeFluidHandler;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.widget.InfoRenderWidget;
import muramasa.antimatter.integration.jei.renderer.IInfoRenderer;
import muramasa.antimatter.pipe.types.FluidPipe;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import tesseract.Tesseract;
import tesseract.api.capability.TesseractFluidCapability;
import tesseract.api.fluid.FluidController;
import tesseract.api.fluid.FluidHolder;
import tesseract.api.fluid.IFluidNode;
import tesseract.api.fluid.IFluidPipe;
import tesseract.graph.Graph.INodeGetter;

import java.util.List;

public class TileEntityFluidPipe<T extends FluidPipe<T>> extends TileEntityPipe<T> implements IFluidPipe, Dispatch.Sided<IFluidHandler>, IInfoRenderer<InfoRenderWidget.TesseractFluidWidget> {

    protected LazyOptional<PipeFluidHandler> fluidHandler;
    private FluidHolder holder;

    public TileEntityFluidPipe(T type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        if (fluidHandler == null) {
            fluidHandler = FluidController.SLOOSH ? LazyOptional.of(() -> new PipeFluidHandler(this, 1000 * (getPipeSize().ordinal() + 1), 1000, 1, 0)) : LazyOptional.empty();
        }
        pipeCapHolder.set(() -> this);
    }

    @Override
    public void onLoad() {
        holder = new FluidHolder(this);
        super.onLoad();
    }

    @Override
    public void onBlockUpdate(BlockPos neighbour) {
        super.onBlockUpdate(neighbour);
        Tesseract.FLUID.blockUpdate(getLevel(), getBlockPos().asLong(), neighbour.asLong(), getter());
    }


    @Override
    protected void register() {
        Tesseract.FLUID.registerConnector(getLevel(), getBlockPos().asLong(), this, getter(), isConnector());
    }

    public INodeGetter<IFluidNode> getter() {
        return (pos, dir, cb) -> {
            BlockEntity tile = level.getBlockEntity(BlockPos.of(pos));
            if (tile == null) {
                return null;
            }
            LazyOptional<IFluidHandler> capability = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir);
            if (capability.isPresent()) {
                if (cb != null) capability.addListener(o -> cb.run());
                IFluidHandler handler = capability.orElse(null);
                return handler instanceof IFluidNode ? (IFluidNode) handler: new IFluidNode.FluidTileWrapper(tile, handler);
            } else {
                return null;
            }
        };
    }


    @Override
    protected boolean deregister() {
        return Tesseract.FLUID.remove(getLevel(), getBlockPos().asLong());
    }


    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(Ref.KEY_MACHINE_FLUIDS))
            fluidHandler.ifPresent(t -> t.deserializeNBT(tag.getCompound(Ref.KEY_MACHINE_FLUIDS)));
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        CompoundTag nbt = super.save(tag);
        fluidHandler.ifPresent(t -> tag.put(Ref.KEY_MACHINE_FLUIDS, t.serializeNBT()));
        return nbt;
    }

    @Override
    public void onRemove() {
        fluidHandler.ifPresent(t -> t.onRemove());
        fluidHandler.invalidate();
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
    public FluidHolder getHolder() {
        return holder;
    }

    @Override
    public int getCapacity() {
        return getPipeType().getCapacity(getPipeSize());
    }

    @Override
    public int getPressure() {
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
        BlockEntity tile = level.getBlockEntity(getBlockPos().relative(dir));
        if (tile == null) return false;
        return tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir.getOpposite()).isPresent();
    }

    @Override
    protected void serverTick(Level level, BlockPos pos, BlockState state) {
        super.serverTick(level, pos, state);
        this.getHolder().tick(getLevel().getGameTime());
    }

    @Override
    protected Capability<?> getCapability() {
        return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
    }

    @Override
    public LazyOptional<? extends IFluidHandler> forSide(Direction side) {
        if (FluidController.SLOOSH) {
            if (fluidHandler == null) {
                fluidHandler = LazyOptional.of(() -> new PipeFluidHandler(this, 1000 * (getPipeSize().ordinal() + 1), 1000, 1, 0));
            }
        } else {
            return LazyOptional.of(() -> new TesseractFluidCapability<>(this, side, !isConnector(), (stack, in, out, simulate) -> 
            this.coverHandler.ifPresent(t -> t.onTransfer(stack, in, out, simulate))));
        }
        return fluidHandler;
    }

    @Override
    public LazyOptional<? extends IFluidHandler> forNullSide() {
        return forSide(null);
    }

    @Override
    public int drawInfo(InfoRenderWidget.TesseractFluidWidget instance, PoseStack stack, Font renderer, int left, int top) {
        renderer.draw(stack, "Pressure: " + instance.holderPressure, left, top, 16448255);
        renderer.draw(stack, "Fluid: " + instance.stack.getFluid().getRegistryName().toString(), left, top, 16448255);
        renderer.draw(stack, "Amount: " + instance.stack.getAmount(), left, top, 16448255);
        return 16;
    }

    @Override
    public List<String> getInfo() {
        List<String> list = super.getInfo();
        fluidHandler.ifPresent(t -> {
            for (int i = 0; i < t.getTanks(); i++) {
                FluidStack stack = t.getFluidInTank(i);
                list.add(stack.getFluid().getRegistryName().toString() + " " + stack.getAmount() + " mb.");
            }
        });
        list.add("Pressure: " + getPipeType().getPressure(getPipeSize()));
        list.add("Capacity: " + getPipeType().getCapacity(getPipeSize()));
        list.add("Max temperature: " + getPipeType().getTemperature());
        list.add(getPipeType().isGasProof() ? "Gas proof." : "Cannot handle gas.");
        return list;
    }
}
