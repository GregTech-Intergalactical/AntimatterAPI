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

public class ClientboundGuiSyncPacket extends GuiSyncPacket<ClientboundGuiSyncPacket>{

    public static final PacketHandler<ClientboundGuiSyncPacket> HANDLER = new ClientHandler();
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
    public PacketHandler<ClientboundGuiSyncPacket> getHandler() {
        return HANDLER;
    }

    private static class ClientHandler implements PacketHandler<ClientboundGuiSyncPacket> {
        @Override
        public void encode(ClientboundGuiSyncPacket msg, FriendlyByteBuf buf) {
            buf.writeVarInt(msg.data.length);
            for (GuiInstance.SyncHolder data : msg.data) {
                buf.writeVarInt(data.index);
                data.writer.accept(buf, data.current);
            }
        }


        @Override
        public ClientboundGuiSyncPacket decode(FriendlyByteBuf buf) {
            return new ClientboundGuiSyncPacket(buf.copy());
        }

        @Override
        public PacketContext handle(ClientboundGuiSyncPacket msg) {
            return (sender, level) -> {
                AbstractContainerMenu c = Minecraft.getInstance().player.containerMenu;
                if (c instanceof IAntimatterContainer) {
                    ((AntimatterContainer) c).handler.receivePacket(msg, ICanSyncData.SyncDirection.SERVER_TO_CLIENT);
                }
            };
        }
    }
}
