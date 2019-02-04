package muramasa.gregtech.client.render.bakedmodels;

import muramasa.gregtech.client.render.MatrixVertexTransformer;
import muramasa.gregtech.client.render.models.ModelBase;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import java.util.LinkedList;
import java.util.List;

public class BakedModelBase implements IBakedModel {

    public static Matrix4f matrixGui = get(0, 0, 0, 30, 225, 0, 0.625f).getMatrix();
    public static Matrix4f matrixFPH = get(0, 0, 0, 0, 45, 0, 0.4f).getMatrix();
    public static Matrix4f matrixIdentity = TRSRTransformation.identity().getMatrix();

//    private static IVertexConsumer[] transformationConsumers = new IVertexConsumer[]{
//
//    };

    protected IBakedModel bakedModel;

    public BakedModelBase() {

    }

    public BakedModelBase(IBakedModel bakedModel) {
        this.bakedModel = bakedModel;
    }

    public static List<BakedQuad> transform(List<BakedQuad> quads, float rot) {
        return transform(quads, new Vector3f(0, 1, 0), rot);
    }

    public static List<BakedQuad> transform(List<BakedQuad> quads, Vector3f pos, float rotation) {
        List<BakedQuad> transformedQuads = new LinkedList<>();

        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();
        matrix.setRotation(new AxisAngle4f(pos.x, pos.y, pos.z, rotation));

        MatrixVertexTransformer transformer = new MatrixVertexTransformer(matrix);
        for (BakedQuad bakedQuad : quads) {
            UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(bakedQuad.getFormat());
            transformer.setParent(builder);
            transformer.setVertexFormat(builder.getVertexFormat());
            bakedQuad.pipe(transformer);
            builder.setQuadOrientation(null); // After rotation, facing a specific side cannot be guaranteed anymore
            BakedQuad q = builder.build();
            transformedQuads.add(q);
        }
        return transformedQuads;
    }

    private static TRSRTransformation get(float tx, float ty, float tz, float ax, float ay, float az, float s) {
        return new TRSRTransformation(new Vector3f(tx / 16, ty / 16, tz / 16), TRSRTransformation.quatFromXYZDegrees(new Vector3f(ax, ay, az)), new Vector3f(s, s, s), null);
    }

    public static boolean hasProperty(IBlockState state, IProperty property) {
        return state.getPropertyKeys().contains(property);
    }

    public static boolean hasUnlistedProperty(IExtendedBlockState exState, IUnlistedProperty property) {
        return exState.getUnlistedNames().contains(property);
    }

    public List<BakedQuad> getBakedQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        return bakedModel.getQuads(state, side, rand);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        try {
            return getBakedQuads(state, side, rand);
        } catch (Exception e) {
            System.err.println("BakedModelBase.getBakedQuads() failed due to " + e + ":");
            e.printStackTrace();
            return new LinkedList<>();
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
        return ModelBase.missingBaked.getParticleTexture();
    }

    public List<BakedQuad> retexture(List<BakedQuad> quads, TextureAtlasSprite sprite) {
        int size = quads.size();
        for (int i = 0; i < size; i++) {
            quads.set(i, new BakedQuadRetextured(quads.get(i), sprite));
        }
        return quads;
    }

    public List<BakedQuad> retexture(List<BakedQuad> quads, int tintIndex, TextureAtlasSprite sprite) {
        int size = quads.size();
        for (int i = 0; i < size; i++) {
            if (quads.get(i).getTintIndex() != tintIndex) continue;
            quads.set(i, new BakedQuadRetextured(quads.get(i), sprite));
        }
        return quads;
    }

