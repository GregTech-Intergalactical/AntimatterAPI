package muramasa.antimatter.network.packets;

import muramasa.antimatter.Antimatter;
import muramasa.antimatter.capability.ICapabilityHost;
import muramasa.antimatter.util.Utils;
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
    private final int dim;

    public CapabilityPacket(String cap, BlockPos pos, int dim) {
        this.cap = cap;
        this.pos = pos;
        this.dim = dim;
    }

    public static void encode(CapabilityPacket msg, PacketBuffer buf) {
        buf.writeString(msg.cap);
        buf.writeBlockPos(msg.pos);
        buf.writeVarInt(msg.dim);
    }

    public static CapabilityPacket decode(PacketBuffer buf) {
        return new CapabilityPacket(buf.readString(), buf.readBlockPos(), buf.readVarInt());
    }

    public static void handle(final CapabilityPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DimensionType dimensionType = DimensionType.getById(msg.dim);
            if (dimensionType != null) {
                ServerWorld world = ServerLifecycleHooks.getCurrentServer().getWorld(dimensionType);
                TileEntity tile = Utils.getTile(world, msg.pos);
                if (tile instanceof ICapabilityHost) {
                    Antimatter.NETWORK.sendTo(new CompoundPacket(((ICapabilityHost) tile).getCapabilityTag(msg.cap), msg.pos, msg.dim), ctx.get().getSender());
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
