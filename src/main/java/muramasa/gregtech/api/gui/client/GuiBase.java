package muramasa.gregtech.api.gui.client;

import muramasa.gregtech.api.gui.server.ContainerBase;
import net.minecraft.client.gui.inventory.GuiContainer;

import java.util.ArrayList;

public class GuiBase extends GuiContainer {

    public GuiBase(ContainerBase container) {
        super(container);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        //NOOP
    }

    public int getCenteredStringX(String s) {
        return xSize / 2 - fontRenderer.getStringWidth(s) / 2;
    }

    public void drawTooltipInArea(String line, int mouseX, int mouseY, int x, int y, int sizeX, int sizeY) {
        ArrayList<String> list = new ArrayList<>();
        list.add(line);
        drawTooltipInArea(list, mouseX, mouseY, x, y, sizeX, sizeY);
    }

    public void drawTooltipInArea(ArrayList<String> lines, int mouseX, int mouseY, int x, int y, int sizeX, int sizeY) {
        if (isInGui(x, y, sizeX, sizeY, mouseX, mouseY)) {
            drawHoveringText(lines, mouseX - guiLeft, mouseY - guiTop);
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
