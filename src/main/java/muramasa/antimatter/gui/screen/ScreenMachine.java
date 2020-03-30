package muramasa.antimatter.gui.screen;

import muramasa.antimatter.gui.container.ContainerMachine;
import muramasa.antimatter.machine.MachineFlag;
import muramasa.antimatter.tile.TileEntityRecipeMachine;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.ModList;

public class ScreenMachine extends AntimatterContainerScreen<ContainerMachine> {

    protected ContainerMachine container;
    protected String name;
    protected ResourceLocation gui;

    public ScreenMachine(ContainerMachine container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
        this.container = container;
        this.name = name.getString();
        gui = container.getTile().getMachineType().getGui().getTexture(container.getTile().getTier());
    }

    protected void drawTitle(int mouseX, int mouseY) {
        Minecraft.getInstance().fontRenderer.drawString(name, getCenteredStringX(name), 4, 0x404040);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        drawTitle(mouseX, mouseY);
        if (container.getTile().hasFlag(MachineFlag.RECIPE)) {
            drawTooltipInArea("Show Recipes", mouseX, mouseY, (xSize / 2) - 10, 24, 20, 14);
        }
        if (container.getTile().hasFlag(MachineFlag.FLUID)) {
            //TODO
            //drawContainedFluids(mouseX, mouseY);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawTexture(gui, guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    protected void drawProgress(float partialTicks, int mouseX, int mouseY) {
        if (!(container.getTile() instanceof TileEntityRecipeMachine)) return;
        int progressTime = (int)(20 * ((TileEntityRecipeMachine) container.getTile()).getClientProgress());
        drawTexture(gui, guiLeft + (xSize / 2) - 10, guiTop + 24, xSize, 0, progressTime, 18);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (!ModList.get().isLoaded("jei") || !container.getTile().hasFlag(MachineFlag.RECIPE)) return false;
        if (isInGui((xSize / 2) - 10, 24, 20, 18, mouseX, mouseY)) {
            //TODO
            //GregTechJEIPlugin.showCategory(container.getTile().getMachineType());
            return true;
        }
        return false;
    }
}
