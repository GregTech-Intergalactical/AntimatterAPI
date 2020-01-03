//package muramasa.antimatter.network;
//
//import io.netty.buffer.ByteBuf;
//import muramasa.gtu.api.guiold.GuiEvent;
//import net.minecraft.tileentity.TileEntity;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.World;
//import net.minecraftforge.fml.common.FMLCommonHandler;
//import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
//import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
//import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
//
//public class GuiEventMessage implements IMessage {
//
//    private GuiEvent event;
//    private BlockPos pos;
//    private int dimension;
//
//    public GuiEventMessage() {
//        //NOOP
//    }
//
//    public GuiEventMessage(GuiEvent event, BlockPos pos, int dimension) {
//        this.event = event;
//        this.pos = pos;
//        this.dimension = dimension;
//    }
//
//    @Override
//    public void toBytes(ByteBuf buf) {
//        buf.writeInt(event.ordinal());
//        buf.writeInt(pos.getX());
//        buf.writeInt(pos.getY());
//        buf.writeInt(pos.getZ());
//        buf.writeInt(dimension);
//    }
//
//    @Override
//    public void fromBytes(ByteBuf buf) {
//        event = GuiEvent.values()[buf.readInt()];
//        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
//        dimension = buf.readInt();
//    }
//
//    public static class SoundMessageHandler implements IMessageHandler<GuiEventMessage, IMessage> {
//
//        @Override
//        public IMessage onMessage(GuiEventMessage message, MessageContext ctx) {
//            World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(message.dimension);
//
//
//
//            TileEntity tile = world.getTileEntity(message.pos);
//            if (tile instanceof TileEntityMachine) {
//                ((TileEntityMachine) tile).onGuiEvent(message.event);
//            }
//            return null;
//        }
//    }
//}
