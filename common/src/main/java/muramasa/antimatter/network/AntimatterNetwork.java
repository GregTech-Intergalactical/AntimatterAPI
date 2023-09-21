package muramasa.antimatter.network;

import com.teamresourceful.resourcefullib.common.networking.NetworkChannel;
import com.teamresourceful.resourcefullib.common.networking.base.NetworkDirection;
import muramasa.antimatter.Ref;
import muramasa.antimatter.network.packets.*;
import net.minecraft.resources.ResourceLocation;

public abstract class AntimatterNetwork {

    public static final NetworkChannel NETWORK = new NetworkChannel(Ref.ID, 0, "main");

    public static final ResourceLocation TILE_GUI_PACKET_ID = new ResourceLocation(Ref.ID, "tile_gui");
    public static final ResourceLocation COVER_GUI_PACKET_ID = new ResourceLocation(Ref.ID, "cover_gui");
    public static final ResourceLocation GUI_SYNC_PACKET_ID = new ResourceLocation(Ref.ID, "gui_sync_clientbound");
    public static final ResourceLocation FAKE_TILE_PACKET_ID = new ResourceLocation(Ref.ID, "fake_tile");

    public static final ResourceLocation GUI_SYNC_PACKET_ID_SERVERBOUND = new ResourceLocation(Ref.ID, "gui_sync_serverbound");

    public static void register(){
        NETWORK.registerPacket(NetworkDirection.CLIENT_TO_SERVER, TILE_GUI_PACKET_ID, TileGuiEventPacket.HANDLER, TileGuiEventPacket.class);
        NETWORK.registerPacket(NetworkDirection.CLIENT_TO_SERVER, COVER_GUI_PACKET_ID, CoverGuiEventPacket.HANDLER, CoverGuiEventPacket.class);
        NETWORK.registerPacket(NetworkDirection.CLIENT_TO_SERVER, GUI_SYNC_PACKET_ID_SERVERBOUND, ServerboundGuiSyncPacket.HANDLER, ServerboundGuiSyncPacket.class);
        NETWORK.registerPacket(NetworkDirection.SERVER_TO_CLIENT, GUI_SYNC_PACKET_ID, ClientboundGuiSyncPacket.HANDLER, ClientboundGuiSyncPacket.class);
        NETWORK.registerPacket(NetworkDirection.SERVER_TO_CLIENT, FAKE_TILE_PACKET_ID, FakeTilePacket.HANDLER, FakeTilePacket.class);
    }
}
