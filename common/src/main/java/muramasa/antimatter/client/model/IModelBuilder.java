package muramasa.antimatter.client.model;

import muramasa.antimatter.client.ModelUtils;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.Direction;

public interface IModelBuilder<T extends IModelBuilder<T>> {
    static IModelBuilder<?> of(IModelConfiguration owner, ItemOverrides overrides, TextureAtlasSprite particle)
    {
        return new IModelBuilder.Simple(ModelUtils.createSimpleModelBuilder(owner.useSmoothLighting(), owner.isSideLit(), owner.isShadedInGui(), owner.getCameraTransforms(), overrides).particle(particle));
    }

    T addFaceQuad(Direction facing, BakedQuad quad);
    T addGeneralQuad(BakedQuad quad);

    BakedModel build();

    class Simple implements IModelBuilder<IModelBuilder.Simple> {
        final SimpleBakedModel.Builder builder;

        Simple(SimpleBakedModel.Builder builder)
        {
            this.builder = builder;
        }

        @Override
        public IModelBuilder.Simple addFaceQuad(Direction facing, BakedQuad quad)
        {
            builder.addCulledFace(facing, quad);
            return this;
        }

        @Override
        public IModelBuilder.Simple addGeneralQuad(BakedQuad quad)
        {
            builder.addUnculledFace(quad);
            return this;
        }

        @Override
        public BakedModel build()
        {
            return builder.build();
        }
    }
}
