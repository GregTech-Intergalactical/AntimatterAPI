package muramasa.antimatter.network.packets;

import io.netty.buffer.ByteBuf;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.ICanSyncData;
import muramasa.antimatter.gui.container.AntimatterContainer;
import muramasa.antimatter.gui.container.IAntimatterContainer;
import muramasa.antimatter.gui.screen.AntimatterContainerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class GuiSyncPacket {
    private GuiInstance.SyncHolder[] data;
    public ByteBuf clientData;

    public GuiSyncPacket(final List<GuiInstance.SyncHolder> data) {
        this.data = data.toArray(new GuiInstance.SyncHolder[0]);
    }

    public GuiSyncPacket(final ByteBuf data) {
        this.clientData = data;

    }

    public static void encode(GuiSyncPacket msg, PacketBuffer buf) {
        buf.writeVarInt(msg.data.length);
        for (GuiInstance.SyncHolder data : msg.data) {
            buf.writeVarInt(data.index);
            data.writer.accept(buf, data.current);
        }
    }

    public static GuiSyncPacket decode(PacketBuffer buf) {
        return new GuiSyncPacket(buf.copy());
    }

    public static void handle(final GuiSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            ctx.get().enqueueWork(() -> {
                Container c = Minecraft.getInstance().player.openContainer;
                if (c instanceof IAntimatterContainer) {
                    ((AntimatterContainer) c).handler.receivePacket(msg, ICanSyncData.SyncDirection.CLIENT_TO_SERVER);
                }
            });
        } else {
            ctx.get().enqueueWork(() -> ((AntimatterContainer)ctx.get().getSender().openContainer).handler.receivePacket(msg, ICanSyncData.SyncDirection.SERVER_TO_CLIENT));
        }
        ctx.get().setPacketHandled(true);
    }
}
