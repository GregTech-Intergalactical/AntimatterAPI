package muramasa.antimatter.network.forge;

import muramasa.antimatter.Ref;
import muramasa.antimatter.network.AntimatterNetwork;
import muramasa.antimatter.network.packets.CoverGuiEventPacket;
import muramasa.antimatter.network.packets.GuiSyncPacket;
import muramasa.antimatter.network.packets.IAntimatterPacket;
import muramasa.antimatter.network.packets.TileGuiEventPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;

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
        handler.registerMessage(currMessageId++, TileGuiEventPacket.class, TileGuiEventPacket::encodeStatic, TileGuiEventPacket::decode, (msg, ctx) -> {
            ctx.get().enqueueWork(() -> {
                msg.handleClient(ctx.get().getSender());
            });
            ctx.get().setPacketHandled(true);
        });
        handler.registerMessage(currMessageId++, CoverGuiEventPacket.class, CoverGuiEventPacket::encodeStatic, CoverGuiEventPacket::decode, (msg, ctx) -> {
            ctx.get().enqueueWork(() -> {
                msg.handleClient(ctx.get().getSender());
            });
            ctx.get().setPacketHandled(true);
        });
        handler.registerMessage(currMessageId++, GuiSyncPacket.class, GuiSyncPacket::encode, GuiSyncPacket::decode, (msg, ctx) -> {
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                ctx.get().enqueueWork(msg::handleServer);
            } else {
                ctx.get().enqueueWork(() -> msg.handleClient(ctx.get().getSender()));
            }
            ctx.get().setPacketHandled(true);
        });
    }

    public <MSG> void registerMessage(Class<MSG> messageType, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer) {
        handler.registerMessage(currMessageId++, messageType, encoder, decoder, messageConsumer);
    }

    public void sendToServer(ResourceLocation id, IAntimatterPacket msg) {
        handler.sendToServer(msg);
    }

    public void sendToClient(ResourceLocation id, IAntimatterPacket msg, ServerPlayer player) {
        if (!(player instanceof FakePlayer))
            handler.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    protected MinecraftServer getCurrentServer(){
        return ServerLifecycleHooks.getCurrentServer();
    }
}
