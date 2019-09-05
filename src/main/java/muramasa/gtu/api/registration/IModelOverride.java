package muramasa.gtu.api.registration;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Set;

public interface IModelOverride {

    @SideOnly(Side.CLIENT)
    default void onModelRegistration() {
        //NOOP
    }

    @SideOnly(Side.CLIENT)
    default void getTextures(Set<ResourceLocation> textures) {
        //NOOP
    }

    @SideOnly(Side.CLIENT)
    default void onModelBake(IRegistry<ModelResourceLocation, IBakedModel> registry) {
        //NOOP
    }
}
