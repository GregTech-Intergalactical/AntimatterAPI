package muramasa.gregtech.client.render.bakedmodels;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import muramasa.gregtech.api.texture.Texture;
import muramasa.gregtech.api.texture.TextureMode;
import muramasa.gregtech.client.render.BakedQuadTinted;
import muramasa.gregtech.client.render.MatrixVertexTransformer;
import muramasa.gregtech.client.render.models.ModelBase;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BakedBase implements IBakedModel {

    private static Matrix4f[] facingToMatrix = new Matrix4f[] {
        getMat(new AxisAngle4f(new Vector3f(1, 0, 0), 4.7124f)),
        getMat(new AxisAngle4f(new Vector3f(1, 0, 0), 1.5708f)),
        getMat(new AxisAngle4f(new Vector3f(0, 1, 0), 0f)),
        getMat(new AxisAngle4f(new Vector3f(0, 1, 0), 3.1416f)),
        getMat(new AxisAngle4f(new Vector3f(0, 1, 0), 1.5708f)),
        getMat(new AxisAngle4f(new Vector3f(0, 1, 0), 4.7124f)),
    };

    public static Matrix4f matrixGui = get(0, 0, 0, 30, 225, 0, 0.625f).getMatrix();
    public static Matrix4f matrixFPH = get(0, 0, 0, 0, 45, 0, 0.4f).getMatrix();
    public static Matrix4f matrixIdentity = TRSRTransformation.identity().getMatrix();

    private IBakedModel bakedModel;

    public BakedBase() {

    }

    public BakedBase(IBakedModel bakedModel) {
        this.bakedModel = bakedModel;
    }

    public List<BakedQuad> getBakedQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        return bakedModel.getQuads(state, side, rand);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (side != null) return Collections.emptyList();
        try {
            return getBakedQuads(state, null, rand);
        } catch (Exception e) {
            System.err.println("BakedModelBase.getBakedQuads() failed due to: " + e);
            e.printStackTrace();
            return Collections.emptyList();
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
        return ModelBase.MISSING.getParticleTexture();
    }

    /** Helper Methods **/
    public static Matrix4f getMat(AxisAngle4f angle) {
        Matrix4f mat = new Matrix4f();
        mat.setIdentity();
        mat.setRotation(angle);
        return mat;
    }

    public static TRSRTransformation get(float tx, float ty, float tz, float ax, float ay, float az, float s) {
        return new TRSRTransformation(new Vector3f(tx / 16, ty / 16, tz / 16), TRSRTransformation.quatFromXYZDegrees(new Vector3f(ax, ay, az)), new Vector3f(s, s, s), null);
    }

    public static boolean hasProperty(IBlockState state, IProperty property) {
        return state.getPropertyKeys().contains(property);
    }

    public static boolean hasUnlistedProperty(IExtendedBlockState exState, IUnlistedProperty property) {
        return exState.getUnlistedNames().contains(property);
    }

    public static IBakedModel getBaked(IBakedModel baked, List<BakedQuad> quads) {
        Map faceQuads = Maps.newEnumMap(EnumFacing.class);
        for (EnumFacing s : EnumFacing.values()) {
            faceQuads.put(s, Lists.newArrayList());
        }
        return new BakedBase(new SimpleBakedModel(quads, faceQuads, baked.isAmbientOcclusion(), baked.isGui3d(), baked.getParticleTexture(), baked.getItemCameraTransforms(), baked.getOverrides()));
    }

    //Credit: From AE2
    public static List<BakedQuad> trans(List<BakedQuad> quads, int rotation) {
        List<BakedQuad> transformedQuads = new LinkedList<>();
        MatrixVertexTransformer transformer = new MatrixVertexTransformer(facingToMatrix[rotation]);
        for (BakedQuad bakedQuad : quads) {
            UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(bakedQuad.getFormat());
            transformer.setParent(builder);
            transformer.setVertexFormat(builder.getVertexFormat());
            bakedQuad.pipe(transformer);
            builder.setQuadOrientation(EnumFacing.VALUES[rotation]);
            BakedQuad q = builder.build();
            transformedQuads.add(q);
        }
        return transformedQuads;
    }

    public static List<BakedQuad> tex(List<BakedQuad> quads, TextureMode mode, Texture[] textures, int layer) {
//        for (int i = 0; i < textures.length; i++) {
//            if (textures[i] == null) continue;
//            switch (mode) {
//                case SINGLE:
//                case FULL:
//                    tex(quads, layer, textures[i]);
//                    break;
//                case COPIED_SIDES:
////                    System.out.println("Side: " + i + " - " + textures[i].getLoc());
////                    tex(quads, i, layer, textures[i]);
////                    if (i == textures.length - 1) {
////                        System.out.println(i);
//////                        tex(quads, 5, layer, textures[4]);
////                    }
//                    break;
//            }
//        }
//        return quads;
        switch (mode) {
            case SINGLE:
                tex(quads, layer, textures[0]);
                break;
            case FULL:
//                for (int i = 0; i < 6; i++) {
//                    tex(quads, layer, textures[i]);
//                }
                //TODO
                break;
            case COPIED_SIDES:
                //TODO
                break;
        }
        return quads;
    }

    public static List<BakedQuad> texOverlays(List<BakedQuad> quads, TextureMode mode, Texture[] textures) {
        switch (mode) {
            case FULL:
                int size = quads.size(), index;
                for (int t = 0; t < textures.length; t++) {
                    for (int q = 0; q < size; q++) {
                        index = quads.get(q).getTintIndex();
//                        if (index != t || index) continue;
                        if (index == t) {
                            quads.set(q, new BakedQuadRetextured(quads.get(q), textures[t].getSprite()));
                        } else {
                            System.out.println("ELSE: " + index);
                        }
                    }
                }
                break;
        }
        return quads;
    }

    public static List<BakedQuad> tex(List<BakedQuad> quads, int layer, Texture texture) {
        int size = quads.size();
        for (int i = 0; i < size; i++) {
            if (quads.get(i).getTintIndex() != layer) continue;
            quads.set(i, new BakedQuadRetextured(quads.get(i), texture.getSprite()));
        }
        return quads;
    }

    public static List<BakedQuad> tint(List<BakedQuad> quads, int rgb) {
        int size = quads.size();
        for (int i = 0; i < size; i++) {
            quads.set(i, new BakedQuadTinted(quads.get(i), rgb));
        }
        return quads;
    }
}
