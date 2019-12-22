package muramasa.gtu.api.gui.screen;

import muramasa.gtu.api.gui.container.ContainerMachine;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class ScreenMultiMachine extends ScreenMachine {

    public ScreenMultiMachine(ContainerMachine container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        //Minecraft.getInstance().fontRenderer.drawString("This is a multi", getCenteredStringX("This is a multi"), 0, 0x404040);
    }
}
