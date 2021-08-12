package muramasa.antimatter.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.util.int2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.List;

public abstract class Widget implements IGuiElement{
    public final GuiInstance gui;
    public final boolean isRemote;
    protected IGuiElement parent;
    protected boolean enabled = true;
    protected boolean shouldRender = true;
    protected int x, y, w, h = 0;
    protected boolean isClicking = false;

    protected Widget(final GuiInstance gui) {
        this.gui = gui;
        this.isRemote = gui.isRemote;
    }

    public void init(final IGuiHandler source, final ICanSyncData data, final Container container) {

    }

    public void setParent(IGuiElement parent) {
        this.parent = parent;
    }

    public boolean isInside(double mouseX, double mouseY) {
        int realX = realX();
        int realY = realY();
        return ((mouseX >= realX && mouseX <= realX+getW()) && (mouseY >= realY && mouseY <= realY+getH()));
        //return x >= (double)realX && y >= (double)realY && x < (double)(realX + this.w) && y < (double)(realY + this.h);
    }

    public int realX() {
        return parent.getX() + this.x;
    }

    public int realY() {
        return parent.getY() + this.y;
    }

    public void onClick(double mouseX, double mouseY) {

    }

    public void onRelease(double mouseX, double mouseY) {
        isClicking = false;
    }

    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {

    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    public void renderToolTip(MatrixStack matrixStack, int mouseX, int mouseY) {
    }


    @OnlyIn(Dist.CLIENT)
    public final void renderGui(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (shouldRender) render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @OnlyIn(Dist.CLIENT)
    protected abstract void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks);

    public boolean isEnabled() {
        return enabled;
    }

    public boolean mouseClicked(double mouseX, double mouseY) {
        if (this.isEnabled() && isInside(mouseX, mouseY)) {
                this.clickSound(Minecraft.getInstance().getSoundHandler());
                this.onClick(mouseX, mouseY);
                return true;
            }
        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        isClicking = false;
        this.onRelease(mouseX, mouseY);
        return true;
    }

    public void clickSound(SoundHandler handler) {
        handler.play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        this.onDrag(mouseX, mouseY, dragX, dragY);
        return true;
    }


    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getW() {
        return w;
    }

    @Override
    public int getH() {
        return h;
    }

    @Override
    public IGuiElement parent() {
        return parent;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public void setW(int w) {
        this.w = w;
    }

    @Override
    public void setH(int h) {
        this.h = h;
    }
    @OnlyIn(Dist.CLIENT)
    protected void fillGradient(MatrixStack matrixStack, int x1, int y1, int x2, int y2, int colorFrom, int colorTo) {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        fillGradient(matrixStack.getLast().getMatrix(), bufferbuilder, x1, y1, x2, y2, 0, colorFrom, colorTo);
        tessellator.draw();
        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }
    @OnlyIn(Dist.CLIENT)
    protected static void fillGradient(Matrix4f matrix, BufferBuilder builder, int x1, int y1, int x2, int y2, int z, int colorA, int colorB) {
        float f = (float)(colorA >> 24 & 255) / 255.0F;
        float f1 = (float)(colorA >> 16 & 255) / 255.0F;
        float f2 = (float)(colorA >> 8 & 255) / 255.0F;
        float f3 = (float)(colorA & 255) / 255.0F;
        float f4 = (float)(colorB >> 24 & 255) / 255.0F;
        float f5 = (float)(colorB >> 16 & 255) / 255.0F;
        float f6 = (float)(colorB >> 8 & 255) / 255.0F;
        float f7 = (float)(colorB & 255) / 255.0F;
        builder.pos(matrix, (float)x2, (float)y1, (float)z).color(f1, f2, f3, f).endVertex();
        builder.pos(matrix, (float)x1, (float)y1, (float)z).color(f1, f2, f3, f).endVertex();
        builder.pos(matrix, (float)x1, (float)y2, (float)z).color(f5, f6, f7, f4).endVertex();
        builder.pos(matrix, (float)x2, (float)y2, (float)z).color(f5, f6, f7, f4).endVertex();
    }

    @OnlyIn(Dist.CLIENT)
    protected void drawText(List<? extends ITextProperties> textLines, int x, int y, FontRenderer font, MatrixStack matrixStack) {
        Minecraft minecraft = Minecraft.getInstance();
        GuiUtils.drawHoveringText(ItemStack.EMPTY, matrixStack, textLines, x, y, minecraft.getMainWindow().getScaledWidth(), minecraft.getMainWindow().getScaledHeight(), -1, font);
    }
}
