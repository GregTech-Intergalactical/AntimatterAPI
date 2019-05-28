package muramasa.gtu.api.gui.client;

import muramasa.gtu.api.capability.impl.MachineFluidHandler;
import muramasa.gtu.api.gui.SlotData;
import muramasa.gtu.api.gui.SlotType;
import muramasa.gtu.api.gui.server.ContainerMachine;
import muramasa.gtu.api.machines.MachineFlag;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.client.render.RenderHelper;
import muramasa.gtu.integration.jei.GregTechJEIPlugin;
import muramasa.gtu.integration.jei.renderer.FluidStackRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;

import java.io.IOException;
import java.util.List;

public class GuiMachine extends GuiBase {

    protected TileEntityMachine tile;
    protected ContainerMachine container;

    protected ResourceLocation background;
    protected String displayName;

    public GuiMachine(TileEntityMachine tile, ContainerMachine container) {
        super(container);
        this.tile = tile;
        this.container = container;
        background = tile.getType().getGui().getTexture(tile.getTier());
        displayName = tile.getType().getDisplayName(tile.getTier());
        xSize = 176;
        ySize = 166;
    }

    public void drawContainedFluids(int mouseX, int mouseY) {
        MachineFluidHandler fluidHandler = tile.getFluidHandler();
        if (fluidHandler == null) return;
        FluidStack[] fluids;
        List<SlotData> slots;

        //TODO this should use getInputsRaw to preserve ordering
        fluids = fluidHandler.getInputs();
        slots = tile.getType().getGui().getSlots(SlotType.FL_IN, tile.getTier());
        if (fluids != null) {
            for (int i = 0; i < fluids.length; i++) {
                if (i >= slots.size()) continue;
                RenderHelper.drawFluid(mc, slots.get(i).x, slots.get(i).y, 16, 16, 16, fluids[i]);
                drawTooltipInArea(FluidStackRenderer.getFluidTooltip(fluids[i]), mouseX, mouseY, slots.get(i).x, slots.get(i).y, 16, 16);
            }
        }
        fluids = fluidHandler.getOutputs();
        slots = tile.getType().getGui().getSlots(SlotType.FL_OUT, tile.getTier());
        if (fluids != null) {
            for (int i = 0; i < fluids.length; i++) {
                if (i >= slots.size()) continue;
                RenderHelper.drawFluid(mc, slots.get(i).x, slots.get(i).y, 16, 16, 16, fluids[i]);
                drawTooltipInArea(FluidStackRenderer.getFluidTooltip(fluids[i]), mouseX, mouseY, slots.get(i).x, slots.get(i).y, 16, 16);
            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(displayName, getCenteredStringX(displayName), 4, 0x404040);
        if (tile.getType().hasFlag(MachineFlag.RECIPE)) {
            drawTooltipInArea("Show Recipes", mouseX, mouseY, (xSize / 2) - 10, 24, 20, 14);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (!Loader.isModLoaded("jei") || !tile.getType().hasFlag(MachineFlag.RECIPE)) return;
        if (isInGui((xSize / 2) - 10, 24, 20, 18, mouseX, mouseY)) {
            GregTechJEIPlugin.showCategory(tile.getType());
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
	this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        super.renderHoveredToolTip(mouseX, mouseY);
    }
}
