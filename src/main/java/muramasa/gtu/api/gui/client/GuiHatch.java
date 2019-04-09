package muramasa.gtu.api.gui.client;

import muramasa.gtu.Ref;
import muramasa.gtu.api.gui.SlotData;
import muramasa.gtu.api.gui.SlotType;
import muramasa.gtu.api.gui.server.ContainerMachine;
import muramasa.gtu.api.machines.MachineFlag;
import muramasa.gtu.api.tileentities.multi.TileEntityHatch;
import net.minecraft.util.ResourceLocation;

public class GuiHatch extends GuiMachine {

    public GuiHatch(TileEntityHatch tile, ContainerMachine container) {
        super(tile, container);
        background = new ResourceLocation(Ref.MODID, "textures/gui/machine/hatch.png");
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        if (tile.getType().hasFlag(MachineFlag.FLUID)) {
            drawContainedFluids(mouseX, mouseY);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        for (SlotData slot : tile.getType().getGui().getSlots(tile.getTier())) {
            if (slot.type == SlotType.IT_IN || slot.type == SlotType.IT_OUT) {
                drawTexturedModalRect(guiLeft + slot.x - 1, guiTop + slot.y - 1, xSize, 0, 18, 18);
            } else if (slot.type == SlotType.FL_IN || slot.type == SlotType.FL_OUT) {
                drawTexturedModalRect(guiLeft + slot.x - 1, guiTop + slot.y - 1, xSize, 18, 18, 18);
            }
        }
        if (tile.getType().hasFlag(MachineFlag.FLUID)) {
            drawTexturedModalRect(guiLeft + 7, guiTop + 15, xSize, 36, 18, 54);
        }
    }
}
