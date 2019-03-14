package muramasa.gregtech.api.util;

import muramasa.gregtech.Ref;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public enum Sounds {

    //TODO get GT sound category

    WRENCH(Ref.MODID, "wrench"),
    DRILL(Ref.MODID, "drill"),
    BREAK("minecraft", "entity.item.break"),
    PLACE_METAL("minecraft", "block.metal.place"),
    BUCKET_EMPTY("minecraft", "item.bucket.empty"),
    HAMMER("minecraft", "block.anvil.place", 1.0f, 0.75f);

    private SoundEvent event;
    private float volume = 1.0f, pitch = 1.0f;

    Sounds(String domain, String path) {
        event = new SoundEvent(new ResourceLocation(domain, path));
    }

    Sounds(String domain, String path, float volume, float pitch) {
        this(domain, path);
        this.volume = volume;
        this.pitch = pitch;
    }

    public void play(World world, BlockPos pos) {
        playSound(world, pos, event, volume, pitch);
    }

    public void play(World world, BlockPos pos, float volume, float pitch) {
        playSound(world, pos, event, volume, pitch);
    }

    /** Plays sound to all nearby players with the given volume and pitch, including the player who caused the sound **/
    public void playSound(World world, BlockPos pos, SoundEvent event, float volume, float pith) {
        world.playSound(null, pos, event, SoundCategory.AMBIENT, volume, pith);
    }

    public void playSound(World world, EntityPlayer player, BlockPos pos, SoundEvent event, float volume, float pitch) {
        world.playSound(player, pos, event, SoundCategory.AMBIENT, volume, pitch);
    }
}
