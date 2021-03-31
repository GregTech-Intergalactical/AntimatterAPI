package muramasa.antimatter.capability;

import muramasa.antimatter.gui.event.GuiEvent;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.network.packets.AbstractGuiEventPacket;

public interface IGuiHandler {

    default void onGuiEvent(IGuiEvent event, int... data) {
        //NOOP
    }

    /**
     * Creates a gui packet, depending on the type of gui handler.
     * @param data the input data.
     * @return a packet to send.
     */
    AbstractGuiEventPacket createGuiPacket(GuiEvent event, int... data);
}
