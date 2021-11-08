package muramasa.antimatter.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.Widget;
import muramasa.antimatter.gui.container.IAntimatterContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;

import java.util.List;
import java.util.stream.Collectors;

public class AntimatterContainerScreen<T extends Container & IAntimatterContainer> extends ContainerScreen<T> implements IHasContainer<T>, IGuiElement {

    public AntimatterContainerScreen(T container, PlayerInventory invPlayer, ITextComponent title) {
        super(container, invPlayer, title);
        container.source().initClient(this);
    }

    @Override
    protected void init() {
        super.init();
        container.source().rescale(this);
    }

    @Override
    public void tick() {
        super.tick();
        container.source().update();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Widget widget : container.source().getWidgets(mouseX, mouseY)) {
            if (!widget.isEnabled()) continue;
            if (widget.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public double mouseX() {
        return minecraft.mouseHelper.getMouseX() * (double) this.minecraft.getMainWindow().getScaledWidth() / (double) this.minecraft.getMainWindow().getWidth();
    }

    public double mouseY() {
        return minecraft.mouseHelper.getMouseY() * (double) this.minecraft.getMainWindow().getScaledHeight() / (double) this.minecraft.getMainWindow().getHeight();
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        for (Widget wid : container.source().getWidgets(mouseX, mouseY)) {
            if (!wid.isEnabled()) continue;
            if (wid.mouseDragged(mouseX, mouseY, button, dragX, dragY)) return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (Widget wid : container.source().getWidgets(mouseX, mouseY)) {
            if (!wid.isEnabled()) continue;
            if (wid.mouseReleased(mouseX, mouseY, button)) return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        double x = mouseX();
        double y = mouseY();
        for (Widget wid : container.source().getWidgets(x, y)) {
            if (!wid.isEnabled()) continue;
            if (wid.keyPressed(keyCode, scanCode, modifiers, x, y)) return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
        float ticks = Minecraft.getInstance().getRenderPartialTicks();
        RenderSystem.pushMatrix();
        RenderSystem.translatef((float) -this.guiLeft, (float) -this.guiTop, 0.0F);
        for (Widget widget : container.source().getReverseWidgets()) {
            if (!widget.isEnabled() || !widget.isVisible() || widget.depth() < this.depth()) continue;
            widget.render(matrixStack, x, y, ticks);
        }
        container.source().getTopLevelWidget(x, y).ifPresent(t -> t.mouseOver(matrixStack, x, y, ticks));
        RenderSystem.popMatrix();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        this.renderBackground(matrixStack);
        for (Widget widget : container.source().getReverseWidgets()) {
            if (!widget.isEnabled() || !widget.isVisible()) continue;
            if (widget.depth() >= this.depth()) return;
            widget.render(matrixStack, x, y, Minecraft.getInstance().getRenderPartialTicks());
        }
    }


    public void drawTexture(MatrixStack stack, ResourceLocation loc, int left, int top, int x, int y, int sizeX, int sizeY) {
        RenderSystem.color4f(1, 1, 1, 1);
        Minecraft.getInstance().textureManager.bindTexture(loc);
        blit(stack, left, top, x, y, sizeX, sizeY);
    }

    public int getCenteredStringX(String s) {
        return xSize / 2 - Minecraft.getInstance().fontRenderer.getStringWidth(s) / 2;
    }

    public void drawTooltipInArea(MatrixStack stack, String line, int mouseX, int mouseY, int x, int y, int sizeX, int sizeY) {
        List<String> list = new ObjectArrayList<>();
        list.add(line);
        drawTooltipInArea(stack, list, mouseX, mouseY, x, y, sizeX, sizeY);
    }

    public void drawTooltipInArea(MatrixStack stack, List<String> lines, int mouseX, int mouseY, int x, int y, int sizeX, int sizeY) {
        if (isInGui(x, y, sizeX, sizeY, mouseX, mouseY)) {
            renderTooltip(stack, lines.stream().map(t -> IReorderingProcessor.fromString(t, Style.EMPTY)).collect(Collectors.toList()), mouseX - guiLeft, mouseY - guiTop);
        }
    }

    // Returns true if the given x,y coordinates are within the given rectangle
    public boolean isInRect(int x, int y, int xSize, int ySize, double mouseX, double mouseY) {
        return ((mouseX >= x && mouseX <= x + xSize) && (mouseY >= y && mouseY <= y + ySize));
    }

    public boolean isInGui(int x, int y, int xSize, int ySize, double mouseX, double mouseY) {
        return isInRect(x, y, xSize, ySize, mouseX - guiLeft, mouseY - guiTop);
    }

    protected void removeButton(Widget widget) {
        buttons.removeIf(f -> f.equals(widget));
        children.removeIf(f -> f.equals(widget));
    }

    @Override
    public int getX() {
        return guiLeft;
    }

    @Override
    public int getY() {
        return guiTop;
    }

    @Override
    public int getW() {
        return this.width;
    }

    @Override
    public int getH() {
        return this.height;
    }

    @Override
    public IGuiElement parent() {
        return null;
    }

    @Override
    public void setX(int x) {
        throw new IllegalStateException("Cannot set X on root gui");
    }

    @Override
    public void setY(int y) {
        throw new IllegalStateException("Cannot set X on root gui");
    }

    @Override
    public void setW(int w) {
        throw new IllegalStateException("Cannot set X on root gui");
    }

    @Override
    public void setH(int h) {
        throw new IllegalStateException("Cannot set X on root gui");
    }

    @Override
    public int realX() {
        return getX();
    }

    @Override
    public int realY() {
        return getY();
    }

    @Override
    public int depth() {
        return 0;
    }
}
