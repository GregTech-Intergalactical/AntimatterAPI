package muramasa.antimatter.client;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.cover.CoverNone;
import muramasa.antimatter.cover.ICover;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static muramasa.antimatter.Data.COVERNONE;

@Mod.EventBusSubscriber
public class AntimatterTextureStitcher {

    public interface ITextureSticher {
        void stitch(Consumer<ResourceLocation> consumer);
    }

    final static Map<String, List<ITextureSticher>> STITCHERS = new Object2ObjectOpenHashMap<>();

    public static void addStitcher(ITextureSticher stitcher) {
        addStitcher(stitcher, "blocks");
    }

    public static void addStitcher(ITextureSticher stitcher, String name) {
        STITCHERS.compute(name, (a,b) -> {
            if (b == null) b = new ObjectArrayList<>();
            b.add(stitcher);
            return b;
        });
    }

    public static void onTextureStitch(final TextureStitchEvent.Pre event) {
        STITCHERS.forEach((k,v) -> {
            if (!event.getMap().getTextureLocation().getPath().contains(k)) return;
            v.forEach(t -> t.stitch(event::addSprite));
        });
    }
}
