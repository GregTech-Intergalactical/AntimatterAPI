package muramasa.antimatter.network;

import com.teamresourceful.resourcefullib.common.networking.NetworkChannel;
import dev.architectury.injectables.annotations.ExpectPlatform;
import muramasa.antimatter.Ref;
import muramasa.antimatter.network.packets.CoverGuiEventPacket;
import muramasa.antimatter.network.packets.GuiSyncPacket;
import muramasa.antimatter.network.packets.IAntimatterPacket;
import muramasa.antimatter.network.packets.TileGuiEventPacket;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import trinsdar.networkapi.api.INetwork;
import trinsdar.networkapi.api.PacketRegistration;

public abstract class AntimatterNetwork {

    public static final NetworkChannel NETWORK = new NetworkChannel(Ref.ID, 0, "main");

    public static final ResourceLocation TILE_GUI_PACKET_ID = new ResourceLocation(Ref.ID, "tile_gui");
    public static final ResourceLocation COVER_GUI_PACKET_ID = new ResourceLocation(Ref.ID, "cover_gui");
    public static final ResourceLocation GUI_SYNC_PACKET_ID = new ResourceLocation(Ref.ID, "gui_sync_clientbound");

    public static final ResourceLocation GUI_SYNC_PACKET_ID_SERVERBOUND = new ResourceLocation(Ref.ID, "gui_sync_serverbound");

    public static void register(){
        PacketRegistration.registerPacket(TileGuiEventPacket.class, TILE_GUI_PACKET_ID, TileGuiEventPacket::decode, PacketRegistration.NetworkDirection.PLAY_TO_SERVER);
        PacketRegistration.registerPacket(CoverGuiEventPacket.class, COVER_GUI_PACKET_ID, CoverGuiEventPacket::decode, PacketRegistration.NetworkDirection.PLAY_TO_SERVER);
        PacketRegistration.registerPacket(GuiSyncPacket.class, GUI_SYNC_PACKET_ID, GuiSyncPacket::decode);
    }
}
