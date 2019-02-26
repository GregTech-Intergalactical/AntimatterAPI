package muramasa.gregtech.api.gui.client;

import muramasa.gregtech.api.gui.SlotData;
import muramasa.gregtech.api.gui.SlotType;
import muramasa.gregtech.api.gui.server.ContainerMachine;
import muramasa.gregtech.common.tileentities.base.TileEntityMachine;
import muramasa.gregtech.common.utils.Ref;
import net.minecraft.util.ResourceLocation;

public class GuiHatch extends GuiMachine {

    private int itemSlots = 0, fluidSlots = 0;

    public GuiHatch(TileEntityMachine tile, ContainerMachine container) {
        super(tile, container);
        background = new ResourceLocation(Ref.MODID, "textures/gui/machine/hatch.png");
        itemSlots = tile.getType().getGui().getItemInputs() > 0 ? tile.getType().getGui().getItemInputs() : tile.getType().getGui().getItemOutputs();
        fluidSlots = tile.getType().getGui().getFluidInputs() > 0 ? tile.getType().getGui().getFluidInputs() : tile.getType().getGui().getFluidOutputs();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        for (SlotData slot : tile.getType().getGui().getSlots()) {
            if (slot.type == SlotType.IT_IN || slot.type == SlotType.IT_OUT) {
                drawTexturedModalRect(guiLeft + slot.x - 1, guiTop + slot.y - 1, xSize, 0, 18, 18);
            } else if (slot.type == SlotType.FL_IN || slot.type == SlotType.FL_OUT) {
                drawTexturedModalRect(guiLeft + slot.x - 1, guiTop + slot.y - 1, xSize, 18, 18, 18);
            }
        }

//        drawTexturedModalRect(guiLeft + 79, guiTop + 34, xSize, 0, 18, 18);
    }
}
