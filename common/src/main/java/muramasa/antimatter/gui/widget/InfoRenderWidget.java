package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.utils.FluidHooks;
import muramasa.antimatter.capability.machine.MachineRecipeHandler;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.Widget;
import muramasa.antimatter.integration.jeirei.renderer.IInfoRenderer;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;
import muramasa.antimatter.tile.pipe.TileEntityFluidPipe;
import muramasa.antimatter.tile.pipe.TileEntityPipe;
import net.minecraft.client.Minecraft;
import tesseract.FluidPlatformUtils;
import tesseract.TesseractGraphWrappers;
import tesseract.api.ITickingController;
import tesseract.api.fluid.PipeFluidHolder;
import tesseract.api.gt.GTController;
import tesseract.api.item.ItemController;

import java.util.Set;

import static muramasa.antimatter.gui.ICanSyncData.SyncDirection.SERVER_TO_CLIENT;

public class InfoRenderWidget<T extends InfoRenderWidget<T>> extends Widget {

    final IInfoRenderer<T> renderer;

    protected InfoRenderWidget(GuiInstance gui, IGuiElement parent, IInfoRenderer<T> renderer) {
        super(gui, parent);
        this.renderer = renderer;
    }

    @Override
    public void render(PoseStack matrixStack, double mouseX, double mouseY, float partialTicks) {
        renderer.drawInfo((T) this, matrixStack, Minecraft.getInstance().font, realX(), realY());
    }

    public static <T extends InfoRenderWidget<T>> WidgetSupplier build(IInfoRenderer<T> renderer) {
        return builder((a, b) -> new InfoRenderWidget<>(a, b, renderer));
    }

    public static WidgetSupplier build() {
        return builder((a, b) -> new InfoRenderWidget<>(a, b, (IInfoRenderer<?>) a.handler));
    }

    public static class MultiRenderWidget extends InfoRenderWidget<MultiRenderWidget> {

        public int currentProgress = 0;
        public int maxProgress = 0;
        public int overclock = 0;
        public long euT = 0;

        protected MultiRenderWidget(GuiInstance gui, IGuiElement parent, IInfoRenderer<MultiRenderWidget> renderer) {
            super(gui, parent, renderer);
        }

        public boolean drawActiveInfo() {
            return true;
        }

        @Override
        public void init() {
            super.init();
            TileEntityMultiMachine<?> m = (TileEntityMultiMachine<?>) gui.handler;
            gui.syncInt(() -> m.recipeHandler.map(MachineRecipeHandler::getCurrentProgress).orElse(0), i -> this.currentProgress = i, SERVER_TO_CLIENT);
            gui.syncInt(() -> m.recipeHandler.map(MachineRecipeHandler::getMaxProgress).orElse(0), i -> this.maxProgress = i, SERVER_TO_CLIENT);
            gui.syncInt(() -> m.recipeHandler.map(MachineRecipeHandler::getOverclock).orElse(0), i -> this.overclock = i, SERVER_TO_CLIENT);
            gui.syncLong(() -> m.recipeHandler.map(MachineRecipeHandler::getPower).orElse(0L), i -> this.euT = i, SERVER_TO_CLIENT);
        }

        public static WidgetSupplier build() {
            return builder((a, b) -> new MultiRenderWidget(a, b, (IInfoRenderer) a.handler));
        }
    }

    public static class TesseractGTWidget extends InfoRenderWidget<TesseractGTWidget> {

        public long voltAverage = 0;
        public long ampAverage = 0;
        //public int cableAverage = 0;
        public long loss = 0;

        protected TesseractGTWidget(GuiInstance gui, IGuiElement parent, IInfoRenderer<TesseractGTWidget> renderer) {
            super(gui, parent, renderer);
        }

