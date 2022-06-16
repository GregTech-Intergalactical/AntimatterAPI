package muramasa.antimatter.client;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class AntimatterTextureStitcher {

    public interface ITextureSticher {
        void stitch(Consumer<ResourceLocation> consumer);
    }

    final static Map<String, List<ITextureSticher>> STITCHERS = new Object2ObjectOpenHashMap<>();

    public static void addStitcher(ITextureSticher stitcher) {
        addStitcher(stitcher, "blocks");
    }

    public static void addStitcher(ITextureSticher stitcher, String name) {
        STITCHERS.compute(name, (a, b) -> {
            if (b == null) b = new ObjectArrayList<>();
            b.add(stitcher);
            return b;
        });
    }

    public static void onTextureStitch(TextureAtlas atlas, Consumer<ResourceLocation> consumer) {
        STITCHERS.forEach((k, v) -> {
            if (!atlas.location().getPath().contains(k)) return;
            v.forEach(t -> t.stitch(consumer));
        });
    }
}
