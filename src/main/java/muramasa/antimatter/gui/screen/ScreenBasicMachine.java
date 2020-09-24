package muramasa.antimatter.gui.screen;

import muramasa.antimatter.Antimatter;
import muramasa.antimatter.capability.machine.MachineFluidHandler;
import muramasa.antimatter.capability.machine.MachineItemHandler;
import muramasa.antimatter.gui.ButtonOverlay;
import muramasa.antimatter.gui.container.ContainerMachine;
import muramasa.antimatter.gui.event.GuiEvent;
import muramasa.antimatter.gui.widget.SwitchWidjet;
import muramasa.antimatter.machine.MachineFlag;
import muramasa.antimatter.network.packets.GuiEventPacket;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import static muramasa.antimatter.gui.ButtonBody.OFF;
import static muramasa.antimatter.gui.ButtonBody.ON;
import static muramasa.antimatter.gui.event.GuiEvent.*;

public class ScreenBasicMachine<T extends ContainerMachine> extends ScreenMachine<T> {

    private final static ButtonOverlay FLUID = new ButtonOverlay("fluid_eject", 177, 19, 16, 16);
    private final static ButtonOverlay ITEM = new ButtonOverlay("item_eject", 177, 37, 16, 16);
    private Widget item, fluid;

    public ScreenBasicMachine(T container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        drawTooltipInArea(container.getTile().getMachineState().getDisplayName(), mouseX, mouseY, (xSize / 2) - 5, 45, 10, 8);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        drawProgress(partialTicks, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        if (container.getTile().has(MachineFlag.ITEM)) {
            item = new SwitchWidjet(container.getTile().itemHandler.map(MachineItemHandler::isEjecting).orElse(false), gui, guiLeft + 35, guiTop + 63, 16, 16, ITEM, getSwitchable(ITEM_EJECT));
        }
        if (container.getTile().has(MachineFlag.FLUID)) {
            fluid = new SwitchWidjet(container.getTile().fluidHandler.map(MachineFluidHandler::isEjecting).orElse(false), gui, guiLeft + 53, guiTop + 63, 16, 16, FLUID, getSwitchable(FLUID_EJECT));
        }
        if (item != null || fluid != null) {
            addButton(new SwitchWidjet(false, data.getButtonLocation(), guiLeft + 9, guiTop + 64, 14, 14, ON, OFF, (b, s) -> {
                if (s) {
                    if (item != null) addButton(item);
                    if (fluid != null) addButton(fluid);
                } else {
                    if (item != null) removeButton(item);
                    if (fluid != null) removeButton(fluid);
                }
            }));
        }
    }
}
