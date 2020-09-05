package muramasa.antimatter.gui.screen;

import muramasa.antimatter.gui.container.ContainerMultiMachine;
import muramasa.antimatter.gui.widget.ButtonWidget;
import muramasa.antimatter.machine.MachineState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

import static muramasa.antimatter.gui.ButtonBody.BLUE;

public class ScreenMultiMachine<T extends ContainerMultiMachine> extends ScreenMachine<T> {

    public ScreenMultiMachine(T container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
    }

    @Override
    protected void init() {
        super.init();
        addButton(new ButtonWidget(container.getTile().getMachineType().getGui().getButtonLocation(), guiLeft, guiTop, 8, 8, BLUE, "x", b -> {
            if (container.getTile().getMachineState() == MachineState.INVALID_STRUCTURE) {
                //TODO
                //GregTechNetwork.sendGuiEvent(GuiEvent.MULTI_ACTIVATE, tile);
            }
        }));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        if (container.getTile().getMachineState().getOverlayId() == 2) {
            drawTexture(gui, guiLeft + (xSize / 2) - 4, guiTop + 44, xSize, 54, 8, 9);
        }
    }
}
