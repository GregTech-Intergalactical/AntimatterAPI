package muramasa.antimatter.network.packets;

import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.gui.event.GuiEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CoverGuiEventPacket extends AbstractGuiEventPacket{
    Direction facing;
    public CoverGuiEventPacket(GuiEvent event, BlockPos pos, Direction facing, int... data) {
        super(event, pos, data);
        this.facing = facing;
    }

    public static void encode(CoverGuiEventPacket msg, PacketBuffer buf) {
        buf.writeEnumValue(msg.event);
        buf.writeBlockPos(msg.pos);
        buf.writeEnumValue(msg.facing);
        buf.writeVarIntArray(msg.data);
    }

    public static CoverGuiEventPacket decode(PacketBuffer buf) {
        return new CoverGuiEventPacket(buf.readEnumValue(GuiEvent.class), buf.readBlockPos(), buf.readEnumValue(Direction.class), buf.readVarIntArray());
    }

    public static void handle(final CoverGuiEventPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity sender =  ctx.get().getSender();
            if (sender != null) {
                TileEntity tile = Utils.getTile(sender.getServerWorld(), msg.pos);
                if (tile instanceof TileEntityMachine) {
                    ((TileEntityMachine) tile).getCover(msg.facing).onGuiEvent(msg.event, msg.data);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
