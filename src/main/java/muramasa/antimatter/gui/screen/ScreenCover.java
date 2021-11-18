package muramasa.antimatter.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import muramasa.antimatter.cover.ICoverMode;
import muramasa.antimatter.cover.ICoverModeHandler;
import muramasa.antimatter.gui.container.ContainerCover;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

//A screen showing the GUI for the cover.
public class ScreenCover<T extends ContainerCover> extends AntimatterContainerScreen<T> implements IHasContainer<T> {

    protected ContainerCover container;
    protected String name;
    protected ResourceLocation gui;

    public ScreenCover(T container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
        this.container = container;
        this.gui = container.getCover().getGuiTexture();
    }

    @Override
    protected void renderLabels(MatrixStack stack, int mouseX, int mouseY) {
        drawTitle(stack, mouseX, mouseY);
        if (container.getCover() instanceof ICoverModeHandler) {
            ICoverModeHandler coverModeHandler = (ICoverModeHandler) container.getCover();
            ICoverMode mode = coverModeHandler.getCoverMode();
            Minecraft.getInstance().font.draw(stack, "Mode: " + mode.getName(), getCenteredStringX("Mode: " + mode.getName()), 13, 0x404040);
        }
    }

    protected void drawTitle(MatrixStack stack, int mouseX, int mouseY) {
        Minecraft.getInstance().font.draw(stack, name, getCenteredStringX(name), 4, 0x404040);
    }

    @Override
    protected void renderBg(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
        drawTexture(stack, gui, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        if (container.getCover() instanceof ICoverModeHandler) {
            ICoverModeHandler coverModeHandler = (ICoverModeHandler) container.getCover();
            ICoverMode mode = coverModeHandler.getCoverMode();
            drawTexture(stack, gui, leftPos + mode.getX(), topPos + mode.getY(), coverModeHandler.getOverlayX(), coverModeHandler.getOverlayY(), 18, 18);
        }
    }
}
