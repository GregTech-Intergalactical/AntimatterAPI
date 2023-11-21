package muramasa.antimatter.blockentity.single;

import com.mojang.blaze3d.vertex.PoseStack;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.event.GuiEvents;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.gui.widget.InfoRenderWidget;
import muramasa.antimatter.gui.widget.WidgetSupplier;
import muramasa.antimatter.integration.jeirei.renderer.IInfoRenderer;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.types.Machine;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

import static muramasa.antimatter.gui.ICanSyncData.SyncDirection.SERVER_TO_CLIENT;

public class BlockEntityDigitalTransformer<T extends BlockEntityDigitalTransformer<T>> extends BlockEntityTransformer<T> implements IInfoRenderer<BlockEntityDigitalTransformer.DigitalTransformerWidget> {

    public BlockEntityDigitalTransformer(Machine<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, 0, (v) -> (8192L + v * 64L));
    }

    @Override
    public void onGuiEvent(IGuiEvent event, Player playerEntity) {
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
                    h.setOutputAmperage(amperage);
                    h.setInputAmperage(1);
                } else {
                    h.setInputVoltage(voltage);
                    h.setOutputVoltage(getMachineTier().getVoltage());
                    h.setOutputAmperage(1);
                    h.setInputAmperage(amperage);
                }
            });
        }
    }

    @Override
    public int drawInfo(DigitalTransformerWidget widget, PoseStack stack, Font renderer, int left, int top) {
        renderer.draw(stack, "Control Panel", left + 43, top + 21, 16448255);
        renderer.draw(stack, "VOLT: " + widget.voltage, left + 43, top + 40, 16448255);
        renderer.draw(stack, "TIER: " + Tier.getTier(widget.voltage < 0 ? -widget.voltage : widget.voltage).getId().toUpperCase(), left + 43, top + 48, 16448255);
        renderer.draw(stack, "AMP: " + widget.amperage, left + 43, top + 56, 16448255);
        renderer.draw(stack, "SUM: " + (widget.amperage * widget.voltage), left + 43, top + 64, 16448255);
        return 72;
    }

    @Override
    public void addWidgets(GuiInstance instance, IGuiElement parent) {
        super.addWidgets(instance, parent);
        instance.addWidget(DigitalTransformerWidget.build());
    }

    public static class DigitalTransformerWidget extends InfoRenderWidget<DigitalTransformerWidget> {
        public int amperage = 0;
        public long voltage = 0;

        protected DigitalTransformerWidget(GuiInstance gui, IGuiElement parent, IInfoRenderer<DigitalTransformerWidget> renderer) {
            super(gui, parent, renderer);
        }

        @Override
        public void init() {
            super.init();
            BlockEntityDigitalTransformer<?> m = (BlockEntityDigitalTransformer<?>) gui.handler;
            gui.syncInt(() -> m.amperage, i -> amperage = i, SERVER_TO_CLIENT);
            gui.syncLong(() -> m.voltage, i -> voltage = i, SERVER_TO_CLIENT);
        }

        public static WidgetSupplier build() {
            return builder((a, b) -> new DigitalTransformerWidget(a, b, (IInfoRenderer) a.handler));
        }
    }
}
