package muramasa.antimatter.capability;

import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.network.packets.AbstractGuiEventPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.function.Consumer;

public interface IGuiHandler {

    default void onGuiEvent(IGuiEvent event, PlayerEntity player) {
        // NOOP
    }

    boolean isRemote();

    default void addWidgets(GuiInstance instance, IGuiElement parent) {
        if (this instanceof IHaveWidgets) {
            ((IHaveWidgets) this).getCallbacks().forEach(t -> t.accept(instance));
        }
    }

    ResourceLocation getGuiTexture();

    default int guiSize() {
        return 176;
    }

    default int guiHeight() {
        return 166;
    }

    /**
     * Creates a gui packet, depending on the type of gui handler.
     *
     * @param event the event container.
     * @return a packet to send.
     */
    AbstractGuiEventPacket createGuiPacket(IGuiEvent event);

    String handlerDomain();

    interface IHaveWidgets {
        List<Consumer<GuiInstance>> getCallbacks();

        default IHaveWidgets addGuiCallback(Consumer<GuiInstance> gui) {
            getCallbacks().add(gui);
            return this;
        }
    }
}
