package muramasa.antimatter.network.fabric;

import io.github.fabricators_of_create.porting_lib.util.ServerLifecycleHooks;
import muramasa.antimatter.network.AntimatterNetwork;
import muramasa.antimatter.network.packets.CoverGuiEventPacket;
import muramasa.antimatter.network.packets.GuiSyncPacket;
import muramasa.antimatter.network.packets.IAntimatterPacket;
import muramasa.antimatter.network.packets.TileGuiEventPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class AntimatterNetworkImpl extends AntimatterNetwork {

    public AntimatterNetworkImpl() {
        register();
    }

    public static AntimatterNetwork createAntimatterNetwork(){
        return new AntimatterNetworkImpl();
    }

    public void register() {
        ServerPlayNetworking.registerGlobalReceiver(AntimatterNetwork.TILE_GUI_PACKET_ID, ((server, player, handler, buf, responseSender) -> {
            TileGuiEventPacket packet = TileGuiEventPacket.decode(buf);
            server.execute(() -> {
                packet.handleClient(player);
            });
        }));
        ServerPlayNetworking.registerGlobalReceiver(AntimatterNetwork.COVER_GUI_PACKET_ID, ((server, player, handler, buf, responseSender) -> {
            CoverGuiEventPacket packet = CoverGuiEventPacket.decode(buf);
            server.execute(() -> {
                packet.handleClient(player);
            });
        }));
        ServerPlayNetworking.registerGlobalReceiver(AntimatterNetwork.GUI_SYNC_PACKET_ID, ((server, player, handler, buf, responseSender) -> {
            GuiSyncPacket packet = GuiSyncPacket.decode(buf);
            server.execute(() -> packet.handleClient(player));
        }));
        ClientPlayNetworking.registerGlobalReceiver(AntimatterNetwork.GUI_SYNC_PACKET_ID, ((client, handler, buf, responseSender) -> {
            GuiSyncPacket packet = GuiSyncPacket.decode(buf);
            client.execute(packet::handleServer);
        }));
    }

    public void sendToServer(ResourceLocation id, IAntimatterPacket msg) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        msg.encode(buf);
        ClientPlayNetworking.send(id, buf);
    }

    public void sendToClient(ResourceLocation id, IAntimatterPacket msg, ServerPlayer player) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        msg.encode(buf);
        ServerPlayNetworking.send(player, id, buf);
    }

    protected MinecraftServer getCurrentServer(){
        return ServerLifecycleHooks.getCurrentServer();
    }
}
