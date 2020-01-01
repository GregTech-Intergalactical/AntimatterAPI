package muramasa.gtu.api.registration;

import muramasa.gtu.data.providers.GregTechBlockStateProvider;
import muramasa.gtu.data.providers.GregTechItemModelProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IModelProvider {

    @OnlyIn(Dist.CLIENT)
    default void onItemModelBuild(GregTechItemModelProvider provider) {
        //NOOP
    }

    @OnlyIn(Dist.CLIENT)
    default void onBlockModelBuild(GregTechBlockStateProvider provider) {
        //NOOP
    }
}
