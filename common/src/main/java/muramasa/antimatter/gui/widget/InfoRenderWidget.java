package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.utils.FluidHooks;
import muramasa.antimatter.blockentity.pipe.BlockEntityFluidPipe;
import muramasa.antimatter.capability.machine.MachineRecipeHandler;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.Widget;
import muramasa.antimatter.integration.jeirei.renderer.IInfoRenderer;
import muramasa.antimatter.blockentity.multi.BlockEntityMultiMachine;
import muramasa.antimatter.blockentity.pipe.BlockEntityPipe;
import net.minecraft.client.Minecraft;
import tesseract.FluidPlatformUtils;
import tesseract.TesseractGraphWrappers;
import tesseract.api.ITickingController;
import tesseract.api.fluid.PipeFluidHolder;
import tesseract.api.gt.GTController;

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
            BlockEntityMultiMachine<?> m = (BlockEntityMultiMachine<?>) gui.handler;
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
        public double loss = 0;

        protected TesseractGTWidget(GuiInstance gui, IGuiElement parent, IInfoRenderer<TesseractGTWidget> renderer) {
            super(gui, parent, renderer);
        }

        @Override
        public void init() {
            super.init();
            BlockEntityPipe<?> pipe = (BlockEntityPipe<?>) gui.handler;
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
            gui.syncDouble(() -> {
                ITickingController controller = TesseractGraphWrappers.GT_ENERGY.getController(pipe.getLevel(), pipe.getBlockPos().asLong());
                if (controller == null) return 0.0;
                GTController gt = (GTController) controller;
                return gt.totalLoss();
            }, a -> this.loss = a, SERVER_TO_CLIENT);
        }

        public static WidgetSupplier build() {
            return builder((a, b) -> new TesseractGTWidget(a, b, (IInfoRenderer<TesseractGTWidget>) a.handler));
        }
    }
}
