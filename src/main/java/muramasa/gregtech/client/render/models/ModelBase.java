package muramasa.gregtech.client.render.models;

import com.google.common.collect.ImmutableMap;
import muramasa.gregtech.Ref;
import muramasa.gregtech.api.texture.Texture;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;

import java.util.function.Function;

//TODO remove
public class ModelBase implements IModel {

    public static IBakedModel MISSING, BAKED_BASIC;

    private IModelState state;
    private VertexFormat format;
    private Function<ResourceLocation, TextureAtlasSprite> textureGetter;

    public IBakedModel bakeModel(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> getter) {
        return ModelLoaderRegistry.getMissingModel().bake(state, format, getter);
    }

    @Override
    public IBakedModel bake(IModelState modelState, VertexFormat vertexFormat, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        try {
            state = modelState;
            format = vertexFormat;
            textureGetter = bakedTextureGetter;
            if (MISSING == null) MISSING = ModelLoaderRegistry.getMissingModel().bake(modelState, vertexFormat, bakedTextureGetter);
            if (BAKED_BASIC == null) BAKED_BASIC = load("basic").bake(modelState, vertexFormat, bakedTextureGetter);
            return bakeModel(modelState, vertexFormat, bakedTextureGetter);
        } catch (Exception e) {
            System.err.println("ModelBase.bake failed due to: " + e);
            e.printStackTrace();
            return ModelLoaderRegistry.getMissingModel().bake(state, format, bakedTextureGetter);
        }
    }

    public IBakedModel texAndBake(IModel model, String[] elements, Texture[] textures) {
        return tex(model, elements, textures).bake(state, format, textureGetter);
    }

    public IBakedModel texAndBake(IModel model, String element, Texture texture) {
        return tex(model, element, texture).bake(state, format, textureGetter);
    }

    public static IModel load(String path) {
        return load(new ModelResourceLocation(Ref.MODID + ":" + path));
    }

    public static IModel load(String domain, String path) {
        return load(new ModelResourceLocation(domain + ":" + path));
    }

    public static IModel load(ModelResourceLocation loc) {
        try {
            return ModelLoaderRegistry.getModel(loc);
        } catch (Exception e) {
            System.err.println("ModelBase.load() failed due to " + e + ":");
            e.printStackTrace();
            return ModelLoaderRegistry.getMissingModel();
        }
    }

    public static IModel tex(IModel model, String[] elements, Texture[] textures) {
        for (int i = 0; i < elements.length; i++) {
            model = tex(model, elements[i], textures[i]);
        }
        return model;
    }

    public static IModel tex(IModel model, String element, Texture texture) {
        return tex(model, element, texture.getLoc());
    }

    public static IModel tex(IModel model, String element, ResourceLocation loc) {
        try {
            return model.retexture(ImmutableMap.of(element, loc.toString()));
        } catch (Exception e) {
            System.err.println("ModelBase.tex() failed due to " + e + ":");
            e.printStackTrace();
            return model;
        }
    }
}
