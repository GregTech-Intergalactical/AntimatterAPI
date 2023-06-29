package muramasa.antimatter.network.packets;

import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import io.netty.buffer.ByteBuf;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.ICanSyncData;
import muramasa.antimatter.gui.container.AntimatterContainer;
import muramasa.antimatter.gui.container.IAntimatterContainer;
import muramasa.antimatter.network.AntimatterNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.List;

public class ServerboundGuiSyncPacket extends GuiSyncPacket<ServerboundGuiSyncPacket> {

    public static final ServerHandler HANDLER = new ServerHandler();
    public ServerboundGuiSyncPacket(List<GuiInstance.SyncHolder> data) {
        super(data);
    }

    public ServerboundGuiSyncPacket(ByteBuf data) {
        super(data);
    }

    @Override
    public ResourceLocation getID() {
        return AntimatterNetwork.GUI_SYNC_PACKET_ID_SERVERBOUND;
    }

    @Override
    public PacketHandler<ServerboundGuiSyncPacket> getHandler() {
        return HANDLER;
    }

    private static class ServerHandler implements PacketHandler<ServerboundGuiSyncPacket> {
        @Override
        public void encode(ServerboundGuiSyncPacket msg, FriendlyByteBuf buf) {
            buf.writeVarInt(msg.data.length);
            for (GuiInstance.SyncHolder data : msg.data) {
                buf.writeVarInt(data.index);
                data.writer.accept(buf, data.current);
            }
        }

        @Override
        public ServerboundGuiSyncPacket decode(FriendlyByteBuf buf) {
            return new ServerboundGuiSyncPacket(buf.copy());
        }

        @Override
        public PacketContext handle(ServerboundGuiSyncPacket msg) {
            return (sender, level) -> {
                ((AntimatterContainer) sender.containerMenu).handler.receivePacket(msg, ICanSyncData.SyncDirection.SERVER_TO_CLIENT);
            };
        }
    }
}
