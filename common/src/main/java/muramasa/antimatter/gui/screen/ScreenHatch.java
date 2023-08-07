package muramasa.antimatter.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import muramasa.antimatter.gui.SlotData;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.gui.container.ContainerHatch;
import muramasa.antimatter.machine.MachineFlag;
import muramasa.antimatter.tile.multi.TileEntityHatch;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class ScreenHatch<T extends TileEntityHatch<T>, U extends ContainerHatch<T>> extends ScreenMachine<T, U> {

    public ScreenHatch(U container, Inventory inv, Component name) {
        super(container, inv, name);
    }

    @Override
    protected void renderBg(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(stack, partialTicks, mouseX, mouseY);
        ResourceLocation gui = container.source().handler.getGuiTexture();
        if (container.getTile().getMachineType().has(MachineFlag.FLUID)) {
            drawTexture(stack, gui, leftPos + 8, topPos + 21, imageWidth, 36, 18, 54);
        }
    }
}
