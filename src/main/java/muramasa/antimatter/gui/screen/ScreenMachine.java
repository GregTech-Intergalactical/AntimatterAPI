package muramasa.antimatter.gui.screen;

import muramasa.antimatter.Antimatter;
import muramasa.antimatter.gui.ButtonData;
import muramasa.antimatter.gui.container.ContainerMachine;
import muramasa.antimatter.gui.event.GuiEvent;
import muramasa.antimatter.gui.widget.ButtonWidget;
import muramasa.antimatter.gui.widget.SwitchWidjet;
import muramasa.antimatter.integration.jei.AntimatterJEIPlugin;
import muramasa.antimatter.machine.MachineFlag;
import muramasa.antimatter.network.packets.GuiEventPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.ModList;

public class ScreenMachine<T extends ContainerMachine> extends AntimatterContainerScreen<T> implements IHasContainer<T> {

    protected T container;
    protected String name;
    protected ResourceLocation gui;

    public ScreenMachine(T container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
        this.container = container;
        this.name = name.getString();
        gui = container.getTile().getMachineType().getGui().getTexture(container.getTile().getMachineTier(), "machine");
    }

    protected void drawTitle(int mouseX, int mouseY) {
        Minecraft.getInstance().fontRenderer.drawString(name, getCenteredStringX(name), 4, 0x404040);
    }

    @Override
    protected void init() {
        super.init();
        ResourceLocation loc = container.getTile().getMachineType().getGui().getButtonLocation();
        for (ButtonData button : container.getTile().getMachineType().getGui().getButtons()) {
            addButton(button.getType().getButtonSupplier().get(guiLeft, guiTop, container.getTile(), playerInventory, loc, button));
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        drawTitle(mouseX, mouseY);
        if (container.getTile().has(MachineFlag.RECIPE)) {
            drawTooltipInArea("Show Recipes", mouseX, mouseY, (xSize / 2) - 10, 24, 20, 14);
        }
        if (container.getTile().has(MachineFlag.FLUID)) {
            //TODO
            //drawContainedFluids(mouseX, mouseY);
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        container.getTile().drawInfo(Minecraft.getInstance().fontRenderer, guiLeft, guiTop);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawTexture(gui, guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    protected void drawProgress(float partialTicks, int mouseX, int mouseY) {
        int progressTime = (int)(20 * container.getTile().getClientProgress());
        drawTexture(gui, guiLeft + (xSize / 2) - 10, guiTop + 24, xSize, 0, progressTime, 18);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (!ModList.get().isLoaded("jei") || !container.getTile().has(MachineFlag.RECIPE)) return false;
        if (isInGui((xSize / 2) - 10, 24, 20, 18, mouseX, mouseY)) {
            AntimatterJEIPlugin.showCategory(container.getTile().getMachineType());
            return true;
        }
        return false;
    }
}
