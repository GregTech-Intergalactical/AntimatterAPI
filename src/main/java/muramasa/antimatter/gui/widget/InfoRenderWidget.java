package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.Widget;
import muramasa.antimatter.integration.jei.renderer.IInfoRenderer;
import net.minecraft.client.Minecraft;

public class InfoRenderWidget extends Widget {

    final IInfoRenderer renderer;

    protected InfoRenderWidget(GuiInstance gui, IInfoRenderer renderer) {
        super(gui);
        this.renderer = renderer;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderer.drawInfo(this.gui, matrixStack, Minecraft.getInstance().fontRenderer, realX(), realY());
    }

    public static WidgetSupplier build(IInfoRenderer renderer) {
        return builder(t -> new InfoRenderWidget(t, renderer));
    }

    public static WidgetSupplier build() {
        return builder(t -> new InfoRenderWidget(t, (IInfoRenderer) t.handler));
    }
}
