package muramasa.gtu.api.registration;

import muramasa.gtu.proxy.providers.GregTechItemModelProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.generators.ItemModelBuilder;

public interface IModelProvider {

    @OnlyIn(Dist.CLIENT)
    default void onItemModelBuild(GregTechItemModelProvider provider, ItemModelBuilder builder) {
        //NOOP
    }

    @OnlyIn(Dist.CLIENT)
    default void onBlockModelBuild() {
        //NOOP
    }
}
