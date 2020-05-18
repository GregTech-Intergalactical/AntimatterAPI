package muramasa.antimatter.gui.screen;

import muramasa.antimatter.gui.container.ContainerCover;
import muramasa.antimatter.gui.container.ContainerMachine;
import muramasa.antimatter.machine.Tier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ScreenCover extends AntimatterContainerScreen<ContainerCover> implements IHasContainer<ContainerCover> {

    protected ContainerCover container;
    protected String name;
    protected ResourceLocation gui;

    public ScreenCover(ContainerCover container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
        this.container = container;
        gui = container.getCover().getGui().getTexture(container.getCover().getTier());
    }

    protected void drawTitle(int mouseX, int mouseY) {
        Minecraft.getInstance().fontRenderer.drawString(name, getCenteredStringX(name), 4, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        drawTexture(gui, guiLeft, guiTop, 0, 0, xSize, ySize);
    }
}
