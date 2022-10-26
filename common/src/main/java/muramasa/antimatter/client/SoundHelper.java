package muramasa.antimatter.client;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.machine.types.Machine;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import java.util.Map;


@Environment(EnvType.CLIENT)
public class SoundHelper {

    private static final Map<Level, Map<BlockPos, SoundInstance>> MACHINE_SOUNDS = new Object2ObjectOpenHashMap<>();

    public static void startLoop(Machine<?> machine, Level level, BlockPos pos) {
        Minecraft mc = Minecraft.getInstance();
        //double d0 = mc.gameRenderer.getMainCamera().getPosition().distanceToSqr(msg.pos.getX(), msg.pos.getY(), msg.pos.getZ());
        SimpleSoundInstance simplesoundinstance = new SimpleSoundInstance(machine.machineNoise.getLocation(), SoundSource.BLOCKS, machine.soundVolume,1.0f, true, 0, SoundInstance.Attenuation.LINEAR, pos.getX(), pos.getY(), pos.getZ(), false);
        mc.getSoundManager().play(simplesoundinstance);
        MACHINE_SOUNDS.computeIfAbsent(level, l -> new Object2ObjectOpenHashMap<>()).put(pos, simplesoundinstance);
    }

    public static void worldUnload(LevelAccessor world) {
        Map<BlockPos, SoundInstance> sounds = MACHINE_SOUNDS.remove(world);
        if (sounds != null) {
            for (SoundInstance value : sounds.values()) {
                Minecraft.getInstance().getSoundManager().stop(value);
            }
        }
    }

    public static void clear(Level level, BlockPos pos) {
        var map = MACHINE_SOUNDS.get(level);
        if (map == null) return;
        SoundInstance instance = map.remove(pos);
        if (instance != null) {
            Minecraft.getInstance().getSoundManager().stop(instance);
        }
    }
}
