package muramasa.antimatter.blockentity.single;

import com.mojang.blaze3d.vertex.PoseStack;
import muramasa.antimatter.capability.EnergyHandler;
import muramasa.antimatter.capability.machine.MachineEnergyHandler;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.event.GuiEvents;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.gui.widget.InfoRenderWidget;
import muramasa.antimatter.gui.widget.WidgetSupplier;
import muramasa.antimatter.integration.jeirei.renderer.IInfoRenderer;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.blockentity.BlockEntityMachine;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

import static muramasa.antimatter.gui.ICanSyncData.SyncDirection.SERVER_TO_CLIENT;

public class BlockEntityInfiniteStorage<T extends BlockEntityInfiniteStorage<T>> extends BlockEntityMachine<T> implements IInfoRenderer<BlockEntityInfiniteStorage.InfiniteStorageWidget> {

    public BlockEntityInfiniteStorage(Machine<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        energyHandler.set(() -> new MachineEnergyHandler<T>((T) this, Long.MAX_VALUE, Long.MAX_VALUE, 0, 32, 0, 4) {

            @Override
            public long extractEu(long voltage, boolean simulate) {
                return Math.min(voltage, getOutputVoltage());
            }

            @Override
            public boolean canOutput(Direction direction) {
                return tile.getFacing() == direction;
            }
        });
        // TODO
        /*
        interactHandler.setup((tile, tag) -> new MachineInteractHandler<BlockEntityMachine>(tile, tag) {
            @Override
            public boolean onInteract(PlayerEntity player, Hand hand, Direction side, @Nullable AntimatterToolType type) {
                if ((type == SCREWDRIVER || type == ELECTRIC_SCREWDRIVER) && hand == Hand.MAIN_HAND) {
                    energyHandler.ifPresent(h -> {
                        int amps = h.getOutputAmperage();
                        amps = (amps + 1) % amperage;
                        h.setOutputAmperage(amps);
                        // TODO: Replace by new TranslationTextComponent()
                        player.sendMessage(new StringTextComponent(h.getOutputVoltage() + "V@" + h.getOutputAmperage() + "Amp"));
                    });
                    return true;
                }
                return super.onInteract(player, hand, side, type);
            }
        });
         */


    }

    @Override
    protected boolean allowExplosionsInRain() {
        return false;
    }

    @Override
    public void onGuiEvent(IGuiEvent event, Player playerEntity) {
        if (event.getFactory() == GuiEvents.EXTRA_BUTTON) {
            final int[] data = ((GuiEvents.GuiEvent)event).data;
            energyHandler.ifPresent(h -> {
                int voltage = (int) h.getOutputVoltage();
                int amperage = (int) h.getOutputAmperage();
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

                if (voltage < 0){
                    voltage = 0;
                }
                if (amperage < 0){
                    amperage = 0;
                }

                h.setOutputVoltage(voltage);
                h.setOutputAmperage(amperage);
            });
        }
    }

    @Override
    public List<String> getInfo(boolean simple) {
        List<String> info = super.getInfo(simple);
        energyHandler.ifPresent(h -> {
            info.add("Voltage Out: " + h.getOutputVoltage());
            info.add("Amperage Out: " + h.getOutputAmperage());
        });
        return info;
    }

    @Override
    public int drawInfo(InfiniteStorageWidget widget, PoseStack stack, Font renderer, int left, int top) {
        renderer.draw(stack,"Control Panel", left + 43, top + 21, 16448255);
        renderer.draw(stack,"VOLT: " + widget.voltage, left + 43, top + 40, 16448255);
        renderer.draw(stack,"TIER: " + Tier.getTier(widget.voltage < 0 ? -widget.voltage : widget.voltage).getId().toUpperCase(), left + 43, top + 48, 16448255);
        renderer.draw(stack,"AMP: " + widget.amperage, left + 43, top + 56, 16448255);
        renderer.draw(stack,"SUM: " + (long)(widget.amperage * widget.voltage), left + 43, top + 64, 16448255);
        return 72;
    }

    @Override
    public void addWidgets(GuiInstance instance, IGuiElement parent) {
        super.addWidgets(instance, parent);
        instance.addWidget(InfiniteStorageWidget.build());
    }

    public static class InfiniteStorageWidget extends InfoRenderWidget<InfiniteStorageWidget> {
        public int amperage = 0;
        public long voltage = 0;
        protected InfiniteStorageWidget(GuiInstance gui, IGuiElement parent, IInfoRenderer<InfiniteStorageWidget> renderer) {
            super(gui, parent, renderer);
        }

        @Override
        public void init() {
            super.init();
            BlockEntityInfiniteStorage<?> m = (BlockEntityInfiniteStorage<?>) gui.handler;
            gui.syncInt(() -> Math.toIntExact(m.energyHandler.map(EnergyHandler::getOutputAmperage).orElse(0L)), i -> amperage = i, SERVER_TO_CLIENT);
            gui.syncLong(() -> m.energyHandler.map(EnergyHandler::getOutputVoltage).orElse(0L), i -> voltage = i, SERVER_TO_CLIENT);
        }

        public static WidgetSupplier build() {
            return builder((a,b) -> new InfiniteStorageWidget(a,b, (IInfoRenderer) a.handler));
        }
    }
}