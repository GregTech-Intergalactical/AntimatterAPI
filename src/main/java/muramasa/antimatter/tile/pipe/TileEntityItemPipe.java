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
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
import tesseract.api.item.IItemNode;
import tesseract.api.item.IItemPipe;
import tesseract.util.Pos;

public class TileEntityItemPipe<T extends ItemPipe<T>> extends TileEntityPipe<T> implements IItemPipe, Dispatch.Sided<IItemHandler>, IInfoRenderer<InfoRenderWidget.TesseractItemWidget> {

    public TileEntityItemPipe(T type, boolean covered) {
        super(type, covered);
        pipeCapHolder.set(() -> this);
    }
    

    @Override
    protected void register() {
        Tesseract.ITEM.registerConnector(getLevel(), getBlockPos().asLong(), this,(pos, dir, cb) -> {
            TileEntity tile = getLevel().getBlockEntity(BlockPos.of(pos));
            if (tile == null) {
                return null;
            }
            LazyOptional<IItemHandler> h = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir);
            if (h.isPresent()) {
                h.addListener(t -> cb.run());
                return new ItemTileWrapper(tile, h.orElse(null));
            }
            return null;
        });
        
    }

    @Override
    protected boolean deregister() {
        return Tesseract.ITEM.remove(getLevel(), getBlockPos().asLong());
    }

    @Override
    public void onBlockUpdate(BlockPos neighbour) {
        super.onBlockUpdate(neighbour);
        if (this.isConnector())  Tesseract.ITEM.blockUpdate(getLevel(), getBlockPos().asLong(), neighbour.asLong(), (pos, dir, cb) -> {
            TileEntity tile = getLevel().getBlockEntity(BlockPos.of(pos));
            if (tile == null) {
                return null;
            }
            LazyOptional<IItemHandler> h = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir);
            if (h.isPresent()) {
                h.addListener(t -> cb.run());
                return new ItemTileWrapper(tile, h.orElse(null));
            }
            return null;
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
        return LazyOptional.of(() -> new TesseractItemCapability(this, side, !isConnector(), (stack, in, out, simulate) -> 
            this.coverHandler.ifPresent(t -> t.onTransfer(stack, in, out, simulate))));
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

    public static class TileEntityCoveredItemPipe<T extends ItemPipe<T>> extends TileEntityItemPipe<T> implements ITickablePipe, IItemNode {

        public TileEntityCoveredItemPipe(T type) {
            super(type, true);
        }

        @Override
        public LazyOptional<PipeCoverHandler<?>> getCoverHandler() {
            return this.coverHandler;
        }

        @Override
        public int getSlots() {
            return 0;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return null;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return null;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 0;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return true;
        }

        @Override
        public int getPriority(Direction direction) {
            return 0;
        }

        @Override
        public boolean isEmpty(int slot) {
            return true;
        }

        @Override
        public boolean canOutput() {
            return true;
        }

        @Override
        public boolean canInput() {
            return true;
        }

        @Override
        public boolean canInput(Direction direction) {
            return true;
        }

        @Override
        public boolean canOutput(Direction direction) {
            return true;
        }

    }
}
