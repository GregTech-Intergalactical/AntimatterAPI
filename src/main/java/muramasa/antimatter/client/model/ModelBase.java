package muramasa.antimatter.client.model;

import muramasa.antimatter.client.ModelBuilder;
import muramasa.gtu.Ref;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

public abstract class ModelBase implements IUnbakedModel {

    public ModelBase() {

    }

    @Nullable
    public IBakedModel bakeModel(ModelBakery bakery, Function<ResourceLocation, TextureAtlasSprite> getter, ISprite sprite, VertexFormat format) {
        return null;
    }

    @Nullable
    @Override
    public IBakedModel bake(ModelBakery bakery, Function<ResourceLocation, TextureAtlasSprite> getter, ISprite sprite, VertexFormat format) {
        try {
            return bakeModel(bakery, getter, sprite, format);
        } catch (Exception e) {
            System.err.println("ModelBase.bake() failed due to " + e + ":");
            e.printStackTrace();
            return null;
        }
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
