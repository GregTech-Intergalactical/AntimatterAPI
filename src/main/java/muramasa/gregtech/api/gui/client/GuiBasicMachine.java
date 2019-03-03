package muramasa.gregtech.api.gui.client;

import muramasa.gregtech.api.capability.impl.MachineFluidHandler;
import muramasa.gregtech.api.data.Materials;
import muramasa.gregtech.api.gui.SlotData;
import muramasa.gregtech.api.gui.SlotType;
import muramasa.gregtech.api.gui.server.ContainerMachine;
import muramasa.gregtech.api.machines.MachineFlag;
import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.common.tileentities.base.TileEntityMachine;
import muramasa.gregtech.common.tileentities.overrides.TileEntityBasicMachine;
import muramasa.gregtech.integration.jei.renderer.FluidStackRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.util.ITooltipFlag;

import java.io.IOException;
import java.util.List;

public class GuiBasicMachine extends GuiMachine {

    public GuiBasicMachine(TileEntityMachine tile, ContainerMachine container) {
        super(tile, container);
    }

    @Override
    public void initGui() {
        super.initGui();
//        this.buttonList.get(new GuiButton(1, guiLeft, guiTop, 16, 16, "X"));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        drawTooltipInArea(tile.getMachineState().getDisplayName(), mouseX, mouseY, (xSize / 2) - 5, 45, 10, 8);
        if (tile.getType().hasFlag(MachineFlag.FLUID)) {
            drawContainedFluids(mouseX, mouseY);
        }
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

    public void drawContainedFluids(int mouseX, int mouseY) {
        if (tile instanceof TileEntityBasicMachine) {
            FluidStackRenderer renderer = new FluidStackRenderer();
            MachineFluidHandler fluidHandler = tile.getFluidHandler();
//            for (FluidStack stack : fluidHandler.getInputs()) {
//                renderer.;
//            }
            List<SlotData> slots = tile.getType().getGui().getTypes(SlotType.FL_IN, Tier.getMax());
            if (slots != null && slots.size() > 0) {
                renderer.render(mc, 107, 63, Materials.Iron.getLiquid(245));
                drawTooltipInArea(renderer.getTooltip(mc, Materials.Iron.getLiquid(245), ITooltipFlag.TooltipFlags.NORMAL), mouseX, mouseY, slots.get(0).x, slots.get(0).y, 16, 16);
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 1) {
            System.out.println("Clicked X");
        }
    }
}
