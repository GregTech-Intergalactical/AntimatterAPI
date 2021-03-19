package muramasa.antimatter.client;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.cover.BaseCover;
import muramasa.antimatter.cover.CoverNone;
import muramasa.antimatter.cover.ICover;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.function.Consumer;

import static muramasa.antimatter.Data.COVERNONE;

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
        AntimatterAPI.all(ICover.class).forEach(cover -> {
            if (!event.getMap().getTextureLocation().getPath().contains("blocks")) return;
            if (cover instanceof CoverNone || cover == COVERNONE) return;
            for (ResourceLocation r : cover.getTextures()) {
                event.addSprite(r);
            }
        });
        STITCHERS.forEach(t -> t.stitch(event::addSprite));
    }
}
