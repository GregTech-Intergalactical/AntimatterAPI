package muramasa.antimatter.network.packets;

import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.gui.event.GuiEvent;
import muramasa.antimatter.util.Utils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class TileGuiEventPacket extends AbstractGuiEventPacket {

    public TileGuiEventPacket(GuiEvent event, BlockPos pos, int... data) {
        super(event, pos, data);
    }

    public static void encode(TileGuiEventPacket msg, PacketBuffer buf) {
        buf.writeEnumValue(msg.event);
        buf.writeBlockPos(msg.pos);
        buf.writeVarIntArray(msg.data);
    }

    public static TileGuiEventPacket decode(PacketBuffer buf) {
        return new TileGuiEventPacket(buf.readEnumValue(GuiEvent.class), buf.readBlockPos(), buf.readVarIntArray());
    }

    public static void handle(final TileGuiEventPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity sender =  ctx.get().getSender();
            if (sender != null) {
                TileEntity tile = Utils.getTile(sender.getServerWorld(), msg.pos);
                if (tile instanceof IGuiHandler) {
                    ((IGuiHandler) tile).onGuiEvent(msg.event, sender, msg.data);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
