package muramasa.antimatter.network.packets;

import muramasa.antimatter.Antimatter;
import muramasa.antimatter.capability.CapabilityType;
import muramasa.antimatter.capability.ICapabilityHost;
import muramasa.antimatter.util.Utils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CapabilityPacket {

    private CapabilityType cap;
    private final BlockPos pos;

    public CapabilityPacket(CapabilityType cap, BlockPos pos) {
        this.cap = cap;
        this.pos = pos;
    }

    public static void encode(CapabilityPacket msg, PacketBuffer buf) {
        buf.writeEnumValue(msg.cap);
        buf.writeBlockPos(msg.pos);
    }

    public static CapabilityPacket decode(PacketBuffer buf) {
        return new CapabilityPacket(buf.readEnumValue(CapabilityType.class), buf.readBlockPos());
    }

    public static void handle(final CapabilityPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity sender =  ctx.get().getSender();
            if (sender != null) {
                TileEntity tile = Utils.getTile(sender.getServerWorld(), msg.pos);
                if (tile instanceof ICapabilityHost) {
                    CompoundNBT tag = ((ICapabilityHost) tile).getCapabilityTag(msg.cap);
                    if (!tag.isEmpty()) {
                        Antimatter.NETWORK.sendTo(new CompoundPacket(tag, msg.pos), sender);
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
