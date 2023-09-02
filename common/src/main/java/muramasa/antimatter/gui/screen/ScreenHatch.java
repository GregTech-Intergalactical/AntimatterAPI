package muramasa.antimatter.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import muramasa.antimatter.tile.multi.TileEntityHatch;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ScreenHatch<T extends TileEntityHatch<T>, U extends muramasa.antimatter.gui.container.ContainerMachine<T>> extends ScreenMachine<T, U> {

    public ScreenHatch(U container, Inventory inv, Component name) {
        super(container, inv, name);
    }

    @Override
    protected void renderBg(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(stack, partialTicks, mouseX, mouseY);
    }
}
