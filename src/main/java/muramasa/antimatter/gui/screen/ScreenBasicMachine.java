package muramasa.antimatter.gui.screen;

import muramasa.antimatter.gui.container.ContainerMachine;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class ScreenBasicMachine<T extends ContainerMachine> extends ScreenMachine<T> {

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
}
