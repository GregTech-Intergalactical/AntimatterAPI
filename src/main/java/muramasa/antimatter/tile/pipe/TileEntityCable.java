package muramasa.antimatter.tile.pipe;

import com.mojang.blaze3d.vertex.PoseStack;
import muramasa.antimatter.capability.Dispatch;
import muramasa.antimatter.capability.pipe.PipeCoverHandler;
import muramasa.antimatter.cover.CoverFactory;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.widget.InfoRenderWidget;
import muramasa.antimatter.integration.jei.renderer.IInfoRenderer;
import muramasa.antimatter.pipe.types.Cable;
import muramasa.antimatter.pipe.types.PipeType;
import muramasa.antimatter.tesseract.EnergyTileWrapper;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import tesseract.Tesseract;
import tesseract.api.capability.TesseractGTCapability;
import tesseract.api.gt.GTHolder;
import tesseract.api.gt.IEnergyHandler;
import tesseract.api.gt.IGTCable;
import tesseract.api.gt.IGTNode;
import tesseract.graph.Graph.INodeGetter;

public class TileEntityCable<T extends PipeType<T>> extends TileEntityPipe<T> implements IGTCable, Dispatch.Sided<IEnergyHandler>, IInfoRenderer<InfoRenderWidget.TesseractGTWidget> {

    private long holder;

    public TileEntityCable(T type,BlockPos pos, BlockState state) {
        super(type, pos, state);
        pipeCapHolder.set(() -> this);
    }

    @Override
    public void onLoad() {
        this.holder = GTHolder.create(this, 0);
        super.onLoad();
    }

    @Override
    public CoverFactory[] getValidCovers() {
        return new CoverFactory[0];
    }

    @Override
    protected void register() {
        Tesseract.GT_ENERGY.registerConnector(getLevel(), getBlockPos().asLong(), this, getter());
    }

    public INodeGetter<IGTNode> getter() {
        return (pos, dir, cb) -> {
            BlockEntity tile = level.getBlockEntity(BlockPos.of(pos));
            LazyOptional<IEnergyHandler> capability = tile.getCapability(TesseractGTCapability.ENERGY_HANDLER_CAPABILITY, dir);
            if (capability.isPresent()) {
                if (cb != null) capability.addListener(t -> cb.run());
                return capability.resolve().get();
            } else {
                LazyOptional<IEnergyStorage> cap = tile.getCapability(CapabilityEnergy.ENERGY, dir);
                if (cap.isPresent()) {
                    EnergyTileWrapper node = new EnergyTileWrapper(tile, cap.orElse(null));
                    cap.addListener(o -> cb.run());
                    return node;
                }
            }
            return null;
        };
    }

    @Override
    protected boolean deregister() {
        return Tesseract.GT_ENERGY.remove(getLevel(), getBlockPos().asLong());
    }

    @Override
    protected Capability<?> getCapability() {
        return TesseractGTCapability.ENERGY_HANDLER_CAPABILITY;
    }

    @Override
    public void onBlockUpdate(BlockPos neighbour) {
        super.onBlockUpdate(neighbour);
        if (this.isConnector()) {
            Tesseract.GT_ENERGY.blockUpdate(getLevel(), getBlockPos().asLong(), neighbour.asLong(), getter());
        }
    }

    @Override
    public int getVoltage() {
        return ((Cable<?>) getPipeType()).getTier().getVoltage();
    }

    @Override
    public long getHolder() {
        return holder;
    }

    @Override
    public void setHolder(long holder) {
        this.holder = holder;
    }

    @Override
    public int getLoss() {
        return ((Cable<?>) getPipeType()).getLoss();
    }

    @Override
    public int getAmps() {
        return ((Cable<?>) getPipeType()).getAmps(getPipeSize());
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
        return tile.getCapability(TesseractGTCapability.ENERGY_HANDLER_CAPABILITY, dir.getOpposite()).isPresent() || tile.getCapability(CapabilityEnergy.ENERGY, dir).isPresent();
    }

    @Override
    protected void serverTick(Level level, BlockPos pos, BlockState state) {
        super.serverTick(level, pos, state);
        this.setHolder(GTHolder.create(this, 0));
    }

    @Override
    public LazyOptional<? extends IEnergyHandler> forSide(Direction side) {
        return LazyOptional.of(() -> new TesseractGTCapability<>(this, side, !isConnector(), (stack,in,out,simulate) -> 
        this.coverHandler.ifPresent(t -> t.onTransfer(stack, in, out, simulate))));
    }

    @Override
    public LazyOptional<? extends IEnergyHandler> forNullSide() {
        return forSide(null);
    }

    @Override
    public void refresh() {

    }

    @Override
    public void addWidgets(GuiInstance instance, IGuiElement parent) {
        super.addWidgets(instance, parent);
        instance.addWidget(InfoRenderWidget.TesseractGTWidget.build().setPos(10, 10));
    }

    @Override
    public int drawInfo(InfoRenderWidget.TesseractGTWidget instance, PoseStack stack, Font renderer, int left, int top) {
        renderer.draw(stack, "Amp average: " + instance.ampAverage, left, top, 16448255);
       // renderer.draw(stack, "Cable average: " + instance.cableAverage, left, top + 8, 16448255);
        renderer.draw(stack, "Average extracted: " + ((double) instance.voltAverage) / 20, left, top + 16, 16448255);
        renderer.draw(stack, "Average inserted: " + ((double) (instance.voltAverage - instance.loss)) / 20, left, top + 24, 16448255);
        renderer.draw(stack, "Loss average: " + (double) instance.loss / 20, left, top + 32, 16448255);
        return 40;
    }
}
