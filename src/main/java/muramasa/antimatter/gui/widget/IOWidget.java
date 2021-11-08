package muramasa.antimatter.gui.widget;

import muramasa.antimatter.cover.CoverOutput;
import muramasa.antimatter.gui.ButtonOverlay;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.container.ContainerMachine;
import muramasa.antimatter.gui.event.GuiEvent;
import muramasa.antimatter.util.int4;
import net.minecraft.util.ResourceLocation;

import static muramasa.antimatter.gui.ICanSyncData.SyncDirection.SERVER_TO_CLIENT;
import static muramasa.antimatter.machine.MachineFlag.FLUID;
import static muramasa.antimatter.machine.MachineFlag.ITEM;

public class IOWidget extends AbstractSwitchWidget {

    protected ButtonWidget item;
    protected ButtonWidget fluid;
    private static final int4 fluidLoc = new int4(176, 18, 18, 18), itemLoc = new int4(176, 36, 18, 18);

    private boolean itemState = false;
    private boolean fluidState = false;

    protected IOWidget(GuiInstance instance, IGuiElement parent, int x, int y, int w, int h) {
        super(instance, parent, new ResourceLocation(instance.handler.handlerDomain(), "textures/gui/button/gui_buttons.png"), ButtonOverlay.INPUT_OUTPUT, IOWidget::handler, false);
        this.setX(x);
        this.setY(y);
        this.setW(w);
        this.setH(h);
        ContainerMachine<?> m = (ContainerMachine<?>) instance.container;
        if (m.getTile().getMachineType().has(ITEM)) {
            this.item = (ButtonWidget) ButtonWidget.build(new ResourceLocation(instance.handler.handlerDomain(), "textures/gui/button/gui_buttons.png"), instance.handler.getGuiTexture(), itemLoc, null, GuiEvent.ITEM_EJECT, 0).setSize(26, 0, w, h).buildAndAdd(instance, this);
            item.setEnabled(false);
            item.setStateHandler(wid -> itemState);
            item.setDepth(depth() + 1);
        }
        if (m.getTile().getMachineType().has(FLUID)) {
            this.fluid = (ButtonWidget) ButtonWidget.build(new ResourceLocation(instance.handler.handlerDomain(), "textures/gui/button/gui_buttons.png"), instance.handler.getGuiTexture(), fluidLoc, null, GuiEvent.FLUID_EJECT, 0).setSize(44, 0, w, h).buildAndAdd(instance, this);
            fluid.setStateHandler(wid -> fluidState);
            fluid.setEnabled(false);
            fluid.setDepth(depth() + 1);
        }
    }

    @Override
    public void init() {
        super.init();
        ContainerMachine<?> m = (ContainerMachine<?>) gui.container;
        if (item != null)
            gui.syncBoolean(() -> (m.getTile().coverHandler.map(t -> ((CoverOutput) t.getOutputCover()).shouldOutputItems()).orElse(false)), this::setItem, SERVER_TO_CLIENT);
        if (fluid != null)
            gui.syncBoolean(() -> (m.getTile().coverHandler.map(t -> ((CoverOutput) t.getOutputCover()).shouldOutputFluids()).orElse(false)), this::setFluid, SERVER_TO_CLIENT);
    }

    @Override
    public void updateSize() {
        super.updateSize();
        if (item != null) item.updateSize();
        if (fluid != null) fluid.updateSize();
    }

    private static void handler(AbstractSwitchWidget widget, boolean state) {
        IOWidget wid = (IOWidget) widget;
        if (wid.item != null) wid.item.setEnabled(state);
        if (wid.fluid != null) wid.fluid.setEnabled(state);
    }

    private void setItem(boolean item) {
        this.itemState = item;
    }

    private void setFluid(boolean item) {
        this.fluidState = item;
    }

    public static WidgetSupplier build(int x, int y, int w, int h) {
        return builder((a, b) -> new IOWidget(a, b, x, y, w, h));
    }
}
