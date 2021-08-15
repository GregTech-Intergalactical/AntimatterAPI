package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import muramasa.antimatter.capability.machine.MachineRecipeHandler;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.Widget;
import muramasa.antimatter.integration.jei.renderer.IInfoRenderer;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;
import net.minecraft.client.Minecraft;

public class InfoRenderWidget<T extends InfoRenderWidget<T>> extends Widget {

    final IInfoRenderer<T> renderer;

    protected InfoRenderWidget(GuiInstance gui, IGuiElement parent, IInfoRenderer<T> renderer) {
        super(gui, parent);
        this.renderer = renderer;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
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
            gui.syncInt(() -> m.recipeHandler.map(t -> t.getActiveRecipe() == null ? 0 : t.getActiveRecipe().getDuration()).orElse(0), i -> this.maxProgress = i);
            gui.syncInt(() -> m.recipeHandler.map(MachineRecipeHandler::getOverclock).orElse(0), i -> this.overclock = i);
            gui.syncLong(() -> m.recipeHandler.map(MachineRecipeHandler::getPower).orElse(0L), i -> this.euT = i);
        }

        public static WidgetSupplier build() {
            return builder((a,b) -> new MultiRenderWidget(a,b, (IInfoRenderer) a.handler));
        }
    }
}
