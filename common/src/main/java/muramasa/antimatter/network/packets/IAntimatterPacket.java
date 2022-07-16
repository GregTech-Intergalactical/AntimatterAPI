package muramasa.antimatter.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public interface IAntimatterPacket {
    void encode(FriendlyByteBuf buf);

    default void handleClient(ServerPlayer sender){}

    default void handleServer(){}
}
