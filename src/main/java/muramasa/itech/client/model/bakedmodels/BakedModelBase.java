package muramasa.itech.client.model.bakedmodels;

import muramasa.itech.client.model.models.ModelBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import java.util.LinkedList;
import java.util.List;

public class BakedModelBase implements IBakedModel {

    public static Matrix4f matrixGui = get(0, 0, 0, 30, 225, 0, 0.625f).getMatrix();
    public static Matrix4f matrixFPH = get(0, 0, 0, 0, 45, 0, 0.4f).getMatrix();
    public static Matrix4f matrixIdentity = TRSRTransformation.identity().getMatrix();

    public static List<BakedQuad> emptyQuads = new LinkedList<>();

    private IBakedModel bakedModel;

    public BakedModelBase() {

    }

    public BakedModelBase(IBakedModel bakedModel) {
        this.bakedModel = bakedModel;
    }

    public List<BakedQuad> getBakedQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (bakedModel != null) {
            return bakedModel.getQuads(state, side, rand);
        } else {
            return emptyQuads;
        }
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        try {
            return getBakedQuads(state, side, rand);
        } catch (Exception e) {
            System.err.println("BakedModelBase.getBakedQuads() failed due to " + e + ":");
            e.printStackTrace();
            return emptyQuads;
        }
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        switch (cameraTransformType) {
            case GUI: return Pair.of(this, matrixGui);
//            case GROUND: return Pair.of(this, get(0, 2, 0, 0, 0, 0, 0.5f).getMatrix());
            case FIRST_PERSON_RIGHT_HAND: return Pair.of(this, matrixFPH);
            default: return Pair.of(this, matrixIdentity);
        }
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
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
        return ModelBase.missingModelBaked.getParticleTexture();
    }

    private static TRSRTransformation get(float tx, float ty, float tz, float ax, float ay, float az, float s) {
        return new TRSRTransformation(new Vector3f(tx / 16, ty / 16, tz / 16), TRSRTransformation.quatFromXYZDegrees(new Vector3f(ax, ay, az)), new Vector3f(s, s, s), null);
    }

    public boolean hasUnlistedProperty(IExtendedBlockState exState, IUnlistedProperty property) {
        return exState.getUnlistedNames().contains(property);
    }
}
