package muramasa.gtu.api.gui.client;

import muramasa.gtu.api.gui.GuiEvent;
import muramasa.gtu.api.gui.server.ContainerMultiMachine;
import muramasa.gtu.api.machines.MachineState;
import muramasa.gtu.api.network.GregTechNetwork;
import muramasa.gtu.api.tileentities.multi.TileEntityMultiMachine;
import net.minecraft.client.gui.GuiButton;

public class GuiMultiMachine extends GuiBasicMachine {

    public GuiMultiMachine(TileEntityMultiMachine tile, ContainerMultiMachine container) {
        super(tile, container);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.add(new GuiButton(1, guiLeft, guiTop, 16, 16, "X"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 1 && tile.getMachineState() == MachineState.INVALID_STRUCTURE) {
            GregTechNetwork.sendGuiEvent(GuiEvent.MULTI_ACTIVATE, tile);
        }
    }

    //    @Override
//    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
//        fontRenderer.drawString(displayName, xSize / 2 - fontRenderer.getStringWidth(displayName) / 2, 6, 0x404040);
//    }

//    @Override
//    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
//        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
//    }
}
