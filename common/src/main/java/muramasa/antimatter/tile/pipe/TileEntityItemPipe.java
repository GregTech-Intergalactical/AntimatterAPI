package muramasa.antimatter.tile.pipe;

import com.mojang.blaze3d.vertex.PoseStack;
import muramasa.antimatter.capability.Dispatch;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.widget.InfoRenderWidget;
import muramasa.antimatter.integration.jeirei.renderer.IInfoRenderer;
import muramasa.antimatter.pipe.types.ItemPipe;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import tesseract.TesseractGraphWrappers;
import tesseract.api.capability.TesseractItemCapability;
import tesseract.api.item.IItemPipe;

public class TileEntityItemPipe<T extends ItemPipe<T>> extends TileEntityPipe<T>
        implements IItemPipe, Dispatch.Sided<IItemHandler>, IInfoRenderer<InfoRenderWidget.TesseractItemWidget> {

    private int holder;

    public TileEntityItemPipe(T type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        pipeCapHolder.set(() -> this);
        this.holder = 0;
    }

    @Override
    protected void register() {
        TesseractGraphWrappers.ITEM.registerConnector(getLevel(), getBlockPos().asLong(), this, isConnector());
    }

    @Override
    protected boolean deregister() {
        return TesseractGraphWrappers.ITEM.remove(getLevel(), getBlockPos().asLong());
    }

    @Override
    public void onBlockUpdate(BlockPos neighbour) {
        super.onBlockUpdate(neighbour);
        TesseractGraphWrappers.ITEM.blockUpdate(getLevel(), getBlockPos().asLong(), neighbour.asLong());
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
        if (!super.validate(dir))
            return false;
        BlockEntity tile = level.getBlockEntity(getBlockPos().relative(dir));
        if (tile == null)
            return false;
        return tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir.getOpposite()).isPresent();
    }

    @Override
    public Class<?> getCapClass() {
        return IItemHandler.class;
    }

    @Override
    public LazyOptional<IItemHandler> forSide(Direction side) {
        return LazyOptional.of(() -> new TesseractItemCapability<>(this, side, !isConnector(), (stack, in, out,
                simulate) -> this.coverHandler.ifPresent(t -> t.onTransfer(stack, in, out, simulate))));
    }

    @Override
    public LazyOptional<? extends IItemHandler> forNullSide() {
        return forSide(null);
    }

    @Override
    protected void serverTick(Level level, BlockPos pos, BlockState state) {
        super.serverTick(level, pos, state);
        if (this.getLevel().getGameTime() % 20 == 0) this.setHolder(0);
    }

    @Override
    public void addWidgets(GuiInstance instance, IGuiElement parent) {
        super.addWidgets(instance, parent);
        instance.addWidget(InfoRenderWidget.TesseractItemWidget.build().setPos(10, 10));
    }

    @Override
    public int drawInfo(InfoRenderWidget.TesseractItemWidget instance, PoseStack stack, Font renderer,
            int left, int top) {
        renderer.draw(stack, "Total transferred in net: " + instance.transferred, left, top, 16448255);
        renderer.draw(stack, "Cable transfers (stacks): " + instance.cableTransferred, left, top + 8, 16448255);
        return 16;
    }

    @Override
    public int getHolder() {
        return holder;
    }

    @Override
    public void setHolder(int holder) {
        this.holder = holder;        
    }
}
