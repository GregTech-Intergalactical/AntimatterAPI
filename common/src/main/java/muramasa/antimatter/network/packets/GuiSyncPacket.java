package muramasa.antimatter.network.packets;

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

    public static void encode(GuiSyncPacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.data.length);
        for (GuiInstance.SyncHolder data : msg.data) {
            buf.writeVarInt(data.index);
            data.writer.accept(buf, data.current);
        }
    }

    public static GuiSyncPacket decode(FriendlyByteBuf buf) {
        return new GuiSyncPacket(buf.copy());
    }

    public static void handleServer(GuiSyncPacket msg){
        AbstractContainerMenu c = Minecraft.getInstance().player.containerMenu;
        if (c instanceof IAntimatterContainer) {
            ((AntimatterContainer) c).handler.receivePacket(msg, ICanSyncData.SyncDirection.CLIENT_TO_SERVER);
        }
    }

    public static void handleClient(GuiSyncPacket msg, ServerPlayer sender){
        ((AntimatterContainer) sender.containerMenu).handler.receivePacket(msg, ICanSyncData.SyncDirection.SERVER_TO_CLIENT);
    }
}
