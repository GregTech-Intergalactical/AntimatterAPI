package muramasa.antimatter.blockentity.pipe;

import com.mojang.blaze3d.vertex.PoseStack;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.capability.Dispatch;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.cover.CoverFactory;
import muramasa.antimatter.cover.CoverPlate;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.widget.InfoRenderWidget;
import muramasa.antimatter.integration.jeirei.renderer.IInfoRenderer;
import muramasa.antimatter.pipe.BlockCable;
import muramasa.antimatter.pipe.types.Cable;
import muramasa.antimatter.pipe.types.PipeType;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import tesseract.TesseractCapUtils;
import tesseract.TesseractGraphWrappers;
import tesseract.api.capability.TesseractGTCapability;
import tesseract.api.gt.GTHolder;
import tesseract.api.gt.IEnergyHandler;
import tesseract.api.gt.IGTCable;

import java.util.Optional;

public class BlockEntityCable<T extends PipeType<T>> extends BlockEntityPipe<T> implements IGTCable, Dispatch.Sided<IEnergyHandler>, IInfoRenderer<InfoRenderWidget.TesseractGTWidget> {

    private long holder;

    public BlockEntityCable(T type, BlockPos pos, BlockState state) {
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
        return AntimatterAPI.all(CoverFactory.class).stream().filter(t -> {
            try {
                return t.get().get(ICoverHandler.empty(this), t.getValidTier(), Direction.SOUTH, t) instanceof CoverPlate;
            } catch (Exception ex) {
                return false;
            }
        }).toArray(CoverFactory[]::new);
    }

    @Override
    protected void register() {
        TesseractGraphWrappers.GT_ENERGY.registerConnector(getLevel(), getBlockPos().asLong(), this, isConnector());
    }

    @Override
    protected boolean deregister() {
        return TesseractGraphWrappers.GT_ENERGY.remove(getLevel(), getBlockPos().asLong());
    }

    @Override
    public Class<?> getCapClass() {
        return IEnergyHandler.class;
    }

    @Override
    public void onBlockUpdate(BlockPos neighbour) {
        super.onBlockUpdate(neighbour);
    }

    @Override
    public long getVoltage() {
        return ((Cable<?>) getPipeType()).getTier().getVoltage();
    }

    @Override
    public boolean insulated() {
        return ((BlockCable<?>) this.getBlockState().getBlock()).insulated;
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
    public double getLoss() {
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
        return TesseractCapUtils.getEnergyHandler(tile, dir.getOpposite()).isPresent();
    }

    @Override
    protected void serverTick(Level level, BlockPos pos, BlockState state) {
        super.serverTick(level, pos, state);
        this.setHolder(GTHolder.create(this, 0));
    }

    @Override
    public Optional<IEnergyHandler> forSide(Direction side) {
        return Optional.of(new TesseractGTCapability<>(this, side, !isConnector(), (stack, dir, input, simulate) ->
        this.coverHandler.map(t -> t.onTransfer(stack, dir, input, simulate)).orElse(false)));
    }

    @Override
    public Optional<IEnergyHandler> forNullSide() {
        return forSide(null);
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
