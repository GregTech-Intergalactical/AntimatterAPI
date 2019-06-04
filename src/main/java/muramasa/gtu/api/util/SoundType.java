package muramasa.gtu.api.util;

import muramasa.gtu.Ref;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;

public class SoundType {

    //TODO get GT sound category

    private static HashMap<Integer, SoundType> LOOKUP = new HashMap<>();

    public static SoundType WRENCH = new SoundType(Ref.MODID, "wrench");
    public static SoundType DRILL = new SoundType(Ref.MODID, "drill");
    public static SoundType BREAK = new SoundType("minecraft", "entity.item.break");
    public static SoundType PLACE_METAL = new SoundType("minecraft", "block.metal.place");
    public static SoundType BUCKET_EMPTY = new SoundType("minecraft", "item.bucket.empty");
    public static SoundType HAMMER = new SoundType("minecraft", "block.anvil.place", 1.0f, 0.75f);

    private static int lastInternalID = 0;

    private SoundEvent event;
    private float volume = 1.0f, pitch = 1.0f;
    private int internalId;

    public SoundType(String domain, String path) {
        event = new SoundEvent(new ResourceLocation(domain, path));
        internalId = lastInternalID++;
        LOOKUP.put(internalId, this);
    }

    public SoundType(String domain, String path, float volume, float pitch) {
        this(domain, path);
        this.volume = volume;
        this.pitch = pitch;
    }

    public SoundEvent getEvent() {
        return event;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }

    public int getInternalId() {
        return internalId;
    }

    public void play(World world, BlockPos pos) {
        play(world, pos, volume, pitch);
    }

    public void play(World world, BlockPos pos, float volume, float pitch) {
        world.playSound(null, pos, event, SoundCategory.AMBIENT, volume, pitch);
    }

    public static SoundType get(int id) {
        return LOOKUP.get(id);
    }
}
