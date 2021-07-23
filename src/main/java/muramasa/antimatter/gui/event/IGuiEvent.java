package muramasa.antimatter.gui.event;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.registration.IAntimatterObject;
import net.minecraft.network.PacketBuffer;

public interface IGuiEvent extends IMachineEvent, IAntimatterObject {
    default void register() {
        AntimatterAPI.register(IGuiEvent.class, this);
    }
    default void write(PacketBuffer buffer) {
        buffer.writeString(this.getId());
    }
    static IGuiEvent read(PacketBuffer buffer) {
        return AntimatterAPI.get(IGuiEvent.class, buffer.readString());
    }
}
