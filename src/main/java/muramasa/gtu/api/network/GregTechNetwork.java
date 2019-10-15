package muramasa.gtu.api.network;

import muramasa.gtu.Ref;
import muramasa.gtu.api.network.packets.GuiEventPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class GregTechNetwork {

    private static final String MAIN_CHANNEL = "main_channel";
    private static final String PROTOCOL_VERSION = Integer.toString(1);

    private SimpleChannel handler;
    private int currMessageId;

    public GregTechNetwork() {
        handler = NetworkRegistry.ChannelBuilder.
            named(new ResourceLocation(Ref.MODID, MAIN_CHANNEL)).
            clientAcceptedVersions(PROTOCOL_VERSION::equals).
            serverAcceptedVersions(PROTOCOL_VERSION::equals).
            networkProtocolVersion(() -> PROTOCOL_VERSION).
            simpleChannel();
    }

    public void register() {
        handler.registerMessage(currMessageId++, GuiEventPacket.class, GuiEventPacket::encode, GuiEventPacket::decode, GuiEventPacket::handle);
    }

    public void sendToServer(Object msg) {
        handler.sendToServer(msg);
    }

    public void sendTo(Object msg, ServerPlayerEntity player) {
        if (!(player instanceof FakePlayer)) handler.sendTo(msg, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
    }

    /*
    public static void init() {
        NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(Ref.MODID);
        NETWORK.registerMessage(SoundMessage.SoundMessageHandler.class, SoundMessage.class, NetworkEvent.SOUND.ordinal(), Side.CLIENT);
        NETWORK.registerMessage(FluidStackMessage.FluidStackMessageHandler.class, FluidStackMessage.class, NetworkEvent.FLUID.ordinal(), Side.CLIENT);
        NETWORK.registerMessage(GuiEventMessage.SoundMessageHandler.class, GuiEventMessage.class, NetworkEvent.GUI.ordinal(), Side.SERVER);
    }

    public static void playSoundOnClient(SoundType type) {
        GregTech.PROXY.playSound(type);
    }

    public static void syncMachineTanks(TileEntityMachine tile) {
        if (tile == null) return;
        tile.fluidHandler.ifPresent(h -> {
            NETWORK.sendToAllTracking(new FluidStackMessage(h.getInputsRaw(), h.getOutputsRaw()), new NetworkRegistry.TargetPoint(tile.getWorld().provider.getDimension(), tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), 1));
        });
    }

    public static void sendGuiEvent(GuiEvent event, TileEntityMachine tile) {
        NETWORK.sendToServer(new GuiEventMessage(event, tile.getPos(), tile.getWorld().provider.getDimension()));
    }
    */
}
