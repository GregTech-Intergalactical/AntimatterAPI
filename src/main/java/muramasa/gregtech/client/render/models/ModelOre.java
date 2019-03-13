package muramasa.gregtech.client.render.models;

import com.google.common.collect.ImmutableMap;
import muramasa.gregtech.api.materials.MaterialSet;
import muramasa.gregtech.api.materials.Prefix;
import muramasa.gregtech.client.render.bakedmodels.BakedModelOre;
import muramasa.gregtech.Ref;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

public class ModelOre implements IModel {

    private static IBakedModel[] bakedModels;

    public ModelOre() {

    }

    @Override
    public IBakedModel bake(IModelState modelState, VertexFormat vertexFormat, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        if (bakedModels != null) return new BakedModelOre();
        try {
            IModel model = ModelLoaderRegistry.getModel(new ModelResourceLocation(Ref.MODID + ":block_ore_base"));
//            IBakedModel bakedModel = /*ModelBase.tex(model, "base", new ResourceLocation(ITech.MODID, "blocks/stone"))*/model.bake(modelState, vertexFormat, bakedTextureGetter);

            bakedModels = new IBakedModel[MaterialSet.values().length];
            for (MaterialSet set : MaterialSet.values()) {
                bakedModels[set.ordinal()] = model.retexture(ImmutableMap.of("overlay", set.getBlockTexture(Prefix.Ore).toString())).bake(modelState, vertexFormat, bakedTextureGetter);
//                bakedModels[set.ordinal()] = ModelBase.tex(model, "overlay", set.getOreLoc()).bake(modelState, vertexFormat, bakedTextureGetter);
            }

            return new BakedModelOre(bakedModels);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ModelLoaderRegistry.getMissingModel().bake(modelState, vertexFormat, bakedTextureGetter);
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        ArrayList<ResourceLocation> textures = new ArrayList<>();
        for (MaterialSet set : MaterialSet.values()) {
            textures.add(set.getBlockTexture(Prefix.Ore).getLoc());
        }
        textures.add(new ResourceLocation(Ref.MODID + ":blocks/stone"));
        textures.add(new ResourceLocation(Ref.MODID + ":blocks/ore"));
        return textures;
    }
}
