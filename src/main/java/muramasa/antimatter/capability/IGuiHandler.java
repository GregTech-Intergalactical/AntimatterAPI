package muramasa.antimatter.capability;

import muramasa.antimatter.Ref;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.event.GuiEvent;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.network.packets.AbstractGuiEventPacket;
import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;

public interface IGuiHandler {

    default void onGuiEvent(IGuiEvent event, PlayerEntity player, int... data) {
        //NOOP
    }

    default String getDomain(){
        return Ref.ID;
    }

    GuiData getStatic();

    boolean isRemote();

    default void addWidgets(GuiInstance instance) {

    }

    /**
     * Creates a gui packet, depending on the type of gui handler.
     * @param data the input data.
     * @return a packet to send.
     */
    AbstractGuiEventPacket createGuiPacket(IGuiEvent event, int... data);
}
