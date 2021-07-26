package muramasa.antimatter.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import muramasa.antimatter.cover.CoverTiered;
import muramasa.antimatter.cover.ICoverMode;
import muramasa.antimatter.cover.ICoverModeHandler;
import muramasa.antimatter.gui.ButtonData;
import muramasa.antimatter.gui.container.ContainerCover;
import muramasa.antimatter.machine.Tier;
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
        if (container.getCover().getCover() instanceof CoverTiered) {
            this.gui = container.getCover().getCover().getGui().getTexture((((CoverTiered)container.getCover().getCover()).getTier()),"cover");
        } else {
            this.gui = container.getCover().getCover().getGui().getTexture(Tier.LV,"cover");
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack stack, int mouseX, int mouseY) {
        drawTitle(stack, mouseX, mouseY);
        if (container.getCover().getCover() instanceof ICoverModeHandler){
            ICoverModeHandler coverModeHandler = (ICoverModeHandler) container.getCover().getCover();
            ICoverMode mode = coverModeHandler.getCoverMode(container.getCover());
            Minecraft.getInstance().fontRenderer.drawString(stack,"Mode: " + mode.getName(), getCenteredStringX("Mode: " + mode.getName()), 13, 0x404040);
        }
    }

    protected void drawTitle(MatrixStack stack, int mouseX, int mouseY) {
        Minecraft.getInstance().fontRenderer.drawString(stack, name, getCenteredStringX(name), 4, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
        drawTexture(stack, gui, guiLeft, guiTop, 0, 0, xSize, ySize);
        if (container.getCover().getCover() instanceof ICoverModeHandler){
            ICoverModeHandler coverModeHandler = (ICoverModeHandler) container.getCover().getCover();
            ICoverMode mode = coverModeHandler.getCoverMode(container.getCover());
            drawTexture(stack, gui, guiLeft + mode.getX(), guiTop + mode.getY(), coverModeHandler.getOverlayX(), coverModeHandler.getOverlayY(), 18, 18);
        }
    }
}
