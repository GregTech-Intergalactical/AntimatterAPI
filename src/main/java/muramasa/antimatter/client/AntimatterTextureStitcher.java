package muramasa.antimatter.client;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.cover.Cover;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class AntimatterTextureStitcher {

    public static void onTextureStitch(final TextureStitchEvent.Pre event) {
        AntimatterAPI.all(Cover.class).forEach(cover -> {
            if (!event.getMap().getTextureLocation().getPath().contains("blocks")) return;
            if (cover.isEmpty()) return;
            for (ResourceLocation r : cover.getTextures()) {
                event.addSprite(r);
            }
        });
    }
}
