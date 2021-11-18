package muramasa.antimatter.network.packets;

import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.gui.container.IAntimatterContainer;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.util.Utils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class TileGuiEventPacket extends AbstractGuiEventPacket {

    public TileGuiEventPacket(IGuiEvent event, BlockPos pos) {
        super(event, pos);
    }

    public static void encode(TileGuiEventPacket msg, PacketBuffer buf) {
        msg.event.getFactory().write(msg.event, buf);
        buf.writeBlockPos(msg.pos);
    }

    public static TileGuiEventPacket decode(PacketBuffer buf) {
        return new TileGuiEventPacket(IGuiEvent.IGuiEventFactory.read(buf), buf.readBlockPos());
    }

    public static void handle(final TileGuiEventPacket msg, @Nonnull Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity sender = ctx.get().getSender();
            if (sender != null) {
                TileEntity tile = Utils.getTile(sender.getLevel(), msg.pos);
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
