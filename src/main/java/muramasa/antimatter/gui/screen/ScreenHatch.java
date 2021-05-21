package muramasa.antimatter.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import muramasa.antimatter.Ref;
import muramasa.antimatter.gui.SlotData;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.gui.container.ContainerHatch;
import muramasa.antimatter.machine.MachineFlag;
import muramasa.antimatter.tile.multi.TileEntityHatch;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public class ScreenHatch<T extends TileEntityHatch<T>, U extends ContainerHatch<T>> extends ScreenMachine<T, U> {

    public ScreenHatch(U container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
        gui = new ResourceLocation(container.getTile().getDomain(), "textures/gui/machine/hatch.png");
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(stack, partialTicks, mouseX, mouseY);
        List<SlotData> list = container.getTile().getMachineType().getGui().getSlots(container.getTile().getMachineTier());
        for (SlotData slot : list) {
            if (slot.getType() == SlotType.IT_IN || slot.getType() == SlotType.IT_OUT) {
                drawTexture(stack, gui, guiLeft + slot.getX()-1, guiTop + slot.getY()-1, xSize, 0, 18, 18);
            } else if (slot.getType() == SlotType.FL_IN) {
                drawTexture(stack, gui, guiLeft + slot.getX()-1, guiTop + slot.getY()-1, xSize, 90, 18, 18);
            } else if (slot.getType() == SlotType.FL_OUT) {
                drawTexture(stack, gui, guiLeft + slot.getX()-1, guiTop + slot.getY()-1, xSize, 108, 18, 18);
            }
        }
        if (container.getTile().getMachineType().has(MachineFlag.FLUID)) {
            drawTexture(stack, gui, guiLeft + 8, guiTop + 21, xSize, 36, 18, 54);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack stack, int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(stack, mouseX, mouseY);
    }
}
