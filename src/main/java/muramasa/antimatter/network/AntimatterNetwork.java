package muramasa.antimatter.network;

import muramasa.antimatter.Ref;
import muramasa.antimatter.network.packets.CoverGuiEventPacket;
import muramasa.antimatter.network.packets.GuiSyncPacket;
import muramasa.antimatter.network.packets.TileGuiEventPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

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
        // handler.registerMessage(currMessageId++, SoundPacket.class, SoundPacket::encode, SoundPacket::decode, SoundPacket::handle);
        handler.registerMessage(currMessageId++, TileGuiEventPacket.class, TileGuiEventPacket::encode, TileGuiEventPacket::decode, TileGuiEventPacket::handle);
        handler.registerMessage(currMessageId++, CoverGuiEventPacket.class, CoverGuiEventPacket::encode, CoverGuiEventPacket::decode, CoverGuiEventPacket::handle);
        handler.registerMessage(currMessageId++, GuiSyncPacket.class, GuiSyncPacket::encode, GuiSyncPacket::decode, GuiSyncPacket::handle);
    }

    public void sendToServer(Object msg) {
        handler.sendToServer(msg);
    }

    public void sendTo(Object msg, ServerPlayerEntity player) {
        if (!(player instanceof FakePlayer))
            handler.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public void sendToAll(Object msg) {
        for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            sendTo(msg, player);
        }
    }

    public void sendToAllAround(Object msg, ServerWorld world, AxisAlignedBB alignedBB) {
        for (ServerPlayerEntity player : world.getEntitiesOfClass(ServerPlayerEntity.class, alignedBB)) {
            sendTo(msg, player);
        }
    }
}
