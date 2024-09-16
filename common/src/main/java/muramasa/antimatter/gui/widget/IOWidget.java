package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import muramasa.antimatter.Ref;
import muramasa.antimatter.cover.CoverOutput;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.Widget;
import muramasa.antimatter.gui.container.ContainerMachine;
import muramasa.antimatter.gui.event.GuiEvents;
import muramasa.antimatter.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

import static muramasa.antimatter.gui.ICanSyncData.SyncDirection.SERVER_TO_CLIENT;
import static muramasa.antimatter.machine.MachineFlag.FLUID;
import static muramasa.antimatter.machine.MachineFlag.ITEM;

public class IOWidget extends Widget {

    private boolean hasItem = false;
    private boolean hasFluid = false;
    private boolean itemState = false;
    private boolean fluidState = false;

    protected IOWidget(GuiInstance instance, IGuiElement parent) {
        super(instance, parent);
        this.setX(instance.handler.getGui().getMachineData().getIoPos().x);
        this.setY(instance.handler.getGui().getMachineData().getIoPos().y);
        this.setW(36);
        this.setH(18);
        ContainerMachine<?> m = (ContainerMachine<?>) instance.container;
        if (m.getTile().getMachineType().has(ITEM)) {
            hasItem = true;
        }
        if (m.getTile().getMachineType().has(FLUID)) {
            hasFluid = true;
        }
    }

    @Override
    public void mouseOver(PoseStack stack, double mouseX, double mouseY, float partialTicks) {
        super.mouseOver(stack, mouseX, mouseY, partialTicks);
        if (isInside(0, 0, 18, 18, mouseX, mouseY) && hasFluid){
            renderTooltip(stack, Utils.translatable("antimatter.tooltip.io_widget.fluid"), mouseX, mouseY);
        } else if (isInside(18, 0, 18, 18, mouseX, mouseY) && hasItem){
            renderTooltip(stack, Utils.translatable("antimatter.tooltip.io_widget.item"), mouseX, mouseY);
        }
    }

    @Override
    public void render(PoseStack matrixStack, double mouseX, double mouseY, float partialTicks) {
        if (hasItem){
            drawTexture(matrixStack, new ResourceLocation(Ref.ID, "textures/gui/button/io.png"), realX() + 18, realY(), itemState ? 18 : 0, 18, 18, 18,36, 36);
        }
        if (hasFluid){
            drawTexture(matrixStack, new ResourceLocation(Ref.ID, "textures/gui/button/io.png"), realX(), realY(), fluidState ? 18 : 0, 0, 18, 18,36, 36);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isEnabled() && isInside(mouseX, mouseY)) {
            boolean clicked = false;
            if (hasItem && isInside(18, 0, 18, 18, mouseX, mouseY)){
                gui.sendPacket(gui.handler.createGuiPacket(new GuiEvents.GuiEvent(GuiEvents.ITEM_EJECT, Screen.hasShiftDown() ? 1 : 0, id)));
                clicked = true;
            }
            if (hasFluid && isInside(0, 0, 18, 18, mouseX, mouseY)){
                gui.sendPacket(gui.handler.createGuiPacket(new GuiEvents.GuiEvent(GuiEvents.FLUID_EJECT, Screen.hasShiftDown() ? 1 : 0, id)));
                clicked = true;
            }
            if (clicked){
                this.clickSound(Minecraft.getInstance().getSoundManager());
                this.onClick(mouseX, mouseY, button);
                return true;
            }
        }
        return false;
    }

    @Override
    public void init() {
        super.init();
        ContainerMachine<?> m = (ContainerMachine<?>) gui.container;
        if (hasItem)
            gui.syncBoolean(() -> (m.getTile().coverHandler.map(t -> ((CoverOutput) t.getOutputCover()).shouldOutputItems()).orElse(false)), this::setItem, SERVER_TO_CLIENT);
        if (hasFluid)
            gui.syncBoolean(() -> (m.getTile().coverHandler.map(t -> ((CoverOutput) t.getOutputCover()).shouldOutputFluids()).orElse(false)), this::setFluid, SERVER_TO_CLIENT);
    }

    private void setItem(boolean item) {
        this.itemState = item;
    }

    private void setFluid(boolean item) {
        this.fluidState = item;
    }

    public static WidgetSupplier build(int x, int y) {
        return builder(IOWidget::new);
    }
}
