package muramasa.antimatter.gui.widget;

import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.gui.ButtonBody;
import muramasa.antimatter.gui.ButtonOverlay;
import muramasa.antimatter.gui.container.AntimatterContainer;
import muramasa.antimatter.gui.container.ContainerMachine;
import muramasa.antimatter.gui.screen.AntimatterContainerScreen;
import muramasa.antimatter.util.int4;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.ResourceLocation;

import static muramasa.antimatter.Data.COVEROUTPUT;

public class IOWidget extends AbstractSwitchWidget {

    protected final Widget item;
    protected final Widget fluid;
    private final int4 ioLoc = new int4(9, 64, 14, 14), itemLoc = new int4(35, 63, 16, 16), fluidLoc = new int4(53, 63, 16, 16);

    protected IOWidget(AntimatterContainerScreen<?> screen, IGuiHandler handler, ResourceLocation res, ButtonOverlay overlay) {
        super(screen, handler, res, overlay, IOWidget::handler, ((ContainerMachine<?>)screen.getContainer()).getTile().coverHandler.map(t -> COVEROUTPUT.shouldOutputFluids(t.get(t.getOutputFacing()))).orElse(false));
        this.item = ButtonWidget.build(res, null, null, null).get(screen, handler);
        this.fluid = ButtonWidget.build(res, null, null, null).get(screen, handler);
    }

    private static void handler(AbstractSwitchWidget widget, boolean state) {
        IOWidget wid = (IOWidget) widget;
        wid.item.active = state;
        wid.fluid.active = state;
    }

    public static <T extends AntimatterContainer> WidgetSupplier.WidgetProvider<T> build(ResourceLocation res, ButtonOverlay overlay) {
        return (screen, handler) -> new IOWidget(screen, handler, res, overlay);
    }
    /*

     if (container.getTile().has(MachineFlag.ITEM)) {
                item = new SwitchWidget(gui, guiLeft + data.getItem().x, guiTop + data.getItem().y, data.getItem().z, data.getItem().w, data.getItemLocation(), (b, s) -> {
                    Antimatter.NETWORK.sendToServer(new TileGuiEventPacket(GuiEvent.ITEM_EJECT, container.getTile().getPos(), s ? 1 : 0));
                }, container.getTile().coverHandler.map(t -> COVEROUTPUT.shouldOutputItems(t.get(t.getOutputFacing()))).orElse(false));
            }
            if (container.getTile().has(MachineFlag.FLUID)) {
                fluid = new SwitchWidget(gui, guiLeft + data.getFluid().x, guiTop + data.getFluid().y, data.getFluid().z, data.getFluid().w, data.getFluidLocation(), (b, s) -> {
                    Antimatter.NETWORK.sendToServer(new TileGuiEventPacket(GuiEvent.FLUID_EJECT, container.getTile().getPos(), s ? 1 : 0));
                },container.getTile().coverHandler.map(t -> COVEROUTPUT.shouldOutputFluids(t.get(t.getOutputFacing()))).orElse(false));
            }
            if (item != null || fluid != null) {
                addButton(new SwitchWidget(loc, guiLeft + data.getIo().x, guiTop + data.getIo().y, data.getIo().z, data.getIo().w, ButtonOverlay.INPUT_OUTPUT , (b, s) -> {
                    if (s) {
                        if (item != null) addButton(item);
                        if (fluid != null) addButton(fluid);
                    } else {
                        if (item != null) removeButton(item);
                        if (fluid != null) removeButton(fluid);
                    }
                }, false));
            }
     */
}
