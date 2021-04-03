package muramasa.antimatter.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.gui.ButtonOverlay;
import muramasa.antimatter.gui.container.ContainerMachine;
import muramasa.antimatter.gui.event.GuiEvent;
import muramasa.antimatter.gui.widget.SwitchWidget;
import muramasa.antimatter.machine.MachineFlag;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.network.packets.TileGuiEventPacket;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import static muramasa.antimatter.Data.COVEROUTPUT;

public class ScreenBasicMachine<T extends ContainerMachine> extends ScreenMachine<T> {

    private final static ButtonOverlay FLUID = new ButtonOverlay("fluid_eject", 177, 19, 16, 16);
    private final static ButtonOverlay ITEM = new ButtonOverlay("item_eject", 177, 37, 16, 16);
    private Widget item, fluid;

    public ScreenBasicMachine(T container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack stack, int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(stack, mouseX, mouseY);
        if (container.getTile().has(MachineFlag.RECIPE))
            drawTooltipInArea(stack, container.getTile().getMachineState().getDisplayName(), mouseX, mouseY, (xSize / 2) - 5, 45, 10, 8);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(stack, partialTicks, mouseX, mouseY);
        drawProgress(stack, partialTicks, mouseX, mouseY);
        //Draw error.
        if (container.getTile().has(MachineFlag.RECIPE)) {
            if (container.getTile().getMachineState() == MachineState.POWER_LOSS) {
                drawTexture(stack, gui, guiLeft + (xSize / 2) - 4, guiTop + 45, xSize, 55, 8, 8);
            }
        }
    }

    @Override
    protected void init() {
        super.init();
        ResourceLocation loc = container.getTile().getMachineType().getGui().getButtonLocation();
        boolean shouldDrawIO = this.getClass() == ScreenBasicMachine.class && container.getTile().getClass() == TileEntityMachine.class
                && container.getTile().getMachineTier().getVoltage() > 0;
        if (shouldDrawIO) {
            if (container.getTile().has(MachineFlag.ITEM)) {
                item = new SwitchWidget(gui, guiLeft + 35, guiTop + 63, 16, 16, ITEM, (b, s) -> {
                    Antimatter.NETWORK.sendToServer(new TileGuiEventPacket(GuiEvent.ITEM_EJECT, container.getTile().getPos(), s ? 1 : 0));
                }, container.getTile().coverHandler.map(t -> COVEROUTPUT.shouldOutputItems(t.get(t.getOutputFacing()))).orElse(false));
            }
            if (container.getTile().has(MachineFlag.FLUID)) {
                fluid = new SwitchWidget(gui, guiLeft + 53, guiTop + 63, 16, 16, FLUID, (b, s) -> {
                    Antimatter.NETWORK.sendToServer(new TileGuiEventPacket(GuiEvent.FLUID_EJECT, container.getTile().getPos(), s ? 1 : 0));
                },container.getTile().coverHandler.map(t -> COVEROUTPUT.shouldOutputFluids(t.get(t.getOutputFacing()))).orElse(false));
            }
            if (item != null || fluid != null) {
                addButton(new SwitchWidget(loc, guiLeft + 9, guiTop + 64, 14, 14, ButtonOverlay.INPUT_OUTPUT , (b, s) -> {
                    if (s) {
                        if (item != null) addButton(item);
                        if (fluid != null) addButton(fluid);
                    } else {
                        if (item != null) removeButton(item);
                        if (fluid != null) removeButton(fluid);
                    }
                }, false));
            }
        }
    }
}
