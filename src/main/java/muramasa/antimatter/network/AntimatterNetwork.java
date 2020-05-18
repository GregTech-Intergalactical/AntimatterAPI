package muramasa.antimatter.network;

import muramasa.antimatter.Ref;
import muramasa.antimatter.network.packets.FluidStackPacket;
import muramasa.antimatter.network.packets.GuiEventPacket;
import muramasa.antimatter.network.packets.SoundPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

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
        handler.registerMessage(currMessageId++, GuiEventPacket.class, GuiEventPacket::encode, GuiEventPacket::decode, GuiEventPacket::handle);
        handler.registerMessage(currMessageId++, FluidStackPacket.class, FluidStackPacket::encode, FluidStackPacket::decode, FluidStackPacket::handle);
    }

    public void sendToServer(Object msg) {
        handler.sendToServer(msg);
    }

    public void sendTo(Object msg, ServerPlayerEntity player) {
        if (!(player instanceof FakePlayer)) handler.sendTo(msg, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
    }

    public void sendToAllAround(Object msg, World world, AxisAlignedBB alignedBB) {
        for (ServerPlayerEntity player : world.getEntitiesWithinAABB(ServerPlayerEntity.class, alignedBB)) {
            sendTo(msg, player);
        }
    }
}
