package muramasa.antimatter.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import muramasa.antimatter.gui.widget.WidgetSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

public abstract class Widget implements IGuiElement {
    public final GuiInstance gui;
    public final boolean isRemote;
    protected IGuiElement parent;
    private ITextComponent message = StringTextComponent.EMPTY;
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

    public void setMessage(ITextComponent message) {
        this.message = message;
    }

    public ITextComponent getMessage() {
        return message;
    }

    @OnlyIn(Dist.CLIENT)
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

    @OnlyIn(Dist.CLIENT)
    protected void renderTooltip(MatrixStack matrixStack, ITextComponent text, double mouseX, double mouseY) {
        net.minecraftforge.fml.client.gui.GuiUtils.drawHoveringText(matrixStack, Arrays.asList(text), (int) mouseX, (int) mouseY, parent.getW(), parent.getH(), -1, Minecraft.getInstance().font);
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


    @OnlyIn(Dist.CLIENT)
    public abstract void render(MatrixStack matrixStack, double mouseX, double mouseY, float partialTicks);

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isVisible() {
        return shouldRender;
    }


    @OnlyIn(Dist.CLIENT)
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isEnabled() && isInside(mouseX, mouseY)) {
            this.clickSound(Minecraft.getInstance().getSoundManager());
            this.onClick(mouseX, mouseY, button);
            return true;
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!isInside(mouseX, mouseY)) return false;
        isClicking = false;
        this.onRelease(mouseX, mouseY);
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public void mouseOver(MatrixStack stack, double mouseX, double mouseY, float partialTicks) {

    }

    @OnlyIn(Dist.CLIENT)
    public void clickSound(SoundHandler handler) {
        handler.play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @OnlyIn(Dist.CLIENT)
    public void update(double mouseX, double mouseY) {

    }

    @OnlyIn(Dist.CLIENT)
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

    @OnlyIn(Dist.CLIENT)
    protected void fillGradient(MatrixStack matrixStack, int x1, int y1, int x2, int y2, int colorFrom, int colorTo) {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        fillGradient(matrixStack.last().pose(), bufferbuilder, x1, y1, x2, y2, 0, colorFrom, colorTo);
        tessellator.end();
        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }

    @OnlyIn(Dist.CLIENT)
    protected void drawHoverText(List<? extends ITextProperties> textLines, int x, int y, FontRenderer font, MatrixStack matrixStack) {
        Minecraft minecraft = Minecraft.getInstance();
        GuiUtils.drawHoveringText(ItemStack.EMPTY, matrixStack, textLines, x, y, minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight(), -1, font);
    }

    @OnlyIn(Dist.CLIENT)
    public int drawText(MatrixStack matrixStack, ITextComponent text, float x, float y, int color) {
        return Minecraft.getInstance().font.draw(matrixStack, text, x, y, color);
    }

    @OnlyIn(Dist.CLIENT)
    protected void drawTexture(MatrixStack stack, ResourceLocation loc, int left, int top, int x, int y, int sizeX, int sizeY) {
        RenderSystem.color4f(1, 1, 1, 1);
        Minecraft.getInstance().textureManager.bind(loc);
        //AbstractGui.blit(stack, left, top, x, y, sizeX, sizeY);
        AbstractGui.blit(stack, left, top, 0, x, y, sizeX, sizeY, 256, 256);
    }

    public static WidgetSupplier builder(BiFunction<GuiInstance, IGuiElement, Widget> source) {
        return new WidgetSupplier(source);
    }
}
