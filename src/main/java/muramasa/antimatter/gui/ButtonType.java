package muramasa.antimatter.gui;

import muramasa.antimatter.Antimatter;
import muramasa.antimatter.gui.container.ContainerMachine;
import muramasa.antimatter.gui.event.GuiEvent;
import muramasa.antimatter.gui.widget.ButtonWidget;
import muramasa.antimatter.gui.widget.SwitchWidjet;
import muramasa.antimatter.network.packets.GuiEventPacket;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;

public class ButtonType {

    public static ButtonType EMPTY_BODY = new ButtonType("empty_body", (l, t, c, i, r, b) -> new ButtonWidget(r, l + b.getX(), t + b.getY(), b.getW(), b.getH(), b.getBody(0), getPressable(c, i, b))); // Empty button with body
    public static ButtonType TEXT_ON_BODY = new ButtonType("text_body", (l, t, c, i, r, b) -> new ButtonWidget(r, l + b.getX(), t + b.getY(), b.getW(), b.getH(), b.getBody(0), b.getText(), getPressable(c, i, b))); // Button with body and text above
    public static ButtonType OVERLAY_ON_BODY = new ButtonType("over_body", (l, t, c, i, r, b) -> new ButtonWidget(r, l + b.getX(), t + b.getY(), b.getW(), b.getH(), b.getBody(0), b.getOverlay(0), getPressable(c, i, b))); // Button with body and overlay above
    public static ButtonType DOUBLE_SWITCH_BODY = new ButtonType("double_switch", (l, t, c, i, r, b) -> new SwitchWidjet(r, l + b.getX(), t + b.getY(), b.getW(), b.getH(), b.getBody(0), b.getBody(1), getSwitchable(c, i, b))); // Switch with two bodies which changes on the toggle
    public static ButtonType SINGLE_SWITCH_BODY = new ButtonType("single_switch", (l, t, c, i, r, b) -> new SwitchWidjet(r, l + b.getX(), t + b.getY(), b.getW(), b.getH(), b.getOverlay(0), getSwitchable(c, i, b))); // Switch with single body which change coloring on the toggle
    public static ButtonType TEXT_ON_SWITCH = new ButtonType("text_switch", (l, t, c, i, r, b) -> new SwitchWidjet(r, l + b.getX(), t + b.getY(), b.getW(), b.getH(), b.getOverlay(0), b.getText(), getSwitchable(c, i, b))); // Switch with single body which change coloring on the toggle and text above

    protected String id;
    protected ButtonType.IButtonSupplier buttonSupplier;

    public ButtonType(String id, ButtonType.IButtonSupplier slotSupplier) {
        this.id = id;
        this.buttonSupplier = slotSupplier;
    }

    public String getId() {
        return id;
    }

    public ButtonType.IButtonSupplier getButtonSupplier() {
        return buttonSupplier;
    }

    public interface IButtonSupplier {

        AbstractButton get(int l, int t, ContainerMachine container, PlayerInventory inv, ResourceLocation res, ButtonData button);
    }

    private static ButtonWidget.IPressable getPressable(ContainerMachine container, PlayerInventory inv, ButtonData button) {
        return b -> {
            int shiftHold = inv.player.isShiftKeyDown() ? 1 : 0;
            container.getTile().onGuiEvent(GuiEvent.EXTRA_BUTTON, button.getId(), shiftHold);
            Antimatter.NETWORK.sendToServer(new GuiEventPacket(GuiEvent.EXTRA_BUTTON, container.getTile().getPos(), button.getId(), shiftHold));
        };
    }

    private static SwitchWidjet.ISwitchable getSwitchable(ContainerMachine container, PlayerInventory inv, ButtonData button) {
        return (b, s) -> {
            int shiftHold = inv.player.isShiftKeyDown() ? 1 : 0;
            container.getTile().onGuiEvent(GuiEvent.EXTRA_SWITCH, button.getId(), shiftHold, s ? 1 : 0);
            Antimatter.NETWORK.sendToServer(new GuiEventPacket(GuiEvent.EXTRA_SWITCH, container.getTile().getPos(), button.getId(), shiftHold, s ? 1 : 0));
        };
    }
}
