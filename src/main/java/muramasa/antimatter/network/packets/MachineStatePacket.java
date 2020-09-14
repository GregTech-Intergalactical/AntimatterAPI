package muramasa.antimatter.network.packets;

import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MachineStatePacket {

    private final MachineState state;
    private final BlockPos pos;

    public MachineStatePacket(MachineState state, BlockPos pos) {
        this.state = state;
        this.pos = pos;
    }

    public static void encode(MachineStatePacket msg, PacketBuffer buf) {
        buf.writeEnumValue(msg.state);
        buf.writeBlockPos(msg.pos);
    }

    public static MachineStatePacket decode(PacketBuffer buf) {
        return new MachineStatePacket(buf.readEnumValue(MachineState.class), buf.readBlockPos());
    }

    public static void handle(final MachineStatePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientWorld world = Minecraft.getInstance().world;
            TileEntity tile = Utils.getTile(world, msg.pos);
            if (tile instanceof TileEntityMachine) {
                ((TileEntityMachine) tile).setMachineState(msg.state);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
