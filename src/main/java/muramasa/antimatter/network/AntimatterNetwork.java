package muramasa.antimatter.network;

import muramasa.antimatter.Ref;
import muramasa.antimatter.network.packets.CoverGuiEventPacket;
import muramasa.antimatter.network.packets.GuiSyncPacket;
import muramasa.antimatter.network.packets.SoundPacket;
import muramasa.antimatter.network.packets.TileGuiEventPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;

public class AntimatterNetwork {

    private static final String MAIN_CHANNEL = "main_channel";
    private static final String PROTOCOL_VERSION = Integer.toString(1);

    private SimpleChannel handler;
    private int currMessageId;

    public AntimatterNetwork() {
        handler = NetworkRegistry.ChannelBuilder.
                named(new ResourceLocation(Ref.ID, MAIN_CHANNEL)).
                clientAcceptedVersions(PROTOCOL_VERSION::equals).
                serverAcceptedVersions(PROTOCOL_VERSION::equals).
                networkProtocolVersion(() -> PROTOCOL_VERSION).
                simpleChannel();
        register();
    }

    public void register() {
        handler.registerMessage(currMessageId++, SoundPacket.class, SoundPacket::encode, SoundPacket::decode, SoundPacket::handle);
        handler.registerMessage(currMessageId++, TileGuiEventPacket.class, TileGuiEventPacket::encode, TileGuiEventPacket::decode, TileGuiEventPacket::handle);
        handler.registerMessage(currMessageId++, CoverGuiEventPacket.class, CoverGuiEventPacket::encode, CoverGuiEventPacket::decode, CoverGuiEventPacket::handle);
        handler.registerMessage(currMessageId++, GuiSyncPacket.class, GuiSyncPacket::encode, GuiSyncPacket::decode, GuiSyncPacket::handle);
    }

    public void sendToServer(Object msg) {
        handler.sendToServer(msg);
    }

    public void sendTo(Object msg, ServerPlayer player) {
        if (!(player instanceof FakePlayer))
            handler.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public void sendToAll(Object msg) {
        for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            sendTo(msg, player);
        }
    }

    public void sendToAllAround(Object msg, ServerLevel world, AABB alignedBB) {
        for (ServerPlayer player : world.getEntitiesOfClass(ServerPlayer.class, alignedBB)) {
            sendTo(msg, player);
        }
    }
}