        @Override
        public void init() {
            super.init();
            TileEntityPipe<?> pipe = (TileEntityPipe<?>) gui.handler;
            final long pos = pipe.getBlockPos().asLong();
            gui.syncLong(() -> {
                ITickingController controller = TesseractGraphWrappers.GT_ENERGY.getController(pipe.getLevel(), pipe.getBlockPos().asLong());
                if (controller == null) return 0L;
                GTController gt = (GTController) controller;
                return gt.getTotalVoltage();
            }, a -> this.voltAverage = a, SERVER_TO_CLIENT);
            gui.syncLong(() -> {
                ITickingController controller = TesseractGraphWrappers.GT_ENERGY.getController(pipe.getLevel(), pipe.getBlockPos().asLong());
                if (controller == null) return 0L;
                GTController gt = (GTController) controller;
                return gt.totalAmps();
            }, a -> this.ampAverage = a, SERVER_TO_CLIENT);
            /*gui.syncInt(() -> {
                ITickingController controller = Tesseract.GT_ENERGY.getController(pipe.getLevel(), pipe.getBlockPos().asLong());
                if (controller == null) return 0;
                GTController gt = (GTController) controller;
                return gt.cableFrameAverage(pos);
            }, a -> this.cableAverage = a, SERVER_TO_CLIENT);*/
            gui.syncLong(() -> {
                ITickingController controller = TesseractGraphWrappers.GT_ENERGY.getController(pipe.getLevel(), pipe.getBlockPos().asLong());
                if (controller == null) return 0L;
                GTController gt = (GTController) controller;
                return gt.totalLoss();
            }, a -> this.loss = a, SERVER_TO_CLIENT);
        }

        public static WidgetSupplier build() {
            return builder((a, b) -> new TesseractGTWidget(a, b, (IInfoRenderer<TesseractGTWidget>) a.handler));
        }
    }

    public static class TesseractItemWidget extends InfoRenderWidget<TesseractItemWidget> {

        public int transferred = 0;
        public int cableTransferred = 0;

        protected TesseractItemWidget(GuiInstance gui, IGuiElement parent, IInfoRenderer<TesseractItemWidget> renderer) {
            super(gui, parent, renderer);
        }

        @Override
        public void init() {
            super.init();
            TileEntityPipe<?> pipe = (TileEntityPipe<?>) gui.handler;
            final long pos = pipe.getBlockPos().asLong();
            gui.syncInt(() -> {
                ITickingController controller = TesseractGraphWrappers.ITEM.getController(pipe.getLevel(), pipe.getBlockPos().asLong());
                if (controller == null) return 0;
                ItemController gt = (ItemController) controller;
                return gt.getTransferred();
            }, a -> this.transferred = a, SERVER_TO_CLIENT);
            gui.syncInt(() -> {
                ITickingController controller = TesseractGraphWrappers.ITEM.getController(pipe.getLevel(), pipe.getBlockPos().asLong());
                if (controller == null) return 0;
                ItemController gt = (ItemController) controller;
                return gt.getCableTransferred(pipe.getBlockPos().asLong());
            }, a -> this.cableTransferred = a, SERVER_TO_CLIENT);
        }

        public static WidgetSupplier build() {
            return builder((a, b) -> new TesseractItemWidget(a, b, (IInfoRenderer<TesseractItemWidget>) a.handler));
        }
    }

    public static class TesseractFluidWidget extends InfoRenderWidget<TesseractFluidWidget> {

        public int holderPressure = 0;
        public FluidHolder stack = FluidHooks.emptyFluid();

        protected TesseractFluidWidget(GuiInstance gui, IGuiElement parent, IInfoRenderer<TesseractFluidWidget> renderer) {
            super(gui, parent, renderer);
        }

        @Override
        public void init() {
            super.init();
            TileEntityFluidPipe<?> pipe = (TileEntityFluidPipe<?>) gui.handler;
            final long pos = pipe.getBlockPos().asLong();
            /*gui.syncInt(() -> {
                ITickingController controller = Tesseract.FLUID.getController(pipe.getLevel(), pipe.getBlockPos().asLong());
                if (controller instanceof FluidController c) {
                    return c.sentPressure.get(pos);
                }
                return 0;
            }, a -> this.holderPressure = a, SERVER_TO_CLIENT);*/
            gui.syncFluidStack(() -> {
                PipeFluidHolder holder = pipe.getHolder();
                if (holder != null) {
                    Set<PipeFluidHolder.SetHolder> fluids = holder.getFluids();
                    if (fluids != null && fluids.size() > 0) {
                        long pressure = holder.tickPressure*20 - holder.getPressureAvailable();
                        return FluidPlatformUtils.createFluidStack(fluids.iterator().next().fluid, pressure);
                    }
                }
                return FluidHooks.emptyFluid();
            }, a -> this.stack = a, SERVER_TO_CLIENT);
        }

        public static WidgetSupplier build() {
            return builder((a, b) -> new TesseractFluidWidget(a, b, (IInfoRenderer<TesseractFluidWidget>) a.handler));
        }
    }
}