    public List<BakedQuad> translateScale(List<BakedQuad> quads, Vec3i t, float s) {
        for (int i = 0; i < quads.size(); i++) {
            BakedQuad q = quads.get(i);

            int[] v = q.getVertexData().clone();

            // rotate
//            r.transform(v);


            // leftRigh, upDown, frontBack
            int lr, ud, fb;

            // A quad has four verticies
            // indices of x values of vertices are 0, 7, 14, 21
            // indices of y values of vertices are 1, 8, 15, 22
            // indices of z values of vertices are 2, 9, 16, 23

            // east: x
            // south: z
            // up: y

            switch (q.getFace()) {
                case UP:
                    // Quad up is towards north
                    lr = t.getX();
                    ud = t.getZ();
                    fb = t.getY();

                    v[0] = transform(v[0], lr, s);
                    v[7] = transform(v[7], lr, s);
                    v[14] = transform(v[14], lr, s);
                    v[21] = transform(v[21], lr, s);

                    v[1] = transform(v[1], fb, s);
                    v[8] = transform(v[8], fb, s);
                    v[15] = transform(v[15], fb, s);
                    v[22] = transform(v[22], fb, s);

                    v[2] = transform(v[2], ud, s);
                    v[9] = transform(v[9], ud, s);
                    v[16] = transform(v[16], ud, s);
                    v[23] = transform(v[23], ud, s);
                    break;

                case DOWN:
                    // Quad up is towards south
                    lr = t.getX();
                    ud = t.getZ();
                    fb = t.getY();

                    v[0] = transform(v[0], lr, s);
                    v[7] = transform(v[7], lr, s);
                    v[14] = transform(v[14], lr, s);
                    v[21] = transform(v[21], lr, s);

                    v[1] = transform(v[1], fb, s);
                    v[8] = transform(v[8], fb, s);
                    v[15] = transform(v[15], fb, s);
                    v[22] = transform(v[22], fb, s);

                    v[2] = transform(v[2], -ud, s);
                    v[9] = transform(v[9], -ud, s);
                    v[16] = transform(v[16], -ud, s);
                    v[23] = transform(v[23], -ud, s);
                    break;

                case WEST:
                    lr = t.getZ();
                    ud = t.getY();
                    fb = t.getX();

                    v[0] = transform(v[0], fb, s);
                    v[7] = transform(v[7], fb, s);
                    v[14] = transform(v[14], fb, s);
                    v[21] = transform(v[21], fb, s);

                    v[1] = transform(v[1], ud, s);
                    v[8] = transform(v[8], ud, s);
                    v[15] = transform(v[15], ud, s);
                    v[22] = transform(v[22], ud, s);

                    v[2] = transform(v[2], lr, s);
                    v[9] = transform(v[9], lr, s);
                    v[16] = transform(v[16], lr, s);
                    v[23] = transform(v[23], lr, s);
                    break;

                case EAST:
                    lr = t.getZ();
                    ud = t.getY();
                    fb = t.getX();

                    v[0] = transform(v[0], fb, s);
                    v[7] = transform(v[7], fb, s);
                    v[14] = transform(v[14], fb, s);
                    v[21] = transform(v[21], fb, s);

                    v[1] = transform(v[1], ud, s);
                    v[8] = transform(v[8], ud, s);
                    v[15] = transform(v[15], ud, s);
                    v[22] = transform(v[22], ud, s);

                    v[2] = transform(v[2], lr, s);
                    v[9] = transform(v[9], lr, s);
                    v[16] = transform(v[16], lr, s);
                    v[23] = transform(v[23], lr, s);
                    break;

                case NORTH:
                    lr = t.getX();
                    ud = t.getY();
                    fb = t.getZ();

                    v[0] = transform(v[0], lr, s);
                    v[7] = transform(v[7], lr, s);
                    v[14] = transform(v[14], lr, s);
                    v[21] = transform(v[21], lr, s);

                    v[1] = transform(v[1], ud, s);
                    v[8] = transform(v[8], ud, s);
                    v[15] = transform(v[15], ud, s);
                    v[22] = transform(v[22], ud, s);

                    v[2] = transform(v[2], fb, s);
                    v[9] = transform(v[9], fb, s);
                    v[16] = transform(v[16], fb, s);
                    v[23] = transform(v[23], fb, s);
                    break;

                case SOUTH:
                    // Case where quad is aligned with world coordinates
                    lr = t.getX();
                    ud = t.getY();
                    fb = t.getZ();

                    v[0] = transform(v[0], lr, s);
                    v[7] = transform(v[7], lr, s);
                    v[14] = transform(v[14], lr, s);
                    v[21] = transform(v[21], lr, s);

                    v[1] = transform(v[1], ud, s);
                    v[8] = transform(v[8], ud, s);
                    v[15] = transform(v[15], ud, s);
                    v[22] = transform(v[22], ud, s);

                    v[2] = transform(v[2], fb, s);
                    v[9] = transform(v[9], fb, s);
                    v[16] = transform(v[16], fb, s);
                    v[23] = transform(v[23], fb, s);
                    break;

                default:
                    System.out.println("Unexpected face=" + q.getFace());
                    break;
            }
            quads.set(i, new BakedQuad(v, q.getTintIndex(), q.getFace(), q.getSprite(), q.shouldApplyDiffuseLighting(), q.getFormat()));
        }
        return quads;
    }

    private int transform(int i, int t, float s) {
        float f = Float.intBitsToFloat(i);
        f = (f + t) * s;
        return Float.floatToRawIntBits(f);
    }
}
