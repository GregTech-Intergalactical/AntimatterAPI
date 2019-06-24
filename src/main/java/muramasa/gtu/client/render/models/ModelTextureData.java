package muramasa.gtu.client.render.models;

import muramasa.gtu.client.render.ModelUtils;
import muramasa.gtu.client.render.bakedmodels.BakedTextureData;
import muramasa.gtu.api.blocks.BlockBaked;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

public class ModelTextureData implements IModel {

    protected BlockBaked block;
    protected ItemOverrideList item;

    public ModelTextureData(BlockBaked block) {
        this.block = block;
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> getter) {
        IBakedModel baked = ModelUtils.load(block.getModel()).bake(state, format, getter);
        return new BakedTextureData(baked, block.getOverride(baked));
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        return new ArrayList<>(block.getTextures());
    }
}
