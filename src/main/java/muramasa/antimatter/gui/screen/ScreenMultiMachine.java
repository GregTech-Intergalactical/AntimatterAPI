package muramasa.antimatter.gui.screen;

import muramasa.antimatter.gui.container.ContainerMachine;
import muramasa.antimatter.machine.MachineState;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class ScreenMultiMachine extends ScreenMachine {

    public ScreenMultiMachine(ContainerMachine container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
    }

    @Override
    protected void init() {
        super.init();
        addButton(new Button(guiLeft, guiTop, 16, 16, "X", b -> {
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
