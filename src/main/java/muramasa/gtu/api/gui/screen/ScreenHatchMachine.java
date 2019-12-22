package muramasa.gtu.api.gui.screen;

import muramasa.gtu.Ref;
import muramasa.gtu.api.gui.SlotData;
import muramasa.gtu.api.gui.SlotType;
import muramasa.gtu.api.gui.container.ContainerMachine;
import muramasa.gtu.api.machines.MachineFlag;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ScreenHatchMachine extends ScreenMachine {

    public ScreenHatchMachine(ContainerMachine container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
        gui = new ResourceLocation(Ref.MODID, "textures/gui/machine/hatch.png");
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        for (SlotData slot : container.getTile().getMachineType().getGui().getSlots(container.getTile().getTier())) {
            if (slot.type == SlotType.IT_IN || slot.type == SlotType.IT_OUT) {
                drawTexture(gui, guiLeft + slot.x - 1, guiTop + slot.y - 1, xSize, 0, 18, 18);
            } else if (slot.type == SlotType.FL_IN || slot.type == SlotType.FL_OUT) {
                drawTexture(gui, guiLeft + slot.x - 1, guiTop + slot.y - 1, xSize, 18, 18, 18);
            }
        }
        if (container.getTile().getMachineType().hasFlag(MachineFlag.FLUID)) {
            drawTexture(gui, guiLeft + 7, guiTop + 15, xSize, 36, 18, 54);
        }
    }
}
