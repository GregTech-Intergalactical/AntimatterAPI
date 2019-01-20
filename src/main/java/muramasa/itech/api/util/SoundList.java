package muramasa.itech.api.util;

import muramasa.itech.ITech;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public enum SoundList {

    WRENCH(ITech.MODID + ":" + "wrench"),
    DRILL(ITech.MODID + ":" + "drill"),
    BREAK("entity.item.break"),
    PLACE_METAL("block.metal.place");

    private SoundEvent event;

    SoundList(String loc) {
        event = new SoundEvent(new ResourceLocation(loc));
    }

    public void play(World world, BlockPos pos) {
        playSound(world, pos, event, 1.0f, 1.0f);
    }

    public void play(World world, BlockPos pos, float volume, float pitch) {
        playSound(world, pos, event, volume, pitch);
    }

    /** Plays sound to all nearby players with the given volume and pitch, including the player who caused the sound **/
    private static void playSound(World world, BlockPos pos, SoundEvent soundEvent, float volume, float pith) {
        world.playSound(null, pos, soundEvent, SoundCategory.AMBIENT, volume, pith);
    }
}
