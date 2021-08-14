package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.Widget;

public class BackgroundWidget extends Widget {

    private final int xSize, ySize;

    protected BackgroundWidget(GuiInstance instance, IGuiElement parent, int xSize, int ySize) {
        super(instance, parent);
        this.xSize = xSize;
        this.ySize = ySize;
        setDepth(-1);
    }

    @Override
    public void setParent(IGuiElement parent) {
        super.setParent(parent);
        this.setDepth(parent.depth()-1);
    }

    public static WidgetSupplier build(int w, int h) {
        return builder((a,b) -> new BackgroundWidget(a,b, w, h)).clientSide();
    }


    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        drawTexture(matrixStack, gui.handler.getGuiTexture(), realX(),realY(), 0,0, xSize, ySize);
    }
}
