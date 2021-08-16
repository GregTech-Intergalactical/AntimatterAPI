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
import tesseract.Tesseract;
import tesseract.api.capability.TesseractGTCapability;
import tesseract.api.gt.IEnergyHandler;
import tesseract.api.gt.IGTCable;

public class TileEntityCable<T extends PipeType<T>> extends TileEntityPipe<T> implements IGTCable, Dispatch.Sided<IEnergyHandler>, IInfoRenderer<InfoRenderWidget.TesseractGTWidget> {

    public TileEntityCable(T type, boolean covered) {
        super(type, covered);
        pipeCapHolder.set(() -> this);
    }

    @Override
    public void refreshConnection() {
        if (isServerSide()) {
            if (Tesseract.GT_ENERGY.remove(getWorld(), pos.toLong())) {
                Tesseract.GT_ENERGY.registerConnector(getWorld(), pos.toLong(), this); // this is connector class
            }
        }
        super.refreshConnection();
    }

    @Override
    protected void initTesseract() {
        if (isServerSide()) Tesseract.GT_ENERGY.registerConnector(getWorld(), pos.toLong(), this); // this is connector class
        super.initTesseract();
    }

    @Override
    public void onRemove() {
        if (isServerSide()) Tesseract.GT_ENERGY.remove(getWorld(), pos.toLong());
        super.onRemove();
    }

    @Override
    public void registerNode(BlockPos pos, Direction side, boolean remove) {
        if (!remove) {
            EnergyTileWrapper.wrap(this, getWorld(), pos, side, () -> world.getTileEntity(pos));
        } else {
           Tesseract.GT_ENERGY.remove(getWorld(), pos.toLong());
        }
    }

    @Override
    public boolean validateTile(TileEntity tile, Direction side) {
        return tile instanceof TileEntityCable || tile.getCapability(TesseractGTCapability.ENERGY_HANDLER_CAPABILITY, side).isPresent() || tile.getCapability(CapabilityEnergy.ENERGY, side).isPresent();
    }

    @Override
    protected Capability<?> getCapability() {
        return TesseractGTCapability.ENERGY_HANDLER_CAPABILITY;
    }
    
    @Override
    public int getVoltage() {
        return ((Cable<?>)getPipeType()).getTier().getVoltage();
    }

    @Override
    public int getLoss() {
        return ((Cable<?>)getPipeType()).getLoss();
    }

    @Override
    public int getAmps() {
        return ((Cable<?>)getPipeType()).getAmps(getPipeSize());
    }

    @Override
    public boolean connects(Direction direction) {
        return canConnect(direction.getIndex());
    }

    @Override
    public LazyOptional<? extends IEnergyHandler> forSide(Direction side) {
        return LazyOptional.of(() -> new TesseractGTCapability(this, side));
    }

    @Override
    public void refresh() {

    }

    @Override
    public void addWidgets(GuiInstance instance, IGuiElement parent) {
        super.addWidgets(instance, parent);
        instance.addWidget(InfoRenderWidget.TesseractGTWidget.build().setPos(10,10));
    }

    @Override
    public int drawInfo(InfoRenderWidget.TesseractGTWidget instance, MatrixStack stack, FontRenderer renderer, int left, int top) {
        renderer.drawString(stack,"Amp average: " + instance.ampAverage, left, top+ 8, 16448255);
        renderer.drawString(stack,"Cable average: " + instance.cableAverage, left, top+ 16, 16448255);
        renderer.drawString(stack,"Average extracted: " + instance.voltAverage/20, left, top, 16448255);
        renderer.drawString(stack,"Average inserted: " + (instance.voltAverage - instance.loss)/20, left, top, 16448255);
        renderer.drawString(stack,"Loss average: " + instance.loss/20, left, top + 24, 16448255);
        return 32;
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
