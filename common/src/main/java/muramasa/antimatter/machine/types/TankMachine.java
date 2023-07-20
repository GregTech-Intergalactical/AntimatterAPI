package muramasa.antimatter.machine.types;

import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.utils.FluidHooks;
import muramasa.antimatter.Data;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.ICanSyncData;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.widget.InfoRenderWidget;
import muramasa.antimatter.gui.widget.WidgetSupplier;
import muramasa.antimatter.integration.jeirei.renderer.IInfoRenderer;
import muramasa.antimatter.tile.TileEntityTank;

import static muramasa.antimatter.machine.MachineFlag.COVERABLE;
import static muramasa.antimatter.machine.MachineFlag.FLUID;
import static muramasa.antimatter.machine.MachineFlag.ITEM;

public class TankMachine extends Machine<TankMachine> {

    public TankMachine(String domain, String name) {
        super(domain, name);
        setTile(TileEntityTank::new);
        addFlags(ITEM, FLUID, COVERABLE);
        setGUI(Data.BASIC_MENU_HANDLER);
        frontCovers();
        allowFrontIO();
    }

    @Override
    protected void setupGui() {
        super.setupGui();
        addGuiCallback(t -> t.addWidget(TankRenderWidget.build().onlyIf(h -> h.handler instanceof TileEntityTank)));
    }

    public static class TankRenderWidget extends InfoRenderWidget<TankRenderWidget> {

        public FluidHolder stack = FluidHooks.emptyFluid();

        protected TankRenderWidget(GuiInstance gui, IGuiElement parent, IInfoRenderer<TankRenderWidget> renderer) {
            super(gui, parent, renderer);
        }

        @Override
        public void init() {
            super.init();
            TileEntityTank<?> tank = (TileEntityTank<?>) gui.handler;
            gui.syncFluidStack(() -> tank.fluidHandler.map(t -> t.getFluidInTank(0)).orElse(FluidHooks.emptyFluid()), f -> this.stack = f, ICanSyncData.SyncDirection.SERVER_TO_CLIENT);
        }

        public static WidgetSupplier build() {
            return builder((a, b) -> new TankRenderWidget(a, b, (IInfoRenderer<TankRenderWidget>) a.handler));
        }
    }
}
