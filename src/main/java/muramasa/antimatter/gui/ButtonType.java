package muramasa.antimatter.gui;

import muramasa.antimatter.Antimatter;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.cover.CoverInstance;
import muramasa.antimatter.gui.event.GuiEvent;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.gui.widget.ButtonWidget;
import muramasa.antimatter.gui.widget.SwitchWidjet;
import muramasa.antimatter.network.packets.GuiEventPacket;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

public class ButtonType {

    public static ButtonType EMPTY_BODY = new ButtonType("empty_body", (l, t, h, i, r, b) -> new ButtonWidget(r, l + b.getX(), t + b.getY(), b.getW(), b.getH(), b.getBody(0), getPressable(h, i, b))); // Empty button with body
    public static ButtonType TEXT_ON_BODY = new ButtonType("text_body", (l, t, h, i, r, b) -> new ButtonWidget(r, l + b.getX(), t + b.getY(), b.getW(), b.getH(), b.getBody(0), b.getText(), getPressable(h, i, b))); // Button with body and text above
    public static ButtonType OVERLAY_ON_BODY = new ButtonType("over_body", (l, t, h, i, r, b) -> new ButtonWidget(r, l + b.getX(), t + b.getY(), b.getW(), b.getH(), b.getBody(0), b.getOverlay(0), getPressable(h, i, b))); // Button with body and overlay above
    public static ButtonType DOUBLE_SWITCH_BODY = new ButtonType("double_switch", (l, t, h, i, r, b) -> new SwitchWidjet(r, l + b.getX(), t + b.getY(), b.getW(), b.getH(), b.getBody(0), b.getBody(1), getSwitchable(h, i, b))); // Switch with two bodies which changes on the toggle
    public static ButtonType SINGLE_SWITCH_BODY = new ButtonType("single_switch", (l, t, h, i, r, b) -> new SwitchWidjet(r, l + b.getX(), t + b.getY(), b.getW(), b.getH(), b.getOverlay(0), getSwitchable(h, i, b))); // Switch with single body which change coloring on the toggle
    public static ButtonType TEXT_ON_SWITCH = new ButtonType("text_switch", (l, t, h, i, r, b) -> new SwitchWidjet(r, l + b.getX(), t + b.getY(), b.getW(), b.getH(), b.getOverlay(0), b.getText(), getSwitchable(h, i, b))); // Switch with single body which change coloring on the toggle and text above
    public static ButtonType OVERLAY_ON_SWITCH = new ButtonType("over_switch", (l, t, h, i, r, b) -> new SwitchWidjet(r, l + b.getX(), t + b.getY(), b.getW(), b.getH(), b.getBody(0), b.getOverlay(1), getSwitchable(h, i, b))); // Switch with body and overlay above
    public static ButtonType DOUBLE_OVERLAY_ON_BODY  = new ButtonType("2over_switch", (l, t, h, i, r, b) -> new SwitchWidjet(r, l + b.getX(), t + b.getY(), b.getW(), b.getH(), b.getBody(0), b.getOverlay(1), b.getOverlay(2), getSwitchable(h, i, b))); // Switch with body and overlay which switch on the toggle

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

        AbstractButton get(int l, int t, IGuiHandler handler, PlayerInventory inv, ResourceLocation res, ButtonData button);
    }
    
    private static ButtonWidget.IPressable getPressable(IGuiHandler handler, PlayerInventory inv, ButtonData button) {
        return b -> {
            int shiftHold = inv.player.isShiftKeyDown() ? 1 : 0;
            IGuiEvent event = button.getEvent();
            if (handler instanceof TileEntity) {
                handler.onGuiEvent(event, button.getId(), shiftHold);
                Antimatter.NETWORK.sendToServer(new GuiEventPacket((GuiEvent) event, ((TileEntity) handler).getPos(), button.getId(), shiftHold));
            } else if(handler instanceof CoverInstance) {
                int side = ((CoverInstance<?>) handler).getSide().getIndex();
                TileEntity tile = ((CoverInstance<?>) handler).getTile();
                if (tile instanceof IGuiHandler) {
                    ((IGuiHandler) tile).onGuiEvent(event, button.getId(), shiftHold, side);
                    Antimatter.NETWORK.sendToServer(new GuiEventPacket((GuiEvent) event, tile.getPos(), button.getId(), shiftHold, side));
                }
            }
        };
    }

    private static SwitchWidjet.ISwitchable getSwitchable(IGuiHandler handler, PlayerInventory inv, ButtonData button) {
        return (b, s) -> {
            int shiftHold = inv.player.isShiftKeyDown() ? 1 : 0;
            int switchOn = s ? 1 : 0;
            IGuiEvent event = button.getEvent();
            if (handler instanceof TileEntity) {
                handler.onGuiEvent(event, button.getId(), shiftHold, switchOn);
                Antimatter.NETWORK.sendToServer(new GuiEventPacket((GuiEvent) event, ((TileEntity) handler).getPos(), button.getId(), shiftHold, switchOn));
            } else if(handler instanceof CoverInstance) {
                int side = ((CoverInstance<?>) handler).getSide().getIndex();
                TileEntity tile = ((CoverInstance<?>) handler).getTile();
                if (tile instanceof IGuiHandler) {
                    ((IGuiHandler) tile).onGuiEvent(event, button.getId(), shiftHold, switchOn, side);
                    Antimatter.NETWORK.sendToServer(new GuiEventPacket((GuiEvent) event, tile.getPos(), button.getId(), shiftHold, switchOn, side));
                }
            }
        };
    }
}
