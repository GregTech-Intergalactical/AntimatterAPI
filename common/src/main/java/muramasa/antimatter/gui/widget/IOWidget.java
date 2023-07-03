package muramasa.antimatter.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import muramasa.antimatter.Ref;
import muramasa.antimatter.cover.CoverOutput;
import muramasa.antimatter.gui.ButtonOverlay;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.Widget;
import muramasa.antimatter.gui.container.ContainerMachine;
import muramasa.antimatter.gui.event.GuiEvents;
import muramasa.antimatter.util.int4;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

import static muramasa.antimatter.gui.ICanSyncData.SyncDirection.SERVER_TO_CLIENT;
import static muramasa.antimatter.machine.MachineFlag.FLUID;
import static muramasa.antimatter.machine.MachineFlag.ITEM;

public class IOWidget extends Widget {
    @Nullable
    protected ButtonWidget item;
    @Nullable
    protected ButtonWidget fluid;
    private static final int4 fluidLoc = new int4(176, 18, 18, 18), itemLoc = new int4(176, 36, 18, 18);

    private boolean hasItem = false;
    private boolean hasFluid = false;
    private boolean itemState = false;
    private boolean fluidState = false;

    protected IOWidget(GuiInstance instance, IGuiElement parent, int x, int y) {
        super(instance, parent);
        this.setX(instance.handler.getGui().getIoPos().x);
        this.setY(instance.handler.getGui().getIoPos().y);
        this.setW(36);
        this.setH(18);
        ContainerMachine<?> m = (ContainerMachine<?>) instance.container;
        if (m.getTile().getMachineType().has(ITEM)) {
            hasItem = true;
            this.item = (ButtonWidget) ButtonWidget.build(new ResourceLocation(Ref.ID, "textures/gui/button/io.png"), new ResourceLocation(Ref.ID, "textures/gui/button/io.png"), itemLoc, null, GuiEvents.ITEM_EJECT, 0).setSize(26, 0, w, h).buildAndAdd(instance, this);
            item.setEnabled(false);
            item.setStateHandler(wid -> itemState);
            item.setDepth(depth() + 1);
        }
        if (m.getTile().getMachineType().has(FLUID)) {
            hasFluid = true;
            this.fluid = (ButtonWidget) ButtonWidget.build(new ResourceLocation(Ref.ID, "textures/gui/button/io.png"), new ResourceLocation(Ref.ID, "textures/gui/button/io.png"), fluidLoc, null, GuiEvents.FLUID_EJECT, 0).setSize(44, 0, w, h).buildAndAdd(instance, this);
            fluid.setStateHandler(wid -> fluidState);
            fluid.setEnabled(false);
            fluid.setDepth(depth() + 1);
        }
    }

    @Override
    public void render(PoseStack matrixStack, double mouseX, double mouseY, float partialTicks) {
        if (hasItem){
            drawTexture(matrixStack, new ResourceLocation(Ref.ID, "textures/gui/button/io.png"), realX() + 18, realY(), itemState ? 18 : 0, 18, 18, 18);
        }
        if (hasFluid){
            drawTexture(matrixStack, new ResourceLocation(Ref.ID, "textures/gui/button/io.png"), realX(), realY(), fluidState ? 18 : 0, 0, 18, 18);
        }
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

    public static WidgetSupplier build(int x, int y, int w, int h) {
        return builder((a, b) -> new IOWidget(a, b, x, y));
    }
}
