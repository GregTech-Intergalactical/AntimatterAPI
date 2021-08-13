package muramasa.antimatter.gui.widget;

import muramasa.antimatter.gui.ButtonOverlay;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.container.ContainerMachine;
import muramasa.antimatter.gui.event.GuiEvent;
import muramasa.antimatter.util.int4;
import net.minecraft.util.ResourceLocation;

import static muramasa.antimatter.Data.COVEROUTPUT;
import static muramasa.antimatter.machine.MachineFlag.FLUID;
import static muramasa.antimatter.machine.MachineFlag.ITEM;

public class IOWidget extends AbstractSwitchWidget {

    protected ButtonWidget item;
    protected ButtonWidget fluid;
    private static final int4 fluidLoc = new int4(176, 18, 18, 18), itemLoc = new int4(176, 36, 18, 18);

    private boolean itemState = false;
    private boolean fluidState = false;

    protected IOWidget(GuiInstance instance, int x, int y, int w, int h) {
        super(instance, new ResourceLocation(instance.handler.getDomain(), "textures/gui/button/gui_buttons.png"), ButtonOverlay.INPUT_OUTPUT, IOWidget::handler, false);
        this.setX(x);
        this.setY(y);
        this.setW(w);
        this.setH(h);
        ContainerMachine<?> m = (ContainerMachine<?>) instance.container;
        if (m.getTile().getMachineType().has(ITEM)) {
            this.item = (ButtonWidget) ButtonWidget.build(new ResourceLocation(instance.handler.getDomain(), "textures/gui/button/gui_buttons.png"), instance.handler.getGuiTexture(), itemLoc, null, GuiEvent.ITEM_EJECT,0).setSize(x+26, y, w, h).get().get(instance);
            this.item.setParent(this);
            item.setEnabled(false);
            item.setStateHandler(wid -> itemState);
            item.setDepth(depth()+1);
            instance.addWidget(item);
        }
        if (m.getTile().getMachineType().has(FLUID)) {
            this.fluid = (ButtonWidget) ButtonWidget.build(new ResourceLocation(instance.handler.getDomain(), "textures/gui/button/gui_buttons.png"), instance.handler.getGuiTexture(), fluidLoc, null, GuiEvent.FLUID_EJECT,0).setSize(x+44, y, w, h).get().get(instance);
            fluid.setStateHandler(wid -> fluidState);
            fluid.setEnabled(false);
            this.fluid.setParent(this);
            fluid.setDepth(depth()+1);
            instance.addWidget(fluid);
        }
    }

    @Override
    public void init() {
        super.init();
        ContainerMachine<?> m = (ContainerMachine<?>) gui.container;
        if (item != null) gui.syncBoolean(() -> (m.getTile().coverHandler.map(t -> COVEROUTPUT.shouldOutputItems(t.getOutputCover())).orElse(false)), this::setItem);
        if (fluid != null) gui.syncBoolean(() -> (m.getTile().coverHandler.map(t -> COVEROUTPUT.shouldOutputFluids(t.getOutputCover())).orElse(false)), this::setFluid);
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
        return builder(i -> new IOWidget(i, x, y, w, h));
    }
}
