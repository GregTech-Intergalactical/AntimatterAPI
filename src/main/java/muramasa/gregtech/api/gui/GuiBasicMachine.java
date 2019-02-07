package muramasa.gregtech.api.gui;

import muramasa.gregtech.api.data.Materials;
import muramasa.gregtech.api.gui.container.ContainerMachine;
import muramasa.gregtech.client.render.RenderHelper;
import muramasa.gregtech.common.tileentities.base.TileEntityMachine;
import muramasa.gregtech.integration.jei.GregTechJEIPlugin;
import net.minecraft.client.gui.GuiButton;

import java.io.IOException;

public class GuiBasicMachine extends GuiMachine {

//    private IFluidHandler fluidHandlerInput, fluidHandlerOutput;
//    private TextureAtlasSprite sprite;
//    private GuiButton button;

    public GuiBasicMachine(TileEntityMachine tile, ContainerMachine container) {
        super(tile, container);
    }

    @Override
    public void initGui() {
        super.initGui();
//        this.buttonList.add(new GuiButton(1, guiLeft, guiTop, 16, 16, "X"));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(displayName, getCenteredStringX(displayName), 4, 0x404040);

//        String stateName = tile.getMachineState().getName();
//        fontRenderer.drawString(stateName, getCenteredStringX(stateName), 15, 0x404040);

//        fluidHandlerInput = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.NORTH);
//        fluidHandlerOutput = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, tile.outputSide);

        drawTooltipInArea("Show Recipes", mouseX, mouseY, (xSize / 2) - 10, 24, 20, 14);

        drawTooltipInArea(tile.getMachineState().getDisplayName(), mouseX, mouseY, (xSize / 2) - 5, 45, 10, 8);

        drawTexturedModalRect(guiLeft, guiTop, RenderHelper.getSprite(Materials.Titanium.getLiquid()), 16, 16);
//        if (fluidHandlerInput.getTankProperties()[0].getContents() != null) {
//            FluidStack fluidStack = fluidHandlerInput.getTankProperties()[0].getContents();
//
//            sprite = mc.getTextureMapBlocks().getTextureExtry(fluidStack.getFluid().getStill().toString());
//            if (sprite != null) {
////                int u = tickCounter % (int) sprite.getMaxU();
////                int v = tickCounter % (int) sprite.getMaxV();
//                ResourceLocation loc = fluidStack.getFluid().getStill();
//                mc.renderEngine.bindTexture(new ResourceLocation(loc.getResourceDomain() + ":textures/" + loc.getResourcePath() + ".png"));
//////            System.out.println(tile.tank.getFluid().getFluid().getStill());
//                GlStateManager.color(1, 1, 1, 1);
//                drawTexturedModalRect(53, 63, sprite, 16, 16);
////                drawTexturedModalRect(53, 63, u, v, 16, 16);
////                sprite.updateAnimation();
//            }
//
//            ArrayList<String> tooltipLines = new ArrayList<>();
//            tooltipLines.clear();
//            tooltipLines.add(fluidStack.getLocalizedName());
//            tooltipLines.add(TextFormatting.BLUE + "Amount: " + fluidStack.amount);
//            tooltipLines.add(TextFormatting.RED + "Temperature: 295 K");
//            tooltipLines.add(TextFormatting.GREEN + "State: Liquid");
//            drawTooltipInArea(tooltipLines, mouseX, mouseY, 53, 63, 16);
//        }
//
//        if (fluidHandlerOutput.getTankProperties()[1].getContents() != null) {
//            FluidStack fluidStack = fluidHandlerOutput.getTankProperties()[0].getContents();
//
//            sprite = mc.getTextureMapBlocks().getTextureExtry(fluidStack.getFluid().getStill().toString());
//            if (sprite != null) {
////                int u = tickCounter % (int) sprite.getMaxU();
////                int v = tickCounter % (int) sprite.getMaxV();
//                ResourceLocation loc = fluidStack.getFluid().getStill();
//                mc.renderEngine.bindTexture(new ResourceLocation(loc.getResourceDomain() + ":textures/" + loc.getResourcePath() + ".png"));
//////            System.out.println(tile.tank.getFluid().getFluid().getStill());
//                GlStateManager.color(1, 1, 1, 1);
//                drawTexturedModalRect(107, 63, sprite, 16, 16);
////                drawTexturedModalRect(53, 63, u, v, 16, 16);
////                sprite.updateAnimation();
//            }
//
//            ArrayList<String> tooltipLines = new ArrayList<>();
//            tooltipLines.clear();
//            tooltipLines.add(fluidStack.getLocalizedName());
//            tooltipLines.add(TextFormatting.BLUE + "Amount: " + fluidStack.amount);
//            tooltipLines.add(TextFormatting.RED + "Temperature: 295 K");
//            tooltipLines.add(TextFormatting.GREEN + "State: Liquid");
//            drawTooltipInArea(tooltipLines, mouseX, mouseY, 107, 63, 16);
//        }
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
            GregTechJEIPlugin.showCategory(tile.getMachineType());
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
}
