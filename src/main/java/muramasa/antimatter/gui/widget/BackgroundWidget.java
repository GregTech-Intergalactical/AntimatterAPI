package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.Widget;
import net.minecraft.util.ResourceLocation;

public class BackgroundWidget extends Widget {

    private final int xSize, ySize;
    private final ResourceLocation loc;

    protected BackgroundWidget(GuiInstance instance, IGuiElement parent, ResourceLocation loc, int xSize, int ySize) {
        super(instance, parent);
        this.xSize = xSize;
        this.ySize = ySize;
        this.loc = loc;
        setDepth(-1);
    }

    @Override
    public void setParent(IGuiElement parent) {
        super.setParent(parent);
        this.setDepth(parent.depth()-1);
    }

    public static WidgetSupplier build(ResourceLocation loc, int w, int h) {
        return builder((a,b) -> new BackgroundWidget(a,b, loc, w, h)).clientSide();
    }


    @Override
    public void render(MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks) {
        drawTexture(matrixStack, loc, realX(),realY(), 0,0, xSize, ySize);
    }
}
