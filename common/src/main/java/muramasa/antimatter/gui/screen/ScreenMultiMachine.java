package muramasa.antimatter.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import muramasa.antimatter.gui.container.ContainerMultiMachine;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ScreenMultiMachine<T extends TileEntityBasicMultiMachine<T>, U extends ContainerMultiMachine<T>> extends ScreenMachine<T, U> {

    public ScreenMultiMachine(U container, Inventory inv, Component name) {
        super(container, inv, name);
    }

    @Override
    protected void renderBg(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(stack, partialTicks, mouseX, mouseY);
        // if (container.getTile().getMachineState().getOverlayId() == 2) {
        //     drawTexture(stack, gui, guiLeft + (xSize / 2) - 4, guiTop + 44, xSize, 54, 8, 9);
        //}
    }
}
