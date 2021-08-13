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
    }

    @Override
    protected void init() {
        super.init();
        container.source().setScreen(this);
        container.source().setRootElement(this);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Widget widget : container.source().widgets()) {
            if (!widget.isEnabled()) continue;
            if (widget.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        for (Widget wid : container.source().widgets()) {
            if (!wid.isEnabled()) continue;
            if (wid.mouseDragged(mouseX, mouseY, button, dragX, dragY)) return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (Widget wid : container.source().widgets()) {
            if (!wid.isEnabled()) continue;
            if (wid.mouseReleased(mouseX, mouseY, button)) return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        int mouseX = (int) Minecraft.getInstance().mouseHelper.getMouseX();
        int mouseY = (int) Minecraft.getInstance().mouseHelper.getMouseX();
        for (Widget wid : container.source().widgets()) {
            if (!wid.isEnabled()) continue;
            if (wid.keyPressed(keyCode, scanCode, modifiers, mouseX, mouseY)) return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.disableBlend();
        this.renderBackground(stack);
        for (Widget widget : container.source().widgets()) {
            if (!widget.isEnabled() || !widget.isVisible()) continue;
            widget.renderBackground(stack, mouseX, mouseY, partialTicks);
        }
        super.render(stack, mouseX, mouseY, partialTicks);
        for (Widget widget : container.source().widgets()) {
            if (!widget.isEnabled() || !widget.isVisible()) continue;
            widget.render(stack, mouseX, mouseY, partialTicks);
        }
        this.renderHoveredTooltip(stack, mouseX, mouseY);
        RenderSystem.enableBlend();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {

    }


    public void drawTexture(MatrixStack stack, ResourceLocation loc, int left, int top, int x, int y, int sizeX, int sizeY) {
        RenderSystem.color4f(1, 1, 1, 1);
        Minecraft.getInstance().textureManager.bindTexture(loc);
        blit(stack, left, top, x, y, sizeX, sizeY);
    }

    public int getCenteredStringX(String s) {
        return xSize / 2 - Minecraft.getInstance().fontRenderer.getStringWidth(s) / 2;
    }

    public void drawTooltipInArea(MatrixStack stack,String line, int mouseX, int mouseY, int x, int y, int sizeX, int sizeY) {
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
        return ((mouseX >= x && mouseX <= x+xSize) && (mouseY >= y && mouseY <= y+ySize));
    }

    public boolean isInGui(int x, int y, int xSize, int ySize, double mouseX, double mouseY) {
        return isInRect(x, y, xSize, ySize, mouseX - guiLeft, mouseY - guiTop);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
        //this.font.drawText(matrixStack, this.title, (float)this.titleX, (float)this.titleY, 4210752);
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
