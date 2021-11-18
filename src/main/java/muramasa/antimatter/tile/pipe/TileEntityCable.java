package muramasa.antimatter.tile.pipe;

import com.mojang.blaze3d.matrix.MatrixStack;
import muramasa.antimatter.capability.Dispatch;
import muramasa.antimatter.capability.pipe.PipeCoverHandler;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.widget.InfoRenderWidget;
import muramasa.antimatter.integration.jei.renderer.IInfoRenderer;
import muramasa.antimatter.pipe.types.Cable;
import muramasa.antimatter.pipe.types.PipeType;
import muramasa.antimatter.tesseract.EnergyTileWrapper;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import tesseract.Tesseract;
import tesseract.api.GraphWrapper;
import tesseract.api.capability.TesseractGTCapability;
import tesseract.api.gt.IEnergyHandler;
import tesseract.api.gt.IGTCable;

public class TileEntityCable<T extends PipeType<T>> extends TileEntityPipe<T> implements IGTCable, Dispatch.Sided<IEnergyHandler>, IInfoRenderer<InfoRenderWidget.TesseractGTWidget> {

    public TileEntityCable(T type, boolean covered) {
        super(type, covered);
        pipeCapHolder.set(() -> this);
    }

    @Override
    public void addNode(Direction side) {
        Tesseract.GT_ENERGY.registerNode(level, worldPosition.relative(side).asLong(), side.getOpposite(), (getPos, dir) -> {
            TileEntity tile = level.getBlockEntity(BlockPos.of(getPos));
            LazyOptional<IEnergyHandler> capability = tile.getCapability(TesseractGTCapability.ENERGY_HANDLER_CAPABILITY, dir);
            if (capability.isPresent()) {
                capability.addListener(o -> this.onSideCapInvalidate(side));
                return capability.resolve().get();
            } else {
                LazyOptional<IEnergyStorage> cap = tile.getCapability(CapabilityEnergy.ENERGY, side.getOpposite());
                if (cap.isPresent()) {
                    EnergyTileWrapper node = new EnergyTileWrapper(tile, cap.orElse(null));
                    cap.addListener(o -> this.onSideCapInvalidate(side));
                    return node;
                }
            }
            return null;
        });
    }

    @Override
    protected GraphWrapper getWrapper() {
        return Tesseract.GT_ENERGY;
    }

    @Override
    protected Capability<?> getCapability() {
        return TesseractGTCapability.ENERGY_HANDLER_CAPABILITY;
    }

    @Override
    public int getVoltage() {
        return ((Cable<?>) getPipeType()).getTier().getVoltage();
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
        TileEntity tile = level.getBlockEntity(getBlockPos().relative(dir));
        if (tile == null) return false;
        return tile.getCapability(TesseractGTCapability.ENERGY_HANDLER_CAPABILITY, dir.getOpposite()).isPresent() || tile.getCapability(CapabilityEnergy.ENERGY, dir).isPresent();
    }

    @Override
    public LazyOptional<? extends IEnergyHandler> forSide(Direction side) {
        return LazyOptional.of(() -> new TesseractGTCapability(this, side));
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
    public int drawInfo(InfoRenderWidget.TesseractGTWidget instance, MatrixStack stack, FontRenderer renderer, int left, int top) {
        renderer.draw(stack, "Amp average: " + instance.ampAverage, left, top, 16448255);
        renderer.draw(stack, "Cable average: " + instance.cableAverage, left, top + 8, 16448255);
        renderer.draw(stack, "Average extracted: " + ((double) instance.voltAverage) / 20, left, top + 16, 16448255);
        renderer.draw(stack, "Average inserted: " + ((double) (instance.voltAverage - instance.loss)) / 20, left, top + 24, 16448255);
        renderer.draw(stack, "Loss average: " + (double) instance.loss / 20, left, top + 32, 16448255);
        return 40;
    }


    public static class TileEntityCoveredCable<T extends Cable<T>> extends TileEntityCable<T> implements ITickablePipe {

        public TileEntityCoveredCable(T type) {
            super(type, true);
        }

        @Override
        public LazyOptional<PipeCoverHandler<?>> getCoverHandler() {
            return this.coverHandler;
        }
    }
}
