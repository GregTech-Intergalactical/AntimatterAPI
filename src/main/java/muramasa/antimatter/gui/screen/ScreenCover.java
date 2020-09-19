package muramasa.antimatter.gui.screen;

import muramasa.antimatter.gui.ButtonData;
import muramasa.antimatter.gui.container.ContainerCover;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

//A screen showing the GUI for the cover.
public class ScreenCover<T extends ContainerCover> extends AntimatterContainerScreen<T> implements IHasContainer<T> {

    protected ContainerCover container;
    protected String name;
    protected ResourceLocation gui;

    public ScreenCover(T container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
        this.container = container;
        this.gui = container.getInstance().getCover().getGui().getTexture("cover");
    }

    protected void drawTitle(int mouseX, int mouseY) {
        Minecraft.getInstance().fontRenderer.drawString(name, getCenteredStringX(name), 4, 0x404040);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        drawTitle(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        drawTexture(gui, guiLeft, guiTop, 0, 0, xSize, ySize);
        drawTexture(container.getInstance().getCover().getItem().getRegistryName(), guiLeft, guiTop, 0, 8, 32, 32);
    }

    @Override
    protected void init() {
        super.init();
        ResourceLocation loc = container.getInstance().getCover().getGui().getButtonLocation();
        for (ButtonData button : container.getInstance().getCover().getGui().getButtons()) {
            addButton(button.getType().getButtonSupplier().get(guiLeft, guiTop, container.getInstance().getTile(), playerInventory, loc, button));
        }
    }
}
