package muramasa.antimatter.client.model;

import muramasa.antimatter.client.ModelBuilder;
import muramasa.gtu.Ref;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

public abstract class ModelBase implements IUnbakedModel {

    public ModelBase() {

    }

    @Override
    public Collection<ResourceLocation> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors) {
        return Collections.emptyList();
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return Collections.emptyList();
    }

    /** Model Helpers **/
    public static ResourceLocation mc(String path) {
        return new ResourceLocation(path);
    }

    public static ResourceLocation mod(String path) {
        return new ResourceLocation(Ref.MODID, path);
    }

    public static ModelBuilder load(ResourceLocation loc) {
        return new ModelBuilder().of(loc);
    }
}
