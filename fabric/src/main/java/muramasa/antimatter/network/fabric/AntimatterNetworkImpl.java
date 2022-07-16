package muramasa.antimatter.network.fabric;

import io.github.fabricators_of_create.porting_lib.util.NetworkDirection;
import io.github.fabricators_of_create.porting_lib.util.ServerLifecycleHooks;
import me.pepperbell.simplenetworking.SimpleChannel;
import muramasa.antimatter.Ref;
import muramasa.antimatter.network.AntimatterNetwork;
import muramasa.antimatter.network.packets.CoverGuiEventPacket;
import muramasa.antimatter.network.packets.GuiSyncPacket;
import muramasa.antimatter.network.packets.TileGuiEventPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class AntimatterNetworkImpl extends AntimatterNetwork {
    private SimpleChannel handler;
    private int currMessageId;

    public AntimatterNetworkImpl() {
        handler = NetworkRegistry.ChannelBuilder.
                named(new ResourceLocation(Ref.ID, MAIN_CHANNEL)).
                clientAcceptedVersions(PROTOCOL_VERSION::equals).
                serverAcceptedVersions(PROTOCOL_VERSION::equals).
                networkProtocolVersion(() -> PROTOCOL_VERSION).
                simpleChannel();
        register();
    }

    public static AntimatterNetwork createAntimatterNetwork(){
        return new AntimatterNetworkImpl();
    }

    public void register() {
        handler.registerMessage(currMessageId++, TileGuiEventPacket.class, TileGuiEventPacket::encode, TileGuiEventPacket::decode, (msg, ctx) -> {
            ctx.get().enqueueWork(() -> {
                TileGuiEventPacket.handle(msg, ctx.get().getSender());
            });
            ctx.get().setPacketHandled(true);
        });
        handler.registerMessage(currMessageId++, CoverGuiEventPacket.class, CoverGuiEventPacket::encode, CoverGuiEventPacket::decode, (msg, ctx) -> {
            ctx.get().enqueueWork(() -> {
                CoverGuiEventPacket.handle(msg, ctx.get().getSender());
            });
            ctx.get().setPacketHandled(true);
        });
        handler.registerMessage(currMessageId++, GuiSyncPacket.class, GuiSyncPacket::encode, GuiSyncPacket::decode, (msg, ctx) -> {
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                ctx.get().enqueueWork(() -> {
                    GuiSyncPacket.handleServer(msg);
                });
            } else {
                ctx.get().enqueueWork(() -> GuiSyncPacket.handleClient(msg, ctx.get().getSender()));
            }
            ctx.get().setPacketHandled(true);
        });
    }

    public void sendToServer(Object msg) {
        handler.sendToServer(msg);
    }

    public void sendTo(Object msg, ServerPlayer player) {
        if (!(player instanceof FakePlayer))
            handler.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    protected MinecraftServer getCurrentServer(){
        return ServerLifecycleHooks.getCurrentServer();
    }
}
