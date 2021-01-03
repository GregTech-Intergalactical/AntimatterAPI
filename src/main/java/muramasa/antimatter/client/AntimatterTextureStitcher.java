package muramasa.antimatter.client;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.cover.Cover;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.function.Consumer;

@Mod.EventBusSubscriber
public class AntimatterTextureStitcher {

    public interface ITextureSticher {
        void stitch(Consumer<ResourceLocation> consumer);
    }

    final static List<ITextureSticher> STITCHERS = new ObjectArrayList<>();

    public static void addStitcher(ITextureSticher stitcher) {
        STITCHERS.add(stitcher);
    }

    public static void onTextureStitch(final TextureStitchEvent.Pre event) {
        AntimatterAPI.all(Cover.class).forEach(cover -> {
            if (!event.getMap().getTextureLocation().getPath().contains("blocks")) return;
            if (cover.isEmpty()) return;
            for (ResourceLocation r : cover.getTextures()) {
                event.addSprite(r);
            }
        });
        STITCHERS.forEach(t -> t.stitch(event::addSprite));
    }
}
