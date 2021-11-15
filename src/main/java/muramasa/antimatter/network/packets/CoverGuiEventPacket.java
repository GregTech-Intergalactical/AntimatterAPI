package muramasa.antimatter.network.packets;

import muramasa.antimatter.gui.container.IAntimatterContainer;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CoverGuiEventPacket extends AbstractGuiEventPacket {
    Direction facing;

    public CoverGuiEventPacket(IGuiEvent event, BlockPos pos, Direction facing) {
        super(event, pos);
        this.facing = facing;
    }

    public static void encode(CoverGuiEventPacket msg, PacketBuffer buf) {
        msg.event.getFactory().write(msg.event, buf);
        buf.writeBlockPos(msg.pos);
        buf.writeEnumValue(msg.facing);
    }

    public static CoverGuiEventPacket decode(PacketBuffer buf) {
        return new CoverGuiEventPacket(IGuiEvent.IGuiEventFactory.read(buf), buf.readBlockPos(), buf.readEnumValue(Direction.class));
    }

    public static void handle(final CoverGuiEventPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity sender = ctx.get().getSender();
            if (sender != null) {
                TileEntity tile = Utils.getTile(sender.getServerWorld(), msg.pos);
                if (tile instanceof TileEntityMachine) {
                    if (msg.event.forward()) {
                        ((TileEntityMachine<?>) tile).coverHandler.ifPresent(ch -> ch.get(msg.facing).onGuiEvent(msg.event, sender));
                    } else {
                        msg.event.handle(sender, ((IAntimatterContainer) sender.openContainer).source());
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
