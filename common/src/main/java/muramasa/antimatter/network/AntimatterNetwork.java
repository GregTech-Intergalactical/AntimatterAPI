package muramasa.antimatter.network;

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

public abstract class AntimatterNetwork {

    protected static final String MAIN_CHANNEL = "main_channel";

    public static final ResourceLocation TILE_GUI_PACKET_ID = new ResourceLocation(Ref.ID, "tile_gui_packet");
    public static final ResourceLocation COVER_GUI_PACKET_ID = new ResourceLocation(Ref.ID, "cover_gui_packet");
    public static final ResourceLocation GUI_SYNC_PACKET_ID = new ResourceLocation(Ref.ID, "gui_sync_packet");

    protected static final String PROTOCOL_VERSION = Integer.toString(1);

    @ExpectPlatform
    public static AntimatterNetwork createAntimatterNetwork(){
        return null;
    }

    public abstract void sendToServer(ResourceLocation id, IAntimatterPacket msg);

    public abstract void sendToClient(ResourceLocation id, IAntimatterPacket msg, ServerPlayer player);

    public void sendToAll(ResourceLocation id, IAntimatterPacket msg) {
        for (ServerPlayer player : getCurrentServer().getPlayerList().getPlayers()) {
            sendToClient(id, msg, player);
        }
    }

    protected abstract MinecraftServer getCurrentServer();

    public void sendToAllAround(ResourceLocation id, IAntimatterPacket msg, ServerLevel world, AABB alignedBB) {
        for (ServerPlayer player : world.getEntitiesOfClass(ServerPlayer.class, alignedBB)) {
            sendToClient(id, msg, player);
        }
    }
}
