package muramasa.gtu.api.container;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.List;

public abstract class GTContainerScreen<T extends Container> extends ContainerScreen<T> {

    public GTContainerScreen(T container, PlayerInventory invPlayer, ITextComponent title) {
        super(container, invPlayer, title);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    public int getCenteredStringX(String s) {
        return xSize / 2 - Minecraft.getInstance().fontRenderer.getStringWidth(s) / 2;
    }

    public void drawTooltipInArea(String line, int mouseX, int mouseY, int x, int y, int sizeX, int sizeY) {
        ArrayList<String> list = new ArrayList<>();
        list.add(line);
        drawTooltipInArea(list, mouseX, mouseY, x, y, sizeX, sizeY);
    }

    public void drawTooltipInArea(List<String> lines, int mouseX, int mouseY, int x, int y, int sizeX, int sizeY) {
        if (isInGui(x, y, sizeX, sizeY, mouseX, mouseY)) {
            renderTooltip(lines, mouseX - guiLeft, mouseY - guiTop);
        }
    }

    // Returns true if the given x,y coordinates are within the given rectangle
    public boolean isInRect(int x, int y, int xSize, int ySize, int mouseX, int mouseY) {
        return ((mouseX >= x && mouseX <= x+xSize) && (mouseY >= y && mouseY <= y+ySize));
    }

    public boolean isInGui(int x, int y, int xSize, int ySize, int mouseX, int mouseY) {
        return isInRect(x, y, xSize, ySize, mouseX - guiLeft, mouseY - guiTop);
    }
}
