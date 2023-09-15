package muramasa.antimatter.gui.widget;

import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.gui.ButtonOverlay;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.ICanSyncData;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.event.GuiEvents;
import muramasa.antimatter.gui.event.IGuiEvent;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;

public class CycleButtonWidget extends ButtonWidget{
    final ButtonOverlay[] buttons;
    final ToIntFunction<IGuiHandler> syncFunction;
    protected IntFunction<String> tooltipKeyFunction;
    int state = 0;
    public CycleButtonWidget(GuiInstance instance, IGuiElement parent, @Nullable Consumer<ButtonWidget> onPress, ToIntFunction<IGuiHandler> syncFunction, ButtonOverlay... buttons) {
        super(instance, parent, buttons[0], onPress);
        this.buttons = buttons;
        this.syncFunction = syncFunction;
    }

    @Override
    public void init() {
        this.gui.syncInt(() -> syncFunction.applyAsInt(gui.handler), i -> state = i, ICanSyncData.SyncDirection.SERVER_TO_CLIENT);
    }

    @Override
    protected ButtonOverlay getBody() {
        return buttons[state];
    }

    @Override
    protected String getTooltipKey() {
        if (tooltipKeyFunction == null) return super.getTooltipKey();
        return tooltipKeyFunction.apply(state);
    }

    public CycleButtonWidget setTooltipKeyFunction(IntFunction<String> tooltipKeyFunction) {
        this.tooltipKeyFunction = tooltipKeyFunction;
        return this;
    }

    public static WidgetSupplier build(ToIntFunction<IGuiHandler> syncFunction, IGuiEvent.IGuiEventFactory ev, int id, ButtonOverlay... buttons) {
        return builder(((a, b) -> new CycleButtonWidget(a, b, but -> but.gui.sendPacket(but.gui.handler.createGuiPacket(new GuiEvents.GuiEvent(ev, Screen.hasShiftDown() ? 1 : 0, id))), syncFunction, buttons)));
    }

    public static WidgetSupplier build(ToIntFunction<IGuiHandler> syncFunction, IGuiEvent.IGuiEventFactory ev, int id, IntFunction<String> tooltipKey, ButtonOverlay... buttons) {
        return builder(((a, b) -> new CycleButtonWidget(a, b, but -> but.gui.sendPacket(but.gui.handler.createGuiPacket(new GuiEvents.GuiEvent(ev, Screen.hasShiftDown() ? 1 : 0, id))), syncFunction, buttons).setTooltipKeyFunction(tooltipKey)));
    }
}
