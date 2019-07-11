package muramasa.gtu.client.render.models;

import muramasa.gtu.api.ore.StoneType;
import muramasa.gtu.client.render.ModelUtils;
import muramasa.gtu.client.render.bakedmodels.BakedBase;
import muramasa.gtu.client.render.bakedmodels.BakedRock;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.ArrayList;
import java.util.function.Function;

public class ModelRock implements IModel {

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> getter) {
        IModel[] models = new IModel[] {ModelUtils.load("rock/rock_0"), ModelUtils.load("rock/rock_1"), ModelUtils.load("rock/rock_2"), ModelUtils.load("rock/rock_3"), ModelUtils.load("rock/rock_4"), ModelUtils.load("rock/rock_5"), ModelUtils.load("rock/rock_6")};

        ArrayList<IBakedModel> baked = new ArrayList<>();
        for (int i = 0; i < models.length; i++) {
            IModel textured = ModelUtils.tex(models[i], "0", StoneType.STONE.getTexture());
            baked.add(new BakedBase(textured.bake(TRSRTransformation.from(EnumFacing.NORTH), format, getter), StoneType.STONE.getTexture()));
            baked.add(new BakedBase(textured.bake(TRSRTransformation.from(EnumFacing.EAST), format, getter), StoneType.STONE.getTexture()));
            baked.add(new BakedBase(textured.bake(TRSRTransformation.from(EnumFacing.SOUTH), format, getter), StoneType.STONE.getTexture()));
            baked.add(new BakedBase(textured.bake(TRSRTransformation.from(EnumFacing.WEST), format, getter), StoneType.STONE.getTexture()));
        }
        BakedRock.BAKED = baked.toArray(new IBakedModel[0]);

        return new BakedRock();
    }
}
