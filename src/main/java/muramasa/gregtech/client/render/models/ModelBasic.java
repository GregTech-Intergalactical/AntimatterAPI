package muramasa.gregtech.client.render.models;

import muramasa.gregtech.api.texture.IBakedBlock;
import muramasa.gregtech.client.render.bakedmodels.BakedModelBasic;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.IModelState;

import java.util.function.Function;

public class ModelBasic extends ModelBase {

    protected IBakedBlock bakedBlock;

    public ModelBasic(String name, IBakedBlock bakedBlock) {
        super(name);
        this.bakedBlock = bakedBlock;
        addTextures(bakedBlock.getTextureData().getBase());
        addTextures(bakedBlock.getTextureData().getOverlay());
    }

    @Override
    public IBakedModel bakeModel(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> getter) {
        return new BakedModelBasic(load(bakedBlock.getModel()).bake(state, format, getter));
    }
}
