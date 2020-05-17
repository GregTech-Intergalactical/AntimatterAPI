package muramasa.antimatter.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SoundPacket {

    private final SoundEvent event;
    private final float volume;
    private final float pitch;

    public SoundPacket(SoundEvent event, float volume, float pitch) {
        this.event = event;
        this.volume = volume;
        this.pitch = pitch;
    }

    public static void encode(SoundPacket msg, PacketBuffer buf) {
        buf.writeRegistryId(msg.event);
        buf.writeFloat(msg.volume);
        buf.writeFloat(msg.pitch);
    }

    public static SoundPacket decode(PacketBuffer buf) {
        return new SoundPacket(buf.readRegistryId(), buf.readFloat(), buf.readFloat());
    }

    public static void handle(final SoundPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            PlayerEntity player = Minecraft.getInstance().player;
            if (player != null) player.playSound(msg.event, msg.volume, msg.pitch);
        });
        ctx.get().setPacketHandled(true);
    }
}
