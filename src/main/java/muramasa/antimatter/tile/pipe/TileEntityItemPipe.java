package muramasa.antimatter.tile.pipe;

import com.mojang.blaze3d.matrix.MatrixStack;
import muramasa.antimatter.capability.Dispatch;
import muramasa.antimatter.capability.pipe.PipeCoverHandler;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.widget.InfoRenderWidget;
import muramasa.antimatter.integration.jei.renderer.IInfoRenderer;
import muramasa.antimatter.pipe.types.ItemPipe;
import muramasa.antimatter.tesseract.ItemTileWrapper;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import tesseract.Tesseract;
import tesseract.api.capability.TesseractItemCapability;
import tesseract.api.item.IItemPipe;

public class TileEntityItemPipe<T extends ItemPipe<T>> extends TileEntityPipe<T> implements IItemPipe, Dispatch.Sided<IItemHandler>, IInfoRenderer<InfoRenderWidget.TesseractItemWidget> {

    public TileEntityItemPipe(T type, boolean covered) {
        super(type, covered);
        pipeCapHolder.set(() -> this);
    }

    @Override
    protected void initTesseract() {
        if (isServerSide()) Tesseract.ITEM.registerConnector(getWorld(), pos.toLong(), this); // this is connector class
        super.initTesseract();
    }

    @Override
    public void refreshConnection() {
        if (isServerSide()) {
            if (Tesseract.ITEM.remove(getWorld(), pos.toLong())) {
                Tesseract.ITEM.registerConnector(getWorld(), pos.toLong(), this); // this is connector class
            }
        }
        super.refreshConnection();
    }

    @Override
    public boolean validateTile(TileEntity tile, Direction side) {
        return tile instanceof TileEntityItemPipe || tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side).isPresent();
    }

    @Override
    public void registerNode(BlockPos pos, Direction side, boolean remove) {
        if (!remove) {
            ItemTileWrapper.wrap(this, getWorld(), pos, side, () -> world.getTileEntity(pos));
        } else {
            Tesseract.ITEM.remove(getWorld(), pos.toLong());
        }
    }

    @Override
    public void onRemove() {
        if (isServerSide()) Tesseract.ITEM.remove(getWorld(), pos.toLong());
        super.onRemove();
    }

    @Override
    public int getCapacity() {
        return getPipeType().getCapacity(getPipeSize());
    }

    @Override
    public boolean connects(Direction direction) {
        return canConnect(direction.getIndex());
    }

    @Override
    protected Capability<?> getCapability() {
        return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    }

    @Override
    public LazyOptional<IItemHandler> forSide(Direction side) {
        return LazyOptional.of(() -> new TesseractItemCapability(this, side));
    }

    @Override
    public void refresh() {

    }

    @Override
    public void addWidgets(GuiInstance instance, IGuiElement parent) {
        super.addWidgets(instance, parent);
        instance.addWidget(InfoRenderWidget.TesseractItemWidget.build().setPos(10,10));
    }

    @Override
    public void drawInfo(InfoRenderWidget.TesseractItemWidget instance, MatrixStack stack, FontRenderer renderer, int left, int top) {
        renderer.drawString(stack,"Total transferred in net: " + instance.transferred, left, top, 16448255);
        renderer.drawString(stack,"Cable transfers (stacks): " + instance.cableTransferred, left, top+ 8, 16448255);
    }

    public static class TileEntityCoveredItemPipe<T extends ItemPipe<T>> extends TileEntityItemPipe<T> implements ITickablePipe {

        public TileEntityCoveredItemPipe(T type) {
            super(type, true);
        }

        @Override
        public LazyOptional<PipeCoverHandler<?>> getCoverHandler() {
            return this.coverHandler;
        }

    }
}
