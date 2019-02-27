package muramasa.gregtech.api.gui.client;

import muramasa.gregtech.api.gui.server.ContainerMachine;
import muramasa.gregtech.api.machines.MachineFlag;
import muramasa.gregtech.common.tileentities.base.TileEntityMachine;
import muramasa.gregtech.integration.jei.GregTechJEIPlugin;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;

public class GuiMachine extends GuiBase {

    protected TileEntityMachine tile;
    protected ContainerMachine container;

    protected ResourceLocation background;
    protected String displayName;

    public GuiMachine(TileEntityMachine tile, ContainerMachine container) {
        super(container);
        this.tile = tile;
        this.container = container;
        background = tile.getType().getGui().getTexture(tile.getTier());
        displayName = tile.getType().getDisplayName(tile.getTier());
        xSize = 176;
        ySize = 166;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(displayName, getCenteredStringX(displayName), 4, 0x404040);
        if (tile.getType().hasFlag(MachineFlag.RECIPE)) {
            drawTooltipInArea("Show Recipes", mouseX, mouseY, (xSize / 2) - 10, 24, 20, 14);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!Loader.isModLoaded("jei") || !tile.getType().hasFlag(MachineFlag.RECIPE)) return;
        if (isInGui((xSize / 2) - 10, 24, 20, 18, mouseX, mouseY)) {
            GregTechJEIPlugin.showCategory(tile.getType());
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        super.renderHoveredToolTip(mouseX, mouseY);
    }
}
