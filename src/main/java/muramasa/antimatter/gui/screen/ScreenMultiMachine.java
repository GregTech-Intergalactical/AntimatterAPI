package muramasa.antimatter.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import muramasa.antimatter.gui.container.ContainerMultiMachine;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class ScreenMultiMachine<T extends TileEntityBasicMultiMachine<T>, U extends ContainerMultiMachine<T>> extends ScreenMachine<T, U> {

    public ScreenMultiMachine(U container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
    }

    @Override
    protected void renderBg(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(stack, partialTicks, mouseX, mouseY);
        // if (container.getTile().getMachineState().getOverlayId() == 2) {
        //     drawTexture(stack, gui, guiLeft + (xSize / 2) - 4, guiTop + 44, xSize, 54, 8, 9);
        //}
    }
}
