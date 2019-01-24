package muramasa.itech.client.model.models;

import muramasa.itech.ITech;
import muramasa.itech.client.model.bakedmodels.BakedModelCable;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;

import java.util.function.Function;

public class ModelCable extends ModelBase {

    private static final ModelResourceLocation CABLE_BASE = new ModelResourceLocation(ITech.MODID + ":cableparts/cablebase");
    private static final ModelResourceLocation CABLE_SINGLE = new ModelResourceLocation(ITech.MODID + ":cableparts/cablesingle");
    private static final ModelResourceLocation CABLE_LINE = new ModelResourceLocation(ITech.MODID + ":cableparts/cableline");
    private static final ModelResourceLocation CABLE_CORNER = new ModelResourceLocation(ITech.MODID + ":cableparts/cablecorner");
    private static final ModelResourceLocation CABLE_CROSS = new ModelResourceLocation(ITech.MODID + ":cableparts/cablecross");
    private static final ModelResourceLocation CABLE_SIDE = new ModelResourceLocation(ITech.MODID + ":cableparts/cableside");

    public ModelCable() {
        super("ModelCable");
    }

    @Override
    public IBakedModel bakeModel(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        IModel[] models = new IModel[] {
            load(CABLE_BASE),
            load(CABLE_SINGLE),
            load(CABLE_LINE),
            load(CABLE_CROSS),
            load(CABLE_SIDE),
            load(CABLE_CORNER),
        };

        IBakedModel[][] bakedConfigs = new IBakedModel[][] {
            new IBakedModel[] {
                models[0].bake(NORTH, format, bakedTextureGetter)
            },
            new IBakedModel[] {
                models[1].bake(NORTH, format, bakedTextureGetter),
                models[1].bake(SOUTH, format, bakedTextureGetter),
                models[1].bake(WEST, format, bakedTextureGetter),
                models[1].bake(EAST, format, bakedTextureGetter)
            },
            new IBakedModel[] {
                models[2].bake(NORTH, format, bakedTextureGetter),
                models[2].bake(WEST, format, bakedTextureGetter),
                models[2].bake(UP, format, bakedTextureGetter)
            },
            new IBakedModel[] {
                models[3].bake(NORTH, format, bakedTextureGetter)
            },
            new IBakedModel[] {
                models[4].bake(SOUTH, format, bakedTextureGetter),
                models[4].bake(NORTH, format, bakedTextureGetter),
                models[4].bake(WEST, format, bakedTextureGetter),
                models[4].bake(EAST, format, bakedTextureGetter),
                models[4].bake(NORTH.compose(UP), format, bakedTextureGetter)
            },
            new IBakedModel[] {
                models[5].bake(NORTH, format, bakedTextureGetter),
                models[5].bake(SOUTH, format, bakedTextureGetter),
                models[5].bake(WEST, format, bakedTextureGetter),
                models[5].bake(EAST, format, bakedTextureGetter),
                models[5].bake(WEST.compose(DOWN), format, bakedTextureGetter),
                models[5].bake(EAST.compose(DOWN), format, bakedTextureGetter),
                models[5].bake(WEST.compose(UP), format, bakedTextureGetter),
                models[5].bake(EAST.compose(UP), format, bakedTextureGetter),
                models[5].bake(NORTH.compose(DOWN), format, bakedTextureGetter),
                models[5].bake(SOUTH.compose(DOWN), format, bakedTextureGetter),
                models[5].bake(NORTH.compose(UP), format, bakedTextureGetter),
                models[5].bake(SOUTH.compose(UP), format, bakedTextureGetter)
            },
        };

        return new BakedModelCable(bakedConfigs);
    }
}
