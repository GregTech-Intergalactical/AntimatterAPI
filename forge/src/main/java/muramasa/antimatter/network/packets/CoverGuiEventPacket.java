package muramasa.antimatter.network.packets;

import muramasa.antimatter.gui.container.IAntimatterContainer;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CoverGuiEventPacket extends AbstractGuiEventPacket {
    Direction facing;

    public CoverGuiEventPacket(IGuiEvent event, BlockPos pos, Direction facing) {
        super(event, pos);
        this.facing = facing;
    }

    public static void encode(CoverGuiEventPacket msg, FriendlyByteBuf buf) {
        msg.event.getFactory().write(msg.event, buf);
        buf.writeBlockPos(msg.pos);
        buf.writeEnum(msg.facing);
    }

    public static CoverGuiEventPacket decode(FriendlyByteBuf buf) {
        return new CoverGuiEventPacket(IGuiEvent.IGuiEventFactory.read(buf), buf.readBlockPos(), buf.readEnum(Direction.class));
    }

    public static void handle(final CoverGuiEventPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            if (sender != null) {
                BlockEntity tile = Utils.getTile(sender.getLevel(), msg.pos);
                if (tile instanceof TileEntityMachine) {
                    if (msg.event.forward()) {
                        ((TileEntityMachine<?>) tile).coverHandler.ifPresent(ch -> ch.get(msg.facing).onGuiEvent(msg.event, sender));
                    } else {
                        msg.event.handle(sender, ((IAntimatterContainer) sender.containerMenu).source());
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
