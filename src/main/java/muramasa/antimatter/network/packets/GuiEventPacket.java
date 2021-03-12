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

public class GuiEventPacket {

    private final GuiEvent event;
    private final BlockPos pos;
    private final int[] data;

    public GuiEventPacket(GuiEvent event, BlockPos pos, int... data) {
        this.event = event;
        this.pos = pos;
        this.data = data;
    }

    public static void encode(GuiEventPacket msg, PacketBuffer buf) {
        buf.writeEnumValue(msg.event);
        buf.writeBlockPos(msg.pos);
        buf.writeVarIntArray(msg.data);
    }

    public static GuiEventPacket decode(PacketBuffer buf) {
        return new GuiEventPacket(buf.readEnumValue(GuiEvent.class), buf.readBlockPos(), buf.readVarIntArray());
    }

    public static void handle(final GuiEventPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity sender =  ctx.get().getSender();
            if (sender != null) {
                TileEntity tile = Utils.getTile(sender.getServerWorld(), msg.pos);
                if (tile instanceof IGuiHandler) {
                    ((IGuiHandler) tile).onGuiEvent(msg.event, msg.data);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
