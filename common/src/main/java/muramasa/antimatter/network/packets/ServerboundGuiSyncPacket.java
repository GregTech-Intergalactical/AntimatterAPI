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

public class ServerboundGuiSyncPacket extends GuiSyncPacket{

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
    public PacketHandler<GuiSyncPacket> getHandler() {
        return HANDLER;
    }

    private static class ServerHandler extends Handler {

        @Override
        public GuiSyncPacket decode(FriendlyByteBuf buf) {
            return new ServerboundGuiSyncPacket(buf.copy());
        }

        @Override
        public PacketContext handle(GuiSyncPacket msg) {
            return (sender, level) -> {
                AbstractContainerMenu c = Minecraft.getInstance().player.containerMenu;
                if (c instanceof IAntimatterContainer) {
                    ((AntimatterContainer) c).handler.receivePacket(msg, ICanSyncData.SyncDirection.CLIENT_TO_SERVER);
                }
            };
        }
    }
}
