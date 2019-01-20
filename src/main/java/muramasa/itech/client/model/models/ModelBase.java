package muramasa.itech.client.model.models;

import com.google.common.collect.ImmutableMap;
import muramasa.itech.api.machines.objects.Tier;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class ModelBase implements IModel {

    public static final TRSRTransformation NORTH = TRSRTransformation.from(EnumFacing.SOUTH);
    public static final TRSRTransformation SOUTH = TRSRTransformation.from(EnumFacing.NORTH);
    public static final TRSRTransformation EAST = TRSRTransformation.from(EnumFacing.WEST);
    public static final TRSRTransformation WEST = TRSRTransformation.from(EnumFacing.EAST);
    public static final TRSRTransformation DOWN = TRSRTransformation.from(EnumFacing.UP);
    public static final TRSRTransformation UP = TRSRTransformation.from(EnumFacing.DOWN);

    public static final HashMap<String, ResourceLocation> baseTextures = new HashMap<>();

    public static IBakedModel error;

    private static IModelState modelState;
    private static VertexFormat vertexFormat;
    private static Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;

    static {
        for (Tier tier : Tier.getAllBasic()) {
            baseTextures.put(tier.getName(), tier.getBaseTexture());
        }
    }

    private String name;
    private List<ResourceLocation> textures = new LinkedList<>();

    public ModelBase(String name, Collection<ResourceLocation>... collections) {
        this.name = name;
        for (int i = 0; i < collections.length; i++) {
            textures.addAll(collections[i]);
        }
    }

    public IBakedModel bakeModel(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        return ModelLoaderRegistry.getMissingModel().bake(state, format, bakedTextureGetter);
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
        try {
            modelState = state;
            vertexFormat = format;
            bakedTextureGetter = textureGetter;
            error = ModelLoaderRegistry.getMissingModel().bake(state, format, bakedTextureGetter);
            return bakeModel(state, format, textureGetter);
        } catch (Exception e) {
            System.err.println(name + ".bake() failed due to exception:" + e);
            return ModelLoaderRegistry.getMissingModel().bake(state, format, textureGetter);
        }
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        return textures;
    }

    public IModel load(ModelResourceLocation loc) {
        try {
            return ModelLoaderRegistry.getModel(loc);
        } catch (Exception e) {
            System.err.println("ModelBase.load() failed due to exception: " + e);
            return ModelLoaderRegistry.getMissingModel();
        }
    }

    public IModel tex(IModel model, String[] elements, ResourceLocation[] textures) {
        for (int i = 0; i < elements.length; i++) {
            model = tex(model, elements[i], textures[i]);
        }
        return model;
    }

    public IModel tex(IModel model, String element, ResourceLocation texture) {
        return model.retexture(ImmutableMap.of(element, texture.toString()));
    }

    public IBakedModel texAndBake(IModel model, String[] elements, IModelState state, ResourceLocation[] textures) {
        return tex(model, elements, textures).bake(state, vertexFormat, bakedTextureGetter);
    }

    public IBakedModel texAndBake(IModel model, String[] elements, ResourceLocation[] textures) {
        return tex(model, elements, textures).bake(modelState, vertexFormat, bakedTextureGetter);
    }

    public IBakedModel texAndBake(IModel model, String element, IModelState state, ResourceLocation texture) {
        return tex(model, element, texture).bake(state, vertexFormat, bakedTextureGetter);
    }

    public IBakedModel texAndBake(IModel model, String element, ResourceLocation texture) {
        return tex(model, element, texture).bake(modelState, vertexFormat, bakedTextureGetter);
    }
}
