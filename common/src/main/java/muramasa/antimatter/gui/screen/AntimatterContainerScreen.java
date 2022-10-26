package muramasa.antimatter.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.Widget;
import muramasa.antimatter.gui.container.IAntimatterContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.List;
import java.util.stream.Collectors;

public class AntimatterContainerScreen<T extends AbstractContainerMenu & IAntimatterContainer> extends AbstractContainerScreen<T> implements MenuAccess<T>, IGuiElement {

    public AntimatterContainerScreen(T container, Inventory invPlayer, Component title) {
        super(container, invPlayer, title);
        container.source().initClient(this);
    }

    @Override
    protected void init() {
        super.init();
        menu.source().rescale(this);
    }

    @Override
    public void containerTick() {
        super.containerTick();
        menu.source().update(this.mouseX(), this.mouseY());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Widget widget : menu.source().getWidgets(mouseX, mouseY)) {
            if (!widget.isEnabled()) continue;
            if (widget.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public double mouseX() {
        return minecraft.mouseHandler.xpos() * (double) this.minecraft.getWindow().getGuiScaledWidth() / (double) this.minecraft.getWindow().getScreenWidth();
    }

    public double mouseY() {
        return minecraft.mouseHandler.ypos() * (double) this.minecraft.getWindow().getGuiScaledHeight() / (double) this.minecraft.getWindow().getScreenHeight();
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        for (Widget wid : menu.source().getWidgets(mouseX, mouseY)) {
            if (!wid.isEnabled()) continue;
            if (wid.mouseDragged(mouseX, mouseY, button, dragX, dragY)) return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (Widget wid : menu.source().getWidgets(mouseX, mouseY)) {
            if (!wid.isEnabled()) continue;
            if (wid.mouseReleased(mouseX, mouseY, button)) return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        double x = mouseX();
        double y = mouseY();
        for (Widget wid : menu.source().getWidgets(x, y)) {
            if (!wid.isEnabled()) continue;
            if (wid.keyPressed(keyCode, scanCode, modifiers, x, y)) return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int x, int y) {
        float ticks = Minecraft.getInstance().getFrameTime();
        matrixStack.pushPose();
        matrixStack.translate((float) -this.leftPos, (float) -this.topPos, 0.0F);
        for (Widget widget : menu.source().widgetsToRender()) {
            if (!widget.isEnabled() || !widget.isVisible() || widget.depth() < this.depth()) continue;
            widget.render(matrixStack, x, y, ticks);
        }
        menu.source().getTopLevelWidget(x, y).ifPresent(t -> t.mouseOver(matrixStack, x, y, ticks));
        matrixStack.popPose();
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
        this.renderBackground(matrixStack);
        for (Widget widget : menu.source().widgetsToRender()) {
            if (!widget.isEnabled() || !widget.isVisible()) continue;
            if (widget.depth() >= this.depth()) return;
            widget.render(matrixStack, x, y, Minecraft.getInstance().getFrameTime());
        }
    }


    public void drawTexture(PoseStack stack, ResourceLocation loc, int left, int top, int x, int y, int sizeX, int sizeY) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        Minecraft.getInstance().getTextureManager().bindForSetup(loc);
        blit(stack, left, top, x, y, sizeX, sizeY);
    }

    public int getCenteredStringX(String s) {
        return imageWidth / 2 - Minecraft.getInstance().font.width(s) / 2;
    }

    public void drawTooltipInArea(PoseStack stack, String line, int mouseX, int mouseY, int x, int y, int sizeX, int sizeY) {
        List<String> list = new ObjectArrayList<>();
        list.add(line);
        drawTooltipInArea(stack, list, mouseX, mouseY, x, y, sizeX, sizeY);
    }

    public void drawTooltipInArea(PoseStack stack, List<String> lines, int mouseX, int mouseY, int x, int y, int sizeX, int sizeY) {
        if (isInGui(x, y, sizeX, sizeY, mouseX, mouseY)) {
            renderTooltip(stack, lines.stream().map(t -> FormattedCharSequence.forward(t, Style.EMPTY)).collect(Collectors.toList()), mouseX - leftPos, mouseY - topPos);
        }
    }

    // Returns true if the given x,y coordinates are within the given rectangle
    public boolean isInRect(int x, int y, int xSize, int ySize, double mouseX, double mouseY) {
        return ((mouseX >= x && mouseX <= x + xSize) && (mouseY >= y && mouseY <= y + ySize));
    }

    public boolean isInGui(int x, int y, int xSize, int ySize, double mouseX, double mouseY) {
        return isInRect(x, y, xSize, ySize, mouseX - leftPos, mouseY - topPos);
    }

    @Override
    public int getX() {
        return leftPos;
    }

    @Override
    public int getY() {
        return topPos;
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
