package muramasa.itech.client.model.models;

import com.google.common.collect.ImmutableMap;
import muramasa.itech.client.model.bakedmodels.BakedModelBase;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.Collection;
import java.util.HashMap;
import java.util.function.Function;

public class ModelBase implements IModel {

    public static final TRSRTransformation NORTH = TRSRTransformation.from(EnumFacing.SOUTH);
    public static final TRSRTransformation SOUTH = TRSRTransformation.from(EnumFacing.NORTH);
    public static final TRSRTransformation EAST = TRSRTransformation.from(EnumFacing.WEST);
    public static final TRSRTransformation WEST = TRSRTransformation.from(EnumFacing.EAST);
    public static final TRSRTransformation DOWN = TRSRTransformation.from(EnumFacing.UP);
    public static final TRSRTransformation UP = TRSRTransformation.from(EnumFacing.DOWN);

    public static IBakedModel missingModelBaked;

    private static IModelState modelState;
    private static VertexFormat vertexFormat;
    private static Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;

    private static HashMap<String, IBakedModel[]> bakedModelLookup = new HashMap<>();
    private static HashMap<String, ResourceLocation> textureLookup = new HashMap<>();

    private String name;

    public ModelBase(String name) {
        this.name = name;
    }

    public IBakedModel bakeModel(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        return missingModelBaked;
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
        try {
            modelState = state;
            vertexFormat = format;
            bakedTextureGetter = textureGetter;
            missingModelBaked = ModelLoaderRegistry.getMissingModel().bake(state, format, bakedTextureGetter);
            return bakeModel(state, format, textureGetter);
        } catch (Exception e) {
            System.err.println(name + ".bake() failed due to exception:" + e);
            e.printStackTrace();
            return ModelLoaderRegistry.getMissingModel().bake(state, format, textureGetter);
        }
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        return textureLookup.values();
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

    public static IModel tex(IModel model, String[] elements, ResourceLocation[] textures) {
        for (int i = 0; i < elements.length; i++) {
            model = tex(model, elements[i], textures[i]);
        }
        return model;
    }

    public static IModel tex(IModel model, String element, ResourceLocation texture) {
        try {
            return model.retexture(ImmutableMap.of(element, texture.toString()));
        } catch (Exception e) {
            System.err.println("ModelBase.tex() failed due to " + e + ":");
            e.printStackTrace();
            return ModelLoaderRegistry.getMissingModel();
        }
    }

    public static int temp() {
        return bakedModelLookup.size();
    }

    public static IBakedModel texAndBake(IModel model, String[] elements, IModelState state, ResourceLocation[] textures) {
        return new BakedModelBase(tex(model, elements, textures).bake(state, vertexFormat, bakedTextureGetter));
    }

    public static IBakedModel texAndBake(IModel model, String[] elements, ResourceLocation[] textures) {
        return new BakedModelBase(tex(model, elements, textures).bake(modelState, vertexFormat, bakedTextureGetter));
    }

    public static IBakedModel texAndBake(IModel model, String element, IModelState state, ResourceLocation texture) {
        return new BakedModelBase(tex(model, element, texture).bake(state, vertexFormat, bakedTextureGetter));
    }

    public static IBakedModel texAndBake(IModel model, String element, ResourceLocation texture) {
        return new BakedModelBase(tex(model, element, texture).bake(modelState, vertexFormat, bakedTextureGetter));
    }

    public static IBakedModel[] getBaked(String prefix, String modelName) {
        return bakedModelLookup.get(prefix + modelName);
    }

    public static void addBaked(String prefix, String modelName, IBakedModel[] models) {
        bakedModelLookup.put(prefix + modelName, models);
    }

    public static void addBaked(String prefix, String modelName, IBakedModel model) {
        bakedModelLookup.put(prefix + modelName, new IBakedModel[]{model});
    }

    public static ResourceLocation getTexture(String prefix, IStringSerializable serializable) {
        return getTexture(prefix, serializable.getName());
    }

    public static ResourceLocation getTexture(String prefix, String textureName) {
        return textureLookup.get(prefix + textureName);
    }

    public static void addTexture(String prefix, String textureName, ResourceLocation texture) {
        textureLookup.put(prefix + textureName, texture);
    }

    public static void addTextures(String prefix, IStringSerializable[] textureNames, ResourceLocation[] textures) {
        for (int i = 0; i < textureNames.length; i++) {
            textureLookup.put(prefix + textureNames[i].getName(), textures[i]);
        }
    }
}
