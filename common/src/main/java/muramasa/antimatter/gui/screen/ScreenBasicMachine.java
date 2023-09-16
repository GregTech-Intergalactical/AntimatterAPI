package muramasa.antimatter.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import muramasa.antimatter.blockentity.BlockEntityMachine;
import muramasa.antimatter.gui.container.ContainerBasicMachine;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ScreenBasicMachine<T extends BlockEntityMachine<T>, U extends ContainerBasicMachine<T>> extends ScreenMachine<T, U> {

    public ScreenBasicMachine(U container, Inventory inv, Component name) {
        super(container, inv, name);
    }

    @Override
    protected void renderLabels(PoseStack stack, int mouseX, int mouseY) {
        super.renderLabels(stack, mouseX, mouseY);
        //if (container.getTile().has(MachineFlag.RECIPE))
        //    drawTooltipInArea(stack, container.getTile().getMachineState().getDisplayName(), mouseX, mouseY, (xSize / 2) - 5, 45, 10, 8);
    }

    @Override
    protected void renderBg(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(stack, partialTicks, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        /*
        GuiData data = container.getTile().getMachineType().getGui();
        ResourceLocation loc = data.getButtonLocation();
        boolean shouldDrawIO = container.getTile().getMachineTier().getVoltage() > 0 && data.hasIOButton();
        if (shouldDrawIO) {
            if (container.getTile().has(MachineFlag.ITEM)) {
                item = new SwitchButtonWidget(gui, guiLeft + data.getItem().x, guiTop + data.getItem().y, data.getItem().z, data.getItem().w, data.getItemLocation(), (b, s) -> {
                    Antimatter.NETWORK.sendToServer(new TileGuiEventPacket(GuiEvent.ITEM_EJECT, container.getTile().getPos(), s ? 1 : 0));
                }, container.getTile().coverHandler.map(t -> COVEROUTPUT.shouldOutputItems(t.get(t.getOutputFacing()))).orElse(false));
            }
            if (container.getTile().has(MachineFlag.FLUID)) {
                fluid = new SwitchButtonWidget(gui, guiLeft + data.getFluid().x, guiTop + data.getFluid().y, data.getFluid().z, data.getFluid().w, data.getFluidLocation(), (b, s) -> {
                    Antimatter.NETWORK.sendToServer(new TileGuiEventPacket(GuiEvent.FLUID_EJECT, container.getTile().getPos(), s ? 1 : 0));
                },container.getTile().coverHandler.map(t -> COVEROUTPUT.shouldOutputFluids(t.get(t.getOutputFacing()))).orElse(false));
            }
            if (item != null || fluid != null) {
                addButton(new SwitchButtonWidget(loc, guiLeft + data.getIo().x, guiTop + data.getIo().y, data.getIo().z, data.getIo().w, ButtonOverlay.INPUT_OUTPUT , (b, s) -> {
                    if (s) {
                        if (item != null) addButton(item);
                        if (fluid != null) addButton(fluid);
                    } else {
                        if (item != null) removeButton(item);
                        if (fluid != null) removeButton(fluid);
                    }
                }, false));
            }
        }*/
    }
}
