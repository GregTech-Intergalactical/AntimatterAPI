package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import muramasa.antimatter.capability.machine.MachineRecipeHandler;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.Widget;
import muramasa.antimatter.integration.jei.renderer.IInfoRenderer;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;
import muramasa.antimatter.tile.pipe.TileEntityPipe;
import net.minecraft.client.Minecraft;
import tesseract.Tesseract;
import tesseract.api.ITickingController;
import tesseract.api.gt.GTController;
import tesseract.api.item.ItemController;

public class InfoRenderWidget<T extends InfoRenderWidget<T>> extends Widget {

    final IInfoRenderer<T> renderer;

    protected InfoRenderWidget(GuiInstance gui, IGuiElement parent, IInfoRenderer<T> renderer) {
        super(gui, parent);
        this.renderer = renderer;
    }

    @Override
    public void render(MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks) {
        renderer.drawInfo((T)this, matrixStack, Minecraft.getInstance().fontRenderer, realX(), realY());
    }

    public static <T extends InfoRenderWidget<T>> WidgetSupplier build(IInfoRenderer<T> renderer) {
        return builder((a,b) -> new InfoRenderWidget<>(a,b,renderer));
    }

    public static WidgetSupplier build() {
        return builder((a,b) -> new InfoRenderWidget<>(a,b, (IInfoRenderer<?>) a.handler));
    }

    public static class MultiRenderWidget extends InfoRenderWidget<MultiRenderWidget> {

        public int currentProgress = 0;
        public int maxProgress = 0;
        public int overclock = 0;
        public long euT = 0;

        protected MultiRenderWidget(GuiInstance gui, IGuiElement parent, IInfoRenderer<MultiRenderWidget> renderer) {
            super(gui, parent, renderer);
        }

        @Override
        public void init() {
            super.init();
            TileEntityMultiMachine<?> m = (TileEntityMultiMachine<?>) gui.handler;
            gui.syncInt(() -> m.recipeHandler.map(MachineRecipeHandler::getCurrentProgress).orElse(0), i -> this.currentProgress = i);
            gui.syncInt(() -> m.recipeHandler.map(MachineRecipeHandler::getMaxProgress).orElse(0), i -> this.maxProgress = i);
            gui.syncInt(() -> m.recipeHandler.map(MachineRecipeHandler::getOverclock).orElse(0), i -> this.overclock = i);
            gui.syncLong(() -> m.recipeHandler.map(MachineRecipeHandler::getPower).orElse(0L), i -> this.euT = i);
        }

        public static WidgetSupplier build() {
            return builder((a,b) -> new MultiRenderWidget(a,b, (IInfoRenderer) a.handler));
        }
    }

    public static class TesseractGTWidget extends InfoRenderWidget<TesseractGTWidget> {

        public long voltAverage = 0;
        public long ampAverage = 0;
        public int cableAverage = 0;
        public long loss = 0;

        protected TesseractGTWidget(GuiInstance gui, IGuiElement parent, IInfoRenderer<TesseractGTWidget> renderer) {
            super(gui, parent, renderer);
        }

        @Override
        public void init() {
            super.init();
            TileEntityPipe<?> pipe = (TileEntityPipe<?>) gui.handler;
            final long pos = pipe.getPos().toLong();
            gui.syncLong(() -> {
                ITickingController controller = Tesseract.GT_ENERGY.getController(pipe.getWorld(),pipe.getPos().toLong());
                if (controller == null) return 0L;
                GTController gt = (GTController) controller;
                return gt.getTotalVoltage();
            }, a -> this.voltAverage = a);
            gui.syncLong(() -> {
                ITickingController controller = Tesseract.GT_ENERGY.getController(pipe.getWorld(),pipe.getPos().toLong());
                if (controller == null) return 0L;
                GTController gt = (GTController) controller;
                return gt.totalAmps();
            }, a -> this.ampAverage = a);
            gui.syncInt(() -> {
                ITickingController controller = Tesseract.GT_ENERGY.getController(pipe.getWorld(),pipe.getPos().toLong());
                if (controller == null) return 0;
                GTController gt = (GTController) controller;
                return gt.cableFrameAverage(pos);
            }, a -> this.cableAverage = a);
            gui.syncLong(() -> {
                ITickingController controller = Tesseract.GT_ENERGY.getController(pipe.getWorld(),pipe.getPos().toLong());
                if (controller == null) return 0L;
                GTController gt = (GTController) controller;
                return gt.totalLoss();
            }, a -> this.loss = a);
        }

        public static WidgetSupplier build() {
            return builder((a,b) -> new TesseractGTWidget(a,b, (IInfoRenderer<TesseractGTWidget>) a.handler));
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
            final long pos = pipe.getPos().toLong();
            gui.syncInt(() -> {
                ITickingController controller = Tesseract.ITEM.getController(pipe.getWorld(),pipe.getPos().toLong());
                if (controller == null) return 0;
                ItemController gt = (ItemController) controller;
                return gt.getTransferred();
            }, a -> this.transferred = a);
            gui.syncInt(() -> {
                ITickingController controller = Tesseract.ITEM.getController(pipe.getWorld(),pipe.getPos().toLong());
                if (controller == null) return 0;
                ItemController gt = (ItemController) controller;
                return gt.getCableTransferred(pipe.getPos().toLong());
            }, a -> this.cableTransferred = a);
        }

        public static WidgetSupplier build() {
            return builder((a,b) -> new TesseractItemWidget(a,b, (IInfoRenderer<TesseractItemWidget>) a.handler));
        }
    }
}
