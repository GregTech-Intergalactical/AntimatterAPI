package muramasa.antimatter.network.packets;

import muramasa.antimatter.gui.event.GuiEvent;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.util.Utils;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.function.Supplier;

public class GuiEventPacket {

    private final GuiEvent event;
    private final BlockPos pos;
    private final int dim;
    private final int[] data;

    public GuiEventPacket(GuiEvent event, BlockPos pos, int dim, int... data) {
        this.event = event;
        this.pos = pos;
        this.dim = dim;
        this.data = data;
    }

    public static void encode(GuiEventPacket msg, PacketBuffer buf) {
        buf.writeEnumValue(msg.event);
        buf.writeBlockPos(msg.pos);
        buf.writeVarInt(msg.dim);
        buf.writeVarIntArray(msg.data);
    }

    public static GuiEventPacket decode(PacketBuffer buf) {
        return new GuiEventPacket(buf.readEnumValue(GuiEvent.class), buf.readBlockPos(), buf.readVarInt(), buf.readVarIntArray());
    }

    public static void handle(final GuiEventPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DimensionType dimensionType = DimensionType.getById(msg.dim);
            if (dimensionType != null) {
                World world = ServerLifecycleHooks.getCurrentServer().getWorld(dimensionType);
                TileEntity tile = Utils.getTile(world, msg.pos);
                if (tile instanceof IGuiHandler) {
                    ((IGuiHandler) tile).onGuiEvent(msg.event, msg.data);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
