package muramasa.itech.api.gui;

import muramasa.itech.api.gui.container.ContainerMultiMachine;
import muramasa.itech.api.machines.Machine;
import muramasa.itech.api.machines.Tier;
import muramasa.itech.common.tileentities.multi.TileEntityMultiMachine;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;

public class GuiMultiMachine extends GuiContainer {

    private TileEntityMultiMachine tile;
    private ResourceLocation bg;
    private String displayName;

    public GuiMultiMachine(TileEntityMultiMachine tile, ContainerMultiMachine container) {
        super(container);
        this.tile = tile;
        xSize = 176;
        ySize = 166;
        Machine type = tile.getMachineType();
        displayName = type.getDisplayName(Tier.MULTI.getName());
        bg = type.getGuiTexture(Tier.MULTI.getName());
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(displayName, xSize / 2 - fontRenderer.getStringWidth(displayName) / 2, 6, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(bg);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }
}
