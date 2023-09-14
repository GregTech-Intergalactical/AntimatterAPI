package muramasa.antimatter.gui.widget;

import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.gui.ButtonOverlay;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.ICanSyncData;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.event.GuiEvents;
import muramasa.antimatter.gui.event.IGuiEvent;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class SwitchButtonWidget extends ButtonWidget{
    final ButtonOverlay bodyOff;
    final ButtonOverlay bodyOn;
    final Predicate<IGuiHandler> syncFunction;
    boolean on = false;
    public SwitchButtonWidget(GuiInstance instance, IGuiElement parent, @NotNull ButtonOverlay bodyOff, @NotNull ButtonOverlay bodyOn, @Nullable Consumer<ButtonWidget> onPress, Predicate<IGuiHandler> syncFunction) {
        super(instance, parent, bodyOff, onPress);
        this.bodyOff = bodyOff;
        this.bodyOn = bodyOn;
        this.syncFunction = syncFunction;
    }

    @Override
    public void init() {
        this.gui.syncBoolean(() -> syncFunction.test(gui.handler), b -> on = b, ICanSyncData.SyncDirection.SERVER_TO_CLIENT);
    }

    @Override
    protected ButtonOverlay getBody() {
        return on ? bodyOn : bodyOff;
    }

    public static WidgetSupplier build(ButtonOverlay bodyOff, ButtonOverlay bodyOn, Predicate<IGuiHandler> syncFunction, IGuiEvent.IGuiEventFactory ev, int id) {
        return builder(((a, b) -> new SwitchButtonWidget(a, b,  bodyOff, bodyOn, but -> but.gui.sendPacket(but.gui.handler.createGuiPacket(new GuiEvents.GuiEvent(ev, Screen.hasShiftDown() ? 1 : 0, id))), syncFunction)));
    }

    public static WidgetSupplier build(ButtonOverlay bodyOff, ButtonOverlay bodyOn, Predicate<IGuiHandler> syncFunction, IGuiEvent.IGuiEventFactory ev, int id, String tooltipKey) {
        return builder(((a, b) -> new SwitchButtonWidget(a, b, bodyOff, bodyOn, but -> but.gui.sendPacket(but.gui.handler.createGuiPacket(new GuiEvents.GuiEvent(ev, Screen.hasShiftDown() ? 1 : 0, id))), syncFunction).setTooltipKey(tooltipKey)));
    }
}
