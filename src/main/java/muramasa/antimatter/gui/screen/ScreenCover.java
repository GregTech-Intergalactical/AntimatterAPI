package muramasa.antimatter.gui.screen;

import muramasa.antimatter.gui.ButtonData;
import muramasa.antimatter.gui.TextData;
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
        this.gui = container.getCover().getCover().getGui().getTexture("cover");
    }

    protected void drawTitle(int mouseX, int mouseY) {
        Minecraft.getInstance().fontRenderer.drawString(name, 32, 4, 0x404040);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        drawTitle(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        drawTexture(gui, guiLeft, guiTop, 0, 0, xSize, ySize);
        drawTexture(container.getCover().getCover().getItem().getRegistryName(), guiLeft, guiTop, 0, 4, 32, 32);
    }

    @Override
    protected void init() {
        super.init();
        ResourceLocation loc = container.getCover().getCover().getGui().getButtonLocation();
        for (ButtonData button : container.getCover().getCover().getGui().getButtons()) {
            boolean state = container.getCover().getButtonsCache().get(button.getId());
            addButton(button.getType().getButtonSupplier().get(state, guiLeft, guiTop, container.getCover(), playerInventory, loc, button));
        }
        for (TextData text : container.getCover().getCover().getGui().getText()) {
            Minecraft.getInstance().fontRenderer.drawString(text.getText(), text.getX(), text.getY(), text.getColor());
        }
    }
}
