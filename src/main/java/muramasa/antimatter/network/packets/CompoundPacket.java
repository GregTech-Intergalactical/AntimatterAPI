package muramasa.antimatter.network.packets;

import muramasa.antimatter.capability.ICapabilityHost;
import muramasa.antimatter.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CompoundPacket {

    private final CompoundNBT tag;
    private final BlockPos pos;

    public CompoundPacket(CompoundNBT tag, BlockPos pos) {
        this.tag = tag;
        this.pos = pos;
    }

    public static void encode(CompoundPacket msg, PacketBuffer buf) {
        buf.writeCompoundTag(msg.tag);
        buf.writeBlockPos(msg.pos);
    }

    public static CompoundPacket decode(PacketBuffer buf) {
        return new CompoundPacket(buf.readCompoundTag(), buf.readBlockPos());
    }

    public static void handle(final CompoundPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientWorld world = Minecraft.getInstance().world;
            TileEntity tile = Utils.getTile(world, msg.pos);
            if (tile instanceof ICapabilityHost) {
                ((ICapabilityHost) tile).update(msg.tag);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
