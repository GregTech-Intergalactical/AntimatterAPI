package muramasa.antimatter.network.packets;

import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SoundPacket(SoundEvent event, BlockPos pos, float volume,
                          float pitch, boolean loop) {

    public static void encode(SoundPacket msg, FriendlyByteBuf buf) {
        buf.writeRegistryId(msg.event);
        buf.writeBlockPos(msg.pos);
        buf.writeFloat(msg.volume);
        buf.writeFloat(msg.pitch);
        buf.writeBoolean(msg.loop);
    }

    public static SoundPacket decode(FriendlyByteBuf buf) {
        return new SoundPacket(buf.readRegistryId(), buf.readBlockPos(), buf.readFloat(), buf.readFloat(), buf.readBoolean());
    }

    public static void handle(final SoundPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> clientHandle(msg));
        ctx.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void clientHandle(SoundPacket msg) {
        Minecraft mc = Minecraft.getInstance();
        Level level = Minecraft.getInstance().level;
        //double d0 = mc.gameRenderer.getMainCamera().getPosition().distanceToSqr(msg.pos.getX(), msg.pos.getY(), msg.pos.getZ());
        SimpleSoundInstance simplesoundinstance = new SimpleSoundInstance(msg.event.getLocation(), SoundSource.BLOCKS, msg.volume, msg.pitch,msg.loop, 0, SoundInstance.Attenuation.LINEAR, msg.pos.getX(), msg.pos.getY(), msg.pos.getZ(), false);
        mc.getSoundManager().play(simplesoundinstance);
        if (level.getBlockEntity(msg.pos) instanceof TileEntityMachine<?> mach) {
            mach.playingSound = simplesoundinstance;
        }

    }

    public static void clear(SoundInstance instance) {
        Minecraft.getInstance().getSoundManager().stop(instance);
    }
}
