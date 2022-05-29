package muramasa.antimatter.network.forge;

import muramasa.antimatter.Ref;
import muramasa.antimatter.network.AntimatterNetwork;
import muramasa.antimatter.network.packets.CoverGuiEventPacket;
import muramasa.antimatter.network.packets.GuiSyncPacket;
import muramasa.antimatter.network.packets.TileGuiEventPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;

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
