package muramasa.gtu.api.registration;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;

import java.util.Map;
import java.util.Set;

public interface IModelOverride {

    @OnlyIn(Dist.CLIENT)
    default void onModelRegistration() {
        //NOOP
    }

    @OnlyIn(Dist.CLIENT)
    default void getTextures(Set<ResourceLocation> textures) {
        //NOOP
    }

    @OnlyIn(Dist.CLIENT)
    default void onModelBake(ModelBakeEvent e, Map<ResourceLocation, IBakedModel> registry) {
        //NOOP
    }
}
