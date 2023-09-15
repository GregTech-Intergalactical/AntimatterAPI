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
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.tile.TileEntityTank;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.function.Function;

import static muramasa.antimatter.machine.MachineFlag.*;

public class TankMachine extends Machine<TankMachine> {
    final Function<Tier, Integer> capacityPerTier;

    public TankMachine(String domain, String name) {
        this(domain, name, t -> 8000 * (1 + t.getIntegerId()));
    }

    public TankMachine(String domain, String name, Function<Tier, Integer> capacityPerTier) {
        super(domain, name);
        this.capacityPerTier = capacityPerTier;
        setTile(TileEntityTank::new);
        setTooltipInfo((machine, stack, world, tooltip, flag) -> {
            tooltip.add(new TranslatableComponent("machine.tank.capacity", capacityPerTier.apply(machine.getTier())));
        });
        addFlags(ITEM, FLUID, COVERABLE);
        setGUI(Data.BASIC_MENU_HANDLER);
        frontCovers();
        allowFrontIO();
    }

    public Function<Tier, Integer> getCapacityPerTier() {
        return capacityPerTier;
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
