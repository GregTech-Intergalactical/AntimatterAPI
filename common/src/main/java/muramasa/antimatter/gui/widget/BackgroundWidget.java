package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.Widget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
;
;

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
    public void render(PoseStack matrixStack, double mouseX, double mouseY, float partialTicks) {
        drawTexture(matrixStack, loc, realX(), realY(), 0, 0, xSize, ySize);
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected void drawTexture(PoseStack stack, ResourceLocation loc, int left, int top, int x, int y, int sizeX, int sizeY) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, loc);
        //AbstractGui.blit(stack, left, top, x, y, sizeX, sizeY);
        GuiComponent.blit(stack, left, top, x, y, sizeX, sizeY, this.guiXSize, this.guiYSize);
    }
}
