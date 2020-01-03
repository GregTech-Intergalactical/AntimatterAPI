package muramasa.antimatter.client.baked;

import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.client.ModelUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BakedBase implements IDynamicBakedModel {

    protected IBakedModel bakedModel;
    protected TextureAtlasSprite particle;

    public BakedBase() {

    }

    public BakedBase(IBakedModel bakedModel) {
        this.bakedModel = bakedModel;
    }

    public BakedBase(Texture texture) {
        this.particle = texture.getSprite();
    }

    public BakedBase(IBakedModel bakedModel, Texture texture) {
        this(bakedModel);
        particle = texture.getSprite();
    }

    public List<BakedQuad> getBakedQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        return bakedModel.getQuads(state, side, rand, data);
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData data) {
        try {
            return getBakedQuads(state, side, rand, data);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        return Pair.of(this, ModelUtils.getBlockTransform(cameraTransformType));
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.EMPTY;
    }

    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return particle != null ? particle : ModelUtils.BAKED_MISSING.getParticleTexture();
    }
}
