//package muramasa.antimatter.network;
//
//import io.netty.buffer.ByteBuf;
//import muramasa.antimatter.util.SoundType;
//import net.minecraft.client.Minecraft;
//import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
//import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
//import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
//
//public class SoundMessage implements IMessage {
//
//    private int soundId;
//
//    public SoundMessage() {
//        //NOOP
//    }
//
//    public SoundMessage(int soundId) {
//        this.soundId = soundId;
//    }
//
//    @Override
//    public void toBytes(ByteBuf buf) {
//        buf.writeInt(soundId);
//    }
//
//    @Override
//    public void fromBytes(ByteBuf buf) {
//        soundId = buf.readInt();
//    }
//
//    public static class SoundMessageHandler implements IMessageHandler<SoundMessage, IMessage> {
//
//        @Override
//        public IMessage onMessage(SoundMessage message, MessageContext ctx) {
//            SoundType type = SoundType.get(message.soundId);
//            if (type != null) Minecraft.getMinecraft().player.playSound(type.getEvent(), type.getVolume(), type.getPitch());
//            return null;
//        }
//    }
//}
