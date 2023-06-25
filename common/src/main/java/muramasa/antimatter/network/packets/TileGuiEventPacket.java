package muramasa.antimatter.network.packets;

import muramasa.antimatter.Antimatter;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.gui.container.IAntimatterContainer;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.network.AntimatterNetwork;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

public class TileGuiEventPacket extends AbstractGuiEventPacket {

    public TileGuiEventPacket(IGuiEvent event, BlockPos pos) {
        super(event, pos, AntimatterNetwork.TILE_GUI_PACKET_ID);
    }

    public static TileGuiEventPacket decode(FriendlyByteBuf buf) {
        return new TileGuiEventPacket(IGuiEvent.IGuiEventFactory.read(buf), buf.readBlockPos());
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        event.getFactory().write(event, buf);
        buf.writeBlockPos(pos);
    }

    @Override
    public void handleClient(ServerPlayer sender) {
        if (sender != null) {
            BlockEntity tile = Utils.getTile(sender.getLevel(), pos);
            if (tile instanceof IGuiHandler) {
                if (event.forward()) {
                    ((IGuiHandler) tile).onGuiEvent(event, sender);
                } else {
                    event.handle(sender, ((IAntimatterContainer) sender.containerMenu).source());
                }
            }
        }
    }

    @Override
    public void handleServer() {

    }
}
