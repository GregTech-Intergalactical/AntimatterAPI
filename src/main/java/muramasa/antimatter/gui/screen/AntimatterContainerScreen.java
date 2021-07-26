package muramasa.antimatter.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.gui.container.AntimatterContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AntimatterContainerScreen<T extends AntimatterContainer> extends ContainerScreen<T> implements IHasContainer<T> {

    protected ResourceLocation gui;
    public final List<Widget> widgetsFromData = new ObjectArrayList<>();

    public AntimatterContainerScreen(T container, PlayerInventory invPlayer, ITextComponent title) {
        super(container, invPlayer, title);
    }

    public void addWidget(Widget widget) {
        this.widgetsFromData.add(widget);
        super.children.add(widget);
    }

    public void addWidgets(Widget... widget) {
        for (Widget w : widget) {
            addWidget(w);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        for (Widget widget : widgetsFromData) {
            if (!widget.active) continue;
            if (widget.mouseClicked(mouseX - guiLeft, mouseY - guiTop, button)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (!super.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            for (Widget wid : widgetsFromData) {
                if (!wid.active) continue;
                if (wid.mouseDragged(mouseX, mouseY, button, dragX, dragY)) return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!super.mouseReleased(mouseX, mouseY, button)) {
            for (Widget wid : widgetsFromData) {
                if (!wid.active) continue;
                if (wid.mouseReleased(mouseX, mouseY, button)) return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!super.keyPressed(keyCode, scanCode, modifiers)) {
            for (Widget wid : widgetsFromData) {
                if (!wid.active) continue;
                if (wid.keyPressed(keyCode, scanCode, modifiers)) return true;
            }
        }
        return false;
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        for (Widget widget : widgetsFromData) {
            if (!widget.active) continue;
            widget.render(stack, mouseX, mouseY, partialTicks);
        }
        this.renderHoveredTooltip(stack, mouseX, mouseY);
    }

    @Override
    protected void renderHoveredTooltip(MatrixStack matrixStack, int x, int y) {
        super.renderHoveredTooltip(matrixStack, x, y);
        for (Widget widget : widgetsFromData) {
            if (!widget.active) continue;
            if (widget.isHovered() || widget.isMouseOver(x - guiLeft , y - guiTop)) {
                widget.renderToolTip(matrixStack, x, y);
            }
        }
    }

    public ResourceLocation sourceGui() {
        return gui;
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

    @Override
    protected void drawGuiContainerForegroundLayer(@Nonnull MatrixStack matrixStack, int x, int y) {
        for(Widget widget : this.widgetsFromData) {
            if (widget.isHovered()) {
                widget.renderToolTip(matrixStack, x - this.guiLeft, y - this.guiTop);
                break;
            }
        }
    }

    // Returns true if the given x,y coordinates are within the given rectangle
    public boolean isInRect(int x, int y, int xSize, int ySize, double mouseX, double mouseY) {
        return ((mouseX >= x && mouseX <= x+xSize) && (mouseY >= y && mouseY <= y+ySize));
    }

    public boolean isInGui(int x, int y, int xSize, int ySize, double mouseX, double mouseY) {
        return isInRect(x, y, xSize, ySize, mouseX - guiLeft, mouseY - guiTop);
    }

    protected void removeButton(Widget widget) {
        buttons.removeIf(f -> f.equals(widget));
        children.removeIf(f -> f.equals(widget));
    }
}
