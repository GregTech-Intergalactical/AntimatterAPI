package muramasa.antimatter.network.packets;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import io.netty.buffer.ByteBuf;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.ICanSyncData;
import muramasa.antimatter.gui.container.AntimatterContainer;
import muramasa.antimatter.gui.container.IAntimatterContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.List;

public abstract class GuiSyncPacket implements Packet<GuiSyncPacket> {
    private GuiInstance.SyncHolder[] data;
    public ByteBuf clientData;

    public GuiSyncPacket(final List<GuiInstance.SyncHolder> data) {
        this.data = data.toArray(new GuiInstance.SyncHolder[0]);
    }

    public GuiSyncPacket(final ByteBuf data) {
        this.clientData = data;

    }

    public static GuiSyncPacket decode(FriendlyByteBuf buf) {
        return new GuiSyncPacket(buf.copy());
    }

    @Override
    public void handleServer(){
        AbstractContainerMenu c = Minecraft.getInstance().player.containerMenu;
        if (c instanceof IAntimatterContainer) {
            ((AntimatterContainer) c).handler.receivePacket(this, ICanSyncData.SyncDirection.CLIENT_TO_SERVER);
        }
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(data.length);
        for (GuiInstance.SyncHolder data : data) {
            buf.writeVarInt(data.index);
            data.writer.accept(buf, data.current);
        }
    }


    @Override
    public void handleClient(ServerPlayer sender){
        ((AntimatterContainer) sender.containerMenu).handler.receivePacket(this, ICanSyncData.SyncDirection.SERVER_TO_CLIENT);
    }

    static abstract class Handler implements PacketHandler<GuiSyncPacket> {
        @Override
        public void encode(GuiSyncPacket msg, FriendlyByteBuf buf) {
            buf.writeVarInt(msg.data.length);
            for (GuiInstance.SyncHolder data : msg.data) {
                buf.writeVarInt(data.index);
                data.writer.accept(buf, data.current);
            }
        }
    }
}
