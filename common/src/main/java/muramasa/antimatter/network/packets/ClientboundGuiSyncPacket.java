package muramasa.antimatter.network.packets;

import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import io.netty.buffer.ByteBuf;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.ICanSyncData;
import muramasa.antimatter.gui.container.AntimatterContainer;
import muramasa.antimatter.network.AntimatterNetwork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class ClientboundGuiSyncPacket extends GuiSyncPacket{

    public static final ClientHandler HANDLER = new ClientHandler();
    public ClientboundGuiSyncPacket(List<GuiInstance.SyncHolder> data) {
        super(data);
    }

    public ClientboundGuiSyncPacket(ByteBuf data) {
        super(data);
    }

    @Override
    public ResourceLocation getID() {
        return AntimatterNetwork.GUI_SYNC_PACKET_ID;
    }

    @Override
    public PacketHandler<GuiSyncPacket> getHandler() {
        return HANDLER;
    }

    private static class ClientHandler extends Handler {

        @Override
        public GuiSyncPacket decode(FriendlyByteBuf buf) {
            return new ClientboundGuiSyncPacket(buf.copy());
        }

        @Override
        public PacketContext handle(GuiSyncPacket msg) {
            return (sender, level) -> {
                ((AntimatterContainer) sender.containerMenu).handler.receivePacket(msg, ICanSyncData.SyncDirection.SERVER_TO_CLIENT);
            };
        }
    }
}
