package muramasa.gregtech.api.gui;

import muramasa.gregtech.api.capability.impl.MachineFluidHandler;
import muramasa.gregtech.api.gui.container.ContainerMachine;
import muramasa.gregtech.api.machines.MachineFlag;
import muramasa.gregtech.client.render.RenderHelper;
import muramasa.gregtech.common.tileentities.base.TileEntityMachine;
import muramasa.gregtech.common.tileentities.overrides.TileEntityBasicMachine;
import muramasa.gregtech.integration.jei.GregTechJEIPlugin;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;

import java.io.IOException;
import java.util.ArrayList;

public class GuiBasicMachine extends GuiMachine {

    public GuiBasicMachine(TileEntityMachine tile, ContainerMachine container) {
        super(tile, container);
    }

    @Override
    public void initGui() {
        super.initGui();
//        this.buttonList.add(new GuiButton(1, guiLeft, guiTop, 16, 16, "X"));
    }

    public void drawContainedFluids(int mouseX, int mouseY) { // mezz.jei.plugins.vanilla.ingredients.FluidStackRenderer
        if (tile instanceof TileEntityBasicMachine) {
            MachineFluidHandler fluidHandler = ((TileEntityBasicMachine) tile).getFluidHandler();
            FluidStack inputStack = fluidHandler.getInput(0).getFluid();
//            FluidStack outputStack = null;//fluidHandler.getTankProperties()[1].getContents();
            if (inputStack != null) {
                TextureAtlasSprite sprite = RenderHelper.getSprite(inputStack.getFluid());
                if (sprite != null) {
                    mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                    setGLColorFromInt(inputStack.getFluid().getColor());
                    GlStateManager.enableBlend();
                    GlStateManager.disableLighting();
                    drawTexturedModalRect(53, 63, sprite, 16, 16);
                }

                ArrayList<String> tooltipLines = new ArrayList<>();
                tooltipLines.add(inputStack.getLocalizedName());
                tooltipLines.add(TextFormatting.BLUE + "Amount: " + inputStack.amount);
                tooltipLines.add(TextFormatting.RED + "Temperature: 295 K");
                tooltipLines.add(TextFormatting.GREEN + "State: Liquid");
                drawTooltipInArea(tooltipLines, mouseX, mouseY, 53, 63, 16, 16);
            }
//            if (outputStack != null) {
//                TextureAtlasSprite sprite = RenderHelper.getSprite(outputStack.getFluid());
//                if (sprite != null) {
//                    mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
//                    setGLColorFromInt(outputStack.getFluid().getColor());
//                    GlStateManager.disableBlend();
//                    GlStateManager.disableLighting();
//                    drawTexturedModalRect(107, 63, sprite, 16, 16);
//                }
//
//                ArrayList<String> tooltipLines = new ArrayList<>();
//                tooltipLines.add(outputStack.getLocalizedName());
//                tooltipLines.add(TextFormatting.BLUE + "Amount: " + outputStack.amount);
//                tooltipLines.add(TextFormatting.RED + "Temperature: 295 K");
//                tooltipLines.add(TextFormatting.GREEN + "State: Liquid");
//                drawTooltipInArea(tooltipLines, mouseX, mouseY, 107, 63, 16, 16);
//            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(displayName, getCenteredStringX(displayName), 4, 0x404040);

        drawTooltipInArea("Show Recipes", mouseX, mouseY, (xSize / 2) - 10, 24, 20, 14);

        drawTooltipInArea(tile.getMachineState().getDisplayName(), mouseX, mouseY, (xSize / 2) - 5, 45, 10, 8);

        if (tile.getType().hasFlag(MachineFlag.FLUID)) {
            drawContainedFluids(mouseX, mouseY);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 1) {
            System.out.println("Clicked X");
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        //if (Loader.isModLoaded("JEI")) { //TODO
        if (isInGui((xSize / 2) - 10, 24, 20, 18, mouseX, mouseY)) {
            GregTechJEIPlugin.showCategory(tile.getType());
        }
        //}
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        int progressTime = (int)(20 * tile.getClientProgress());
        drawTexturedModalRect(guiLeft + (xSize / 2) - 10, guiTop + 24, xSize, 0, progressTime, 18);

        if (tile.getMachineState().getOverlayId() == 2) {
            drawTexturedModalRect(guiLeft + (xSize / 2) - 4, guiTop + 44, xSize, 54, 8, 9);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        super.renderHoveredToolTip(mouseX, mouseY);
    }

    private static void setGLColorFromInt(int color) {
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;

        GlStateManager.color(red, green, blue, 1.0F);
    }
}
