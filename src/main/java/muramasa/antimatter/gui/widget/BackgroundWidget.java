package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BackgroundWidget extends Widget {

    private final int xSize, ySize, guiXSize, guiYSize;
    private final ResourceLocation loc;

    protected BackgroundWidget(GuiInstance instance, IGuiElement parent, ResourceLocation loc, int xSize, int ySize, int guiXSize, int guiYSize) {
        super(instance, parent);
        this.xSize = xSize;
        this.ySize = ySize;
        this.guiXSize = guiXSize;
        this.guiYSize = guiYSize;
        this.loc = loc;
        setDepth(-1);
    }

    @Override
    public void setParent(IGuiElement parent) {
        super.setParent(parent);
        this.setDepth(parent.depth() - 1);
    }

    public static WidgetSupplier build(ResourceLocation loc, int w, int h) {
        return builder((a, b) -> new BackgroundWidget(a, b, loc, w, h, 256, 256)).clientSide();
    }

    public static WidgetSupplier build(ResourceLocation loc, int w, int h, int guiW, int guiH) {
        return builder((a, b) -> new BackgroundWidget(a, b, loc, w, h, guiW, guiH)).clientSide();
    }


    @Override
    public void render(MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks) {
        drawTexture(matrixStack, loc, realX(), realY(), 0, 0, xSize, ySize);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    protected void drawTexture(MatrixStack stack, ResourceLocation loc, int left, int top, int x, int y, int sizeX, int sizeY) {
        RenderSystem.color4f(1, 1, 1, 1);
        Minecraft.getInstance().textureManager.bind(loc);
        //AbstractGui.blit(stack, left, top, x, y, sizeX, sizeY);
        AbstractGui.blit(stack, left, top, x, y, sizeX, sizeY, this.guiXSize, this.guiYSize);
    }
}
