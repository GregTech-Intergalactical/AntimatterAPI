package muramasa.antimatter.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
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
    }

    @Override
    protected void renderBg(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(stack, partialTicks, mouseX, mouseY);
        ResourceLocation gui = container.source().handler.getGuiTexture();
        List<SlotData<?>> list = container.getTile().getMachineType().getSlots(container.getTile().getMachineTier());
        for (SlotData<?> slot : list) {
            if (slot.getType() == SlotType.IT_IN || slot.getType() == SlotType.IT_OUT) {
                drawTexture(stack, gui, leftPos + slot.getX() - 1, topPos + slot.getY() - 1, imageWidth, 0, 18, 18);
            } else if (slot.getType() == SlotType.FL_IN) {
                drawTexture(stack, gui, leftPos + slot.getX() - 1, topPos + slot.getY() - 1, imageWidth, 90, 18, 18);
            } else if (slot.getType() == SlotType.FL_OUT) {
                drawTexture(stack, gui, leftPos + slot.getX() - 1, topPos + slot.getY() - 1, imageWidth, 108, 18, 18);
            }
        }
        if (container.getTile().getMachineType().has(MachineFlag.FLUID)) {
            drawTexture(stack, gui, leftPos + 8, topPos + 21, imageWidth, 36, 18, 54);
        }
    }
}
