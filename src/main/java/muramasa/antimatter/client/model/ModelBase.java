package muramasa.antimatter.client.model;

import muramasa.antimatter.client.ModelBuilder;
import muramasa.antimatter.client.ModelUtils;
import muramasa.antimatter.texture.Texture;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public class ModelBase implements IUnbakedModel {

    protected Function<ModelBuilder, ModelBuilder> baseBuilder;
    protected Set<ResourceLocation> allTextures = new HashSet<>();
    protected ResourceLocation particle;

    public ModelBase(Texture... textures) {
        Texture[] defaultTextures = textures.length > 0 ? textures : new Texture[]{ModelUtils.ERROR};
        allTextures.addAll(Arrays.asList(defaultTextures)); //If supplied textures are not already loaded by a model
        particle = defaultTextures[0];
        baseBuilder = b -> b.simple().tex("all", defaultTextures[0]);
    }

    public ModelBase(Function<ModelBuilder, ModelBuilder> builder, Texture... textures) {
        this(textures);
        baseBuilder = builder;
    }

    @Nullable
    public IBakedModel bakeModel(ModelBakery bakery, Function<ResourceLocation, TextureAtlasSprite> getter, ISprite sprite, VertexFormat format) {
        ModelBuilder builder = baseBuilder.apply(new ModelBuilder());
        allTextures.addAll(builder.getTextures());
        return builder.bake(bakery, getter, sprite, format);
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
        return allTextures;
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return Collections.emptyList();
    }
}
