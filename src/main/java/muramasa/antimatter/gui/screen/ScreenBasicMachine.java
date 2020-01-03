package muramasa.antimatter.gui.screen;

import muramasa.antimatter.gui.container.ContainerMachine;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class ScreenBasicMachine extends ScreenMachine {

    public ScreenBasicMachine(ContainerMachine container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        //Minecraft.getInstance().fontRenderer.drawString("This is a basic", getCenteredStringX("This is a basic"), 0, 0x404040);
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
        addButton(new Button(guiLeft, guiTop, 16, 16, "X", b -> System.out.println("Clicked X")));
    }
}
