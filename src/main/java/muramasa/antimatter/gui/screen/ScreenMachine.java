package muramasa.antimatter.gui.screen;

import it.unimi.dsi.fastutil.ints.Int2BooleanMap;
import it.unimi.dsi.fastutil.ints.Int2BooleanMaps;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.capability.machine.MachineInteractHandler;
import muramasa.antimatter.gui.ButtonData;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.gui.TextData;
import muramasa.antimatter.gui.container.ContainerMachine;
import muramasa.antimatter.gui.event.GuiEvent;
import muramasa.antimatter.gui.widget.ButtonWidget;
import muramasa.antimatter.gui.widget.SwitchWidjet;
import muramasa.antimatter.machine.MachineFlag;
import muramasa.antimatter.network.packets.GuiEventPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.ModList;

public class ScreenMachine<T extends ContainerMachine> extends AntimatterContainerScreen<T> implements IHasContainer<T> {

    protected T container;
    protected String name;
    protected GuiData data;
    protected ResourceLocation gui;

    public ScreenMachine(T container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
        this.container = container;
        this.name = name.getString();
        this.data = container.getTile().getMachineType().getGui();
        gui = data.getTexture(container.getTile().getMachineTier(), "machine");
    }

    protected void drawTitle(int mouseX, int mouseY) {
        Minecraft.getInstance().fontRenderer.drawString(name, getCenteredStringX(name), 4, 0x404040);
    }

    @Override
    protected void init() {
        super.init();
        Int2BooleanMap cache = container.getTile().interactHandler.map(MachineInteractHandler::getButtonCache).orElse(Int2BooleanMaps.EMPTY_MAP);
        for (ButtonData button : data.getButtons()) {
            addButton(button.getType().getButtonSupplier().get(cache.get(button.getId()), guiLeft, guiTop, container.getTile(), playerInventory, data.getButtonLocation(), button));
        }
        for (TextData text : data.getText()) {
            Minecraft.getInstance().fontRenderer.drawString(text.getText(), text.getX(), text.getY(), text.getColor());
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
            //TODO
            //GregTechJEIPlugin.showCategory(container.getTile().getMachineType());
            return true;
        }
        return false;
    }

    protected ButtonWidget.IPressable getPressable(GuiEvent event) {
        return (b) -> {
            container.getTile().onGuiEvent(event);
            Antimatter.NETWORK.sendToServer(new GuiEventPacket(event, container.getTile().getPos()));
        };
    }

    protected SwitchWidjet.ISwitchable getSwitchable(GuiEvent event) {
        return (b, s) -> {
            int switchOn = s ? 1 : 0;
            container.getTile().onGuiEvent(event, switchOn);
            Antimatter.NETWORK.sendToServer(new GuiEventPacket(event, container.getTile().getPos(), switchOn));
        };
    }
}
