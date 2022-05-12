package muramasa.antimatter.network.packets;

import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.gui.container.IAntimatterContainer;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class TileGuiEventPacket extends AbstractGuiEventPacket {

    public TileGuiEventPacket(IGuiEvent event, BlockPos pos) {
        super(event, pos);
    }

    public static void encode(TileGuiEventPacket msg, FriendlyByteBuf buf) {
        msg.event.getFactory().write(msg.event, buf);
        buf.writeBlockPos(msg.pos);
    }

    public static TileGuiEventPacket decode(FriendlyByteBuf buf) {
        return new TileGuiEventPacket(IGuiEvent.IGuiEventFactory.read(buf), buf.readBlockPos());
    }

    public static void handle(final TileGuiEventPacket msg, @Nonnull Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            if (sender != null) {
                BlockEntity tile = Utils.getTile(sender.getLevel(), msg.pos);
                if (tile instanceof IGuiHandler) {
                    if (msg.event.forward()) {
                        ((IGuiHandler) tile).onGuiEvent(msg.event, sender);
                    } else {
                        msg.event.handle(sender, ((IAntimatterContainer) sender.containerMenu).source());
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
