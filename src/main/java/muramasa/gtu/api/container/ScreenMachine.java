package muramasa.gtu.api.container;

import com.mojang.blaze3d.platform.GlStateManager;
import muramasa.gtu.api.machines.MachineFlag;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ScreenMachine extends GTContainerScreen<ContainerMachine> {

    protected ContainerMachine container;
    protected String name;
    protected ResourceLocation gui;

    public ScreenMachine(ContainerMachine container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
        this.container = container;
        this.name = name.getString();
        gui = container.tile.getMachineType().getGui().getTexture(container.tile.getTier());
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        Minecraft.getInstance().fontRenderer.drawString(name, getCenteredStringX(name), 4, 0x404040);
        if (container.tile.getMachineType().hasFlag(MachineFlag.RECIPE)) {
            drawTooltipInArea("Show Recipes", mouseX, mouseY, (xSize / 2) - 10, 24, 20, 14);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1, 1, 1, 1);
        Minecraft.getInstance().textureManager.bindTexture(gui);
        blit(guiLeft, guiTop, 0, 0, xSize, ySize);
    }
}
