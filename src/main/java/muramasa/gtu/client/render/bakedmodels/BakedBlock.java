package muramasa.gtu.client.render.bakedmodels;

import muramasa.gtu.api.texture.Texture;
import muramasa.gtu.client.render.ModelUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.List;

public class BakedBlock extends BakedBase {

    private IBakedModel baked;
    private TextureAtlasSprite particle;

    public BakedBlock(IBakedModel baked) {
        this.baked = baked;
    }

    public BakedBlock(IBakedModel baked, Texture particle) {
        this(baked);
        this.particle = particle.getSprite();
    }

    @Override
    public List<BakedQuad> getBakedQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        return baked.getQuads(state, side, rand);
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return particle;
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        return Pair.of(this, ModelUtils.getBlockTransform(cameraTransformType));
    }
}
