package muramasa.antimatter.tile.single;

import com.mojang.blaze3d.matrix.MatrixStack;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.event.GuiEvents;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.gui.widget.InfoRenderWidget;
import muramasa.antimatter.gui.widget.WidgetSupplier;
import muramasa.antimatter.integration.jei.renderer.IInfoRenderer;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.types.Machine;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.PlayerEntity;
import tesseract.api.capability.TesseractGTCapability;

import static muramasa.antimatter.gui.ICanSyncData.SyncDirection.SERVER_TO_CLIENT;

public class TileEntityDigitalTransformer<T extends TileEntityDigitalTransformer<T>> extends TileEntityTransformer<T> implements IInfoRenderer<TileEntityDigitalTransformer.DigitalTransformerWidget> {

    public TileEntityDigitalTransformer(Machine<?> type) {
        super(type, 0, (v) -> (8192L + v * 64L));
    }

    @Override
    public void onGuiEvent(IGuiEvent event, PlayerEntity playerEntity) {
        if (event.getFactory() == GuiEvents.EXTRA_BUTTON) {
            energyHandler.ifPresent(h -> {
                GuiEvents.GuiEvent ev = (GuiEvents.GuiEvent) event;
                int[] data = ev.data;
                boolean shiftHold = data[0] != 0;
                switch (data[1]) {
                    case 0:
                        voltage /= shiftHold ? 512 : 64;
                        break;
                    case 1:
                        voltage -= shiftHold ? 512 : 64;
                        break;
                    case 2:
                        amperage /= shiftHold ? 512 : 64;
                        break;
                    case 3:
                        amperage -= shiftHold ? 512 : 64;
                        break;
                    case 4:
                        voltage /= shiftHold ? 16 : 2;
                        break;
                    case 5:
                        voltage -= shiftHold ? 16 : 1;
                        break;
                    case 6:
                        amperage /= shiftHold ? 16 : 2;
                        break;
                    case 7:
                        amperage -= shiftHold ? 16 : 1;
                        break;
                    case 8:
                        voltage += shiftHold ? 512 : 64;
                        break;
                    case 9:
                        voltage *= shiftHold ? 512 : 64;
                        break;
                    case 10:
                        amperage += shiftHold ? 512 : 64;
                        break;
                    case 11:
                        amperage *= shiftHold ? 512 : 64;
                        break;
                    case 12:
                        voltage += shiftHold ? 16 : 1;
                        break;
                    case 13:
                        voltage *= shiftHold ? 16 : 2;
                        break;
                    case 14:
                        amperage += shiftHold ? 16 : 1;
                        break;
                    case 15:
                        amperage *= shiftHold ? 16 : 2;
                        break;
                }
                amperage = Math.max(amperage, 0);
                voltage = Math.max(voltage, 0);
                setMachineState((long) (amperage * voltage) >= 0L ? getDefaultMachineState() : MachineState.DISABLED);

                if (isDefaultMachineState()) {
                    h.setInputVoltage(getMachineTier().getVoltage());
                    h.setOutputVoltage(voltage);
                    h.setInputAmperage(amperage);
                    h.setOutputVoltage(1);
                } else {
                    h.setInputVoltage(voltage);
                    h.setOutputVoltage(getMachineTier().getVoltage());
                    h.setInputAmperage(1);
                    h.setOutputVoltage(amperage);
                }

                this.refreshCap(TesseractGTCapability.ENERGY_HANDLER_CAPABILITY);
            });
        }
    }

    @Override
    public int drawInfo(DigitalTransformerWidget widget, MatrixStack stack, FontRenderer renderer, int left, int top) {
        renderer.drawString(stack, "Control Panel", left + 43, top + 21, 16448255);
        renderer.drawString(stack, "VOLT: " + widget.voltage, left + 43, top + 40, 16448255);
        renderer.drawString(stack, "TIER: " + Tier.getTier(widget.voltage < 0 ? -widget.voltage : widget.voltage).getId().toUpperCase(), left + 43, top + 48, 16448255);
        renderer.drawString(stack, "AMP: " + widget.amperage, left + 43, top + 56, 16448255);
        renderer.drawString(stack, "SUM: " + (long) (widget.amperage * widget.voltage), left + 43, top + 64, 16448255);
        return 72;
    }

    @Override
    public void addWidgets(GuiInstance instance, IGuiElement parent) {
        super.addWidgets(instance, parent);
        instance.addWidget(DigitalTransformerWidget.build());
    }

    public static class DigitalTransformerWidget extends InfoRenderWidget<DigitalTransformerWidget> {
        public int amperage = 0, voltage = 0;

        protected DigitalTransformerWidget(GuiInstance gui, IGuiElement parent, IInfoRenderer<DigitalTransformerWidget> renderer) {
            super(gui, parent, renderer);
        }

        @Override
        public void init() {
            super.init();
            TileEntityDigitalTransformer<?> m = (TileEntityDigitalTransformer<?>) gui.handler;
            gui.syncInt(() -> m.amperage, i -> amperage = i, SERVER_TO_CLIENT);
            gui.syncInt(() -> m.voltage, i -> voltage = i, SERVER_TO_CLIENT);
        }

        public static WidgetSupplier build() {
            return builder((a, b) -> new DigitalTransformerWidget(a, b, (IInfoRenderer) a.handler));
        }
    }
}
