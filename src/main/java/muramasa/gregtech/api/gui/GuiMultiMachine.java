package muramasa.gregtech.api.gui;

import muramasa.gregtech.api.gui.container.ContainerMultiMachine;
import muramasa.gregtech.common.tileentities.base.multi.TileEntityMultiMachine;

public class GuiMultiMachine extends GuiBasicMachine {

    public GuiMultiMachine(TileEntityMultiMachine tile, ContainerMultiMachine container) {
        super(tile, container);
    }

//    @Override
//    public void initGui() {
//        super.initGui();
//    }

//    @Override
//    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
//        fontRenderer.drawString(displayName, xSize / 2 - fontRenderer.getStringWidth(displayName) / 2, 6, 0x404040);
//    }

//    @Override
//    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
//        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
//    }
}
