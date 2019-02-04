package muramasa.gregtech.api.gui;

import muramasa.gregtech.api.gui.container.ContainerMachine;
import muramasa.gregtech.common.tileentities.base.TileEntityMachine;
import net.minecraft.util.ResourceLocation;

public class GuiMachine extends GuiBase {

    protected TileEntityMachine tile;
    protected ContainerMachine container;

    protected ResourceLocation background;
    protected String displayName;

    public GuiMachine(TileEntityMachine tile, ContainerMachine container) {
        super(container);
        this.tile = tile;
        this.container = container;
        background = tile.getMachineType().getGUITexture(tile.getTier());
        displayName = tile.getMachineType().getDisplayName(tile.getTier());
        xSize = 176;
        ySize = 166;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }
}
