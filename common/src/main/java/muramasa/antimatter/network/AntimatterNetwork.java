package muramasa.antimatter.network;

import dev.architectury.injectables.annotations.ExpectPlatform;
import muramasa.antimatter.Ref;
import muramasa.antimatter.network.packets.CoverGuiEventPacket;
import muramasa.antimatter.network.packets.GuiSyncPacket;
import muramasa.antimatter.network.packets.TileGuiEventPacket;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;

public abstract class AntimatterNetwork {

    protected static final String MAIN_CHANNEL = "main_channel";
    protected static final String PROTOCOL_VERSION = Integer.toString(1);

    @ExpectPlatform
    public static AntimatterNetwork createAntimatterNetwork(){
        return null;
    }

    public abstract void sendToServer(Object msg);

    public abstract void sendTo(Object msg, ServerPlayer player);

    public void sendToAll(Object msg) {
        for (ServerPlayer player : getCurrentServer().getPlayerList().getPlayers()) {
            sendTo(msg, player);
        }
    }

    protected abstract MinecraftServer getCurrentServer();

    public void sendToAllAround(Object msg, ServerLevel world, AABB alignedBB) {
        for (ServerPlayer player : world.getEntitiesOfClass(ServerPlayer.class, alignedBB)) {
            sendTo(msg, player);
        }
    }
}
