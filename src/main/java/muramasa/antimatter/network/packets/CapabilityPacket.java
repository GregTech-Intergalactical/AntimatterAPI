package muramasa.antimatter.network.packets;

import muramasa.antimatter.Antimatter;
import muramasa.antimatter.capability.ICapabilityHost;
import muramasa.antimatter.util.Utils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.function.Supplier;

public class CapabilityPacket {

    private String cap;
    private final BlockPos pos;

    public CapabilityPacket(String cap, BlockPos pos) {
        this.cap = cap;
        this.pos = pos;
    }

    public static void encode(CapabilityPacket msg, PacketBuffer buf) {
        buf.writeString(msg.cap);
        buf.writeBlockPos(msg.pos);
    }

    public static CapabilityPacket decode(PacketBuffer buf) {
        return new CapabilityPacket(buf.readString(), buf.readBlockPos());
    }

    public static void handle(final CapabilityPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity sender =  ctx.get().getSender();
            if (sender != null) {
                TileEntity tile = Utils.getTile(sender.getServerWorld(), msg.pos);
                if (tile instanceof ICapabilityHost) {
                    Antimatter.NETWORK.sendTo(new CompoundPacket(((ICapabilityHost) tile).getCapabilityTag(msg.cap), msg.pos), sender);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
