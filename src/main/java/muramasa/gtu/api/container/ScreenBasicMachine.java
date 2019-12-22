package muramasa.gtu.api.container;

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
    }
}
