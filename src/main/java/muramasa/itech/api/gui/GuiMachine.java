package muramasa.itech.api.gui;

import muramasa.itech.api.gui.container.ContainerMachine;
import muramasa.itech.api.machines.Machine;
import muramasa.itech.common.tileentities.base.TileEntityMachine;
import muramasa.itech.integration.jei.ITJEIPlugin;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.io.IOException;
import java.util.ArrayList;

public class GuiMachine extends GuiContainer {

    private ResourceLocation bg;

    private TileEntityMachine tile;
    private String displayName;
    private ContainerMachine container;

    private IFluidHandler fluidHandlerInput, fluidHandlerOutput;
    private TextureAtlasSprite sprite;

    private GuiButton button;

    public GuiMachine(TileEntityMachine tile, ContainerMachine container) {
        super(container);
        this.tile = tile;
        this.container = container;
        xSize = 176;
        ySize = 166;

        Machine machineType = tile.getMachineType();
        displayName = machineType.getDisplayName(tile.getTier());
        bg = machineType.getGuiTexture(tile.getTier());
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.add(new GuiButton(1, guiLeft, guiTop, 16, 16, "X"));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(displayName, xSize / 2 - fontRenderer.getStringWidth(displayName) / 2, 6, 0x404040);

//        fluidHandlerInput = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.NORTH);
//        fluidHandlerOutput = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, tile.outputSide);

        drawTooltipInArea("Show Recipes", mouseX, mouseY, (xSize / 2) - 10, 24, 20, 18);

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
            ITJEIPlugin.showCategory(tile.getMachineType());
        }
        //}
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(bg);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
//        int progressTime = tile.maxProgressTicks > 0 ? (tile.curProgressTicks * 20) / tile.maxProgressTicks % (20 + 1) : 0;
        int progressTime = (int)(20 * tile.getClientProgress());
        drawTexturedModalRect(guiLeft + (xSize / 2) - 10, guiTop + 24, xSize, 0, progressTime, 18);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        super.renderHoveredToolTip(mouseX, mouseY);
    }

    public void drawTooltipInArea(String line, int mouseX, int mouseY, int x, int y, int sizeX, int sizeY) {
        ArrayList<String> list = new ArrayList<>();
        list.add(line);
        drawTooltipInArea(list, mouseX, mouseY, x, y, sizeX, sizeY);
    }

    public void drawTooltipInArea(ArrayList<String> lines, int mouseX, int mouseY, int x, int y, int sizeX, int sizeY) {
        if (isInGui(x, y, sizeX, sizeY, mouseX, mouseY)) {
            drawHoveringText(lines, mouseX - guiLeft, mouseY - guiTop);
        }
    }

    // Returns true if the given x,y coordinates are within the given rectangle
    public boolean isInRect(int x, int y, int xSize, int ySize, int mouseX, int mouseY) {
        return ((mouseX >= x && mouseX <= x+xSize) && (mouseY >= y && mouseY <= y+ySize));
    }

    public boolean isInGui(int x, int y, int xSize, int ySize, int mouseX, int mouseY) {
        return isInRect(x, y, xSize, ySize, mouseX - guiLeft, mouseY - guiTop);
    }
}
