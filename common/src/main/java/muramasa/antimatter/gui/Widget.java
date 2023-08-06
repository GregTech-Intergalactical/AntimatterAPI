package muramasa.antimatter.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import muramasa.antimatter.gui.widget.WidgetSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

public abstract class Widget implements IGuiElement {
    public final GuiInstance gui;
    public final boolean isRemote;
    protected IGuiElement parent;
    private Component message = TextComponent.EMPTY;
    public final int id;

    protected boolean enabled = true;
    protected boolean shouldRender = true;
    protected boolean isClicking = false;

    private int depth;
    private int x, y, w, h = 0;
    private int realX, realY;

    protected Widget(@Nonnull final GuiInstance gui, @Nullable final IGuiElement parent) {
        this.gui = gui;
        this.isRemote = gui.isRemote;
        this.parent = parent;
        this.id = 0;
        updateSize();
    }

    public void setMessage(Component message) {
        this.message = message;
    }

    public Component getMessage() {
        return message;
    }

    @Environment(EnvType.CLIENT)
    protected static void fillGradient(Matrix4f matrix, BufferBuilder builder, int x1, int y1, int x2, int y2, int z, int colorA, int colorB) {
        float f = (float) (colorA >> 24 & 255) / 255.0F;
        float f1 = (float) (colorA >> 16 & 255) / 255.0F;
        float f2 = (float) (colorA >> 8 & 255) / 255.0F;
        float f3 = (float) (colorA & 255) / 255.0F;
        float f4 = (float) (colorB >> 24 & 255) / 255.0F;
        float f5 = (float) (colorB >> 16 & 255) / 255.0F;
        float f6 = (float) (colorB >> 8 & 255) / 255.0F;
        float f7 = (float) (colorB & 255) / 255.0F;
        builder.vertex(matrix, (float) x2, (float) y1, (float) z).color(f1, f2, f3, f).endVertex();
        builder.vertex(matrix, (float) x1, (float) y1, (float) z).color(f1, f2, f3, f).endVertex();
        builder.vertex(matrix, (float) x1, (float) y2, (float) z).color(f5, f6, f7, f4).endVertex();
        builder.vertex(matrix, (float) x2, (float) y2, (float) z).color(f5, f6, f7, f4).endVertex();
    }

    public void init() {

    }

    @Override
    public int depth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void setEnabled(boolean enabled) {
        if (enabled != this.enabled) {
            this.enabled = enabled;
            gui.updateWidgetStatus(this);
        }
    }

    public void setVisible(boolean visible) {
        this.shouldRender = visible;
    }

    public void setParent(IGuiElement parent) {
        this.parent = parent;
        updateSize();
    }

    public void updateSize() {
        int oldX = realX;
        int oldY = realY;
        realX = parent != null ? parent.realX() + this.x : this.x;
        realY = parent != null ? parent.realY() + this.y : this.y;
        gui.rescaleWidget(this, oldX, oldY, getW(), getH());
        if (parent != null) parent.onChildSizeChange(this);
    }


    public boolean isInside(double mouseX, double mouseY) {
        int realX = realX();
        int realY = realY();
        return ((mouseX >= realX && mouseX <= realX + getW()) && (mouseY >= realY && mouseY <= realY + getH()));
    }

    public boolean isInside(int x, int y, int w, int h, double mouseX, double mouseY) {
        int realX = realX() + x;
        int realY = realY() + y;
        return ((mouseX >= realX && mouseX <= realX + w) && (mouseY >= realY && mouseY <= realY + h));
    }

    @Environment(EnvType.CLIENT)
    protected void renderTooltip(PoseStack matrixStack, Component text, double mouseX, double mouseY) {
        this.gui.screen.renderComponentTooltip(matrixStack, Collections.singletonList(text), (int)mouseX,(int) mouseY);
    }

    @Override
    public int realX() {
        return realX;
    }

    @Override
    public int realY() {
        return realY;
    }

    public void onClick(double mouseX, double mouseY, int button) {

    }

    public void onRelease(double mouseX, double mouseY) {
        isClicking = false;
    }

    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {

    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers, double mouseX, double mouseY) {
        return false;
    }

    public boolean keyReleased(int keyCode, int scanCode, int modifiers, double mouseX, double mouseY) {
        return false;
    }


    @Environment(EnvType.CLIENT)
    public abstract void render(PoseStack matrixStack, double mouseX, double mouseY, float partialTicks);

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isVisible() {
        return shouldRender;
    }


    @Environment(EnvType.CLIENT)
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isEnabled() && isInside(mouseX, mouseY)) {
            this.clickSound(Minecraft.getInstance().getSoundManager());
            this.onClick(mouseX, mouseY, button);
            return true;
        }
        return false;
    }

    @Environment(EnvType.CLIENT)
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!isInside(mouseX, mouseY)) return false;
        isClicking = false;
        this.onRelease(mouseX, mouseY);
        return true;
    }

    @Environment(EnvType.CLIENT)
    public void mouseOver(PoseStack stack, double mouseX, double mouseY, float partialTicks) {

    }

    @Environment(EnvType.CLIENT)
    public void clickSound(SoundManager handler) {
        handler.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Environment(EnvType.CLIENT)
    public void update(double mouseX, double mouseY) {

    }

    @Environment(EnvType.CLIENT)
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        this.onDrag(mouseX, mouseY, dragX, dragY);
        return true;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public void setX(int x) {
        this.x = x;
        updateSize();
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void setY(int y) {
        this.y = y;
        updateSize();
    }

    @Override
    public int getW() {
        return w;
    }

    @Override
    public void setW(int w) {
        this.w = w;
    }

    @Override
    public int getH() {
        return h;
    }

    @Override
    public void setH(int h) {
        this.h = h;
    }

    @Override
    public IGuiElement parent() {
        return parent;
    }

    @Environment(EnvType.CLIENT)
    protected void fillGradient(PoseStack matrixStack, int x1, int y1, int x2, int y2, int colorFrom, int colorTo) {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        fillGradient(matrixStack.last().pose(), bufferbuilder, x1, y1, x2,y2, 0, colorFrom, colorTo);
        tesselator.end();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }


    @Environment(EnvType.CLIENT)
    protected void drawHoverText(List<Component> textLines, int x, int y, Font font, PoseStack matrixStack) {
        this.gui.screen.renderComponentTooltip(matrixStack, textLines, x, y);
    }

    @Environment(EnvType.CLIENT)
    public int drawText(PoseStack matrixStack, Component text, float x, float y, int color) {
        return Minecraft.getInstance().font.draw(matrixStack, text, x, y, color);
    }

    @Environment(EnvType.CLIENT)
    protected void drawTexture(PoseStack stack, ResourceLocation loc, int left, int top, int x, int y, int sizeX, int sizeY) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, loc);
        //AbstractGui.blit(stack, left, top, x, y, sizeX, sizeY);
        GuiComponent.blit(stack, left, top, 0, x, y, sizeX, sizeY, 256, 256);
    }

    @Environment(EnvType.CLIENT)
    protected void drawTexture(PoseStack stack, ResourceLocation loc, int left, int top, int x, int y, int sizeX, int sizeY, int textureHeight, int textureWidth) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, loc);
        //AbstractGui.blit(stack, left, top, x, y, sizeX, sizeY);
        GuiComponent.blit(stack, left, top, 0, x, y, sizeX, sizeY,  textureHeight, textureWidth);
    }

    public static WidgetSupplier builder(BiFunction<GuiInstance, IGuiElement, Widget> source) {
        return new WidgetSupplier(source);
    }
}
