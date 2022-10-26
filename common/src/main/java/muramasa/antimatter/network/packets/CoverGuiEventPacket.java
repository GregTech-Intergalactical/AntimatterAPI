package muramasa.antimatter.network.packets;

import muramasa.antimatter.gui.container.IAntimatterContainer;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.network.AntimatterNetwork;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CoverGuiEventPacket extends AbstractGuiEventPacket {
    Direction facing;

    public CoverGuiEventPacket(IGuiEvent event, BlockPos pos, Direction facing) {
        super(event, pos, AntimatterNetwork.COVER_GUI_PACKET_ID);
        this.facing = facing;
    }

    public static void encodeStatic(CoverGuiEventPacket msg, FriendlyByteBuf buf) {
        msg.event.getFactory().write(msg.event, buf);
        buf.writeBlockPos(msg.pos);
        buf.writeEnum(msg.facing);
    }

    public static CoverGuiEventPacket decode(FriendlyByteBuf buf) {
        return new CoverGuiEventPacket(IGuiEvent.IGuiEventFactory.read(buf), buf.readBlockPos(), buf.readEnum(Direction.class));
    }

    public static void handle(final CoverGuiEventPacket msg, ServerPlayer sender){
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
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        encodeStatic(this, buf);
    }

    @Override
    public void handleClient(ServerPlayer sender) {
        if (sender != null) {
            BlockEntity tile = Utils.getTile(sender.getLevel(), this.pos);
            if (tile instanceof TileEntityMachine) {
                if (this.event.forward()) {
                    ((TileEntityMachine<?>) tile).coverHandler.ifPresent(ch -> ch.get(this.facing).onGuiEvent(this.event, sender));
                } else {
                    this.event.handle(sender, ((IAntimatterContainer) sender.containerMenu).source());
                }
            }
        }
    }
}
