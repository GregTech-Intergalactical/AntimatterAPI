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
import tesseract.api.GraphWrapper;
import tesseract.api.capability.TesseractItemCapability;
import tesseract.api.item.IItemPipe;

public class TileEntityItemPipe<T extends ItemPipe<T>> extends TileEntityPipe<T> implements IItemPipe, Dispatch.Sided<IItemHandler>, IInfoRenderer<InfoRenderWidget.TesseractItemWidget> {

    public TileEntityItemPipe(T type, boolean covered) {
        super(type, covered);
        pipeCapHolder.set(() -> this);
    }

    @Override
    protected GraphWrapper getWrapper() {
        return Tesseract.ITEM;
    }

    @Override
    public void addNode(Direction side) {
        Tesseract.ITEM.registerNode(getLevel(), worldPosition.relative(side).asLong(), side.getOpposite(), (pos, dir) -> {
            TileEntity tile = getLevel().getBlockEntity(BlockPos.of(pos));
            if (tile == null) {
                return null;
            }
            LazyOptional<IItemHandler> capability = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir);
            if (capability.isPresent()) {
                ItemTileWrapper node = new ItemTileWrapper(tile, capability.orElse(null));
                capability.addListener(o -> this.onSideCapInvalidate(side));
                return node;
            } else {
                return null;
            }
        });
    }

    @Override
    public int getCapacity() {
        return getPipeType().getCapacity(getPipeSize());
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
        return tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir.getOpposite()).isPresent();
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
    public LazyOptional<? extends IItemHandler> forNullSide() {
        return forSide(null);
    }

    @Override
    public void refresh() {

    }

    @Override
    public void addWidgets(GuiInstance instance, IGuiElement parent) {
        super.addWidgets(instance, parent);
        instance.addWidget(InfoRenderWidget.TesseractItemWidget.build().setPos(10, 10));
    }

    @Override
    public int drawInfo(InfoRenderWidget.TesseractItemWidget instance, MatrixStack stack, FontRenderer renderer, int left, int top) {
        renderer.draw(stack, "Total transferred in net: " + instance.transferred, left, top, 16448255);
        renderer.draw(stack, "Cable transfers (stacks): " + instance.cableTransferred, left, top + 8, 16448255);
        return 16;
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
