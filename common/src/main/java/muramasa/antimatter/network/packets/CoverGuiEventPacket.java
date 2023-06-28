package muramasa.antimatter.network.packets;

import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.gui.container.IAntimatterContainer;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.network.AntimatterNetwork;
import muramasa.antimatter.util.AntimatterCapUtils;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Optional;

public class CoverGuiEventPacket extends AbstractGuiEventPacket<CoverGuiEventPacket> {
    public static final Handler HANDLER = new Handler();
    Direction facing;

    public CoverGuiEventPacket(IGuiEvent event, BlockPos pos, Direction facing) {
        super(event, pos, AntimatterNetwork.COVER_GUI_PACKET_ID);
        this.facing = facing;
    }

    @Override
    public PacketHandler<CoverGuiEventPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<CoverGuiEventPacket> {

        @Override
        public void encode(CoverGuiEventPacket msg, FriendlyByteBuf buf) {
            msg.event.getFactory().write(msg.event, buf);
            buf.writeBlockPos(msg.pos);
            buf.writeEnum(msg.facing);
        }

        @Override
        public CoverGuiEventPacket decode(FriendlyByteBuf buf) {
            return new CoverGuiEventPacket(IGuiEvent.IGuiEventFactory.read(buf), buf.readBlockPos(), buf.readEnum(Direction.class));
        }

        @Override
        public PacketContext handle(CoverGuiEventPacket msg) {
            return (sender, level) -> {
                if (sender != null) {
                    BlockEntity tile = Utils.getTile(sender.getLevel(), msg.pos);
                    if (tile == null) throw new RuntimeException("Somehow you got an incorrect packet, CoverGuiEventPacket::handleClient missing Entity!");
                    Optional<ICoverHandler<?>> coverHandler = AntimatterCapUtils.getCoverHandler(tile, msg.facing);
                    if (msg.event.forward()) {
                        coverHandler.ifPresent(ch -> ch.get(msg.facing).onGuiEvent(msg.event, sender));
                    } else {
                        msg.event.handle(sender, ((IAntimatterContainer) sender.containerMenu).source());
                    }
                }
            };
        }
    }
}
