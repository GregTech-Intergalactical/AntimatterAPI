package muramasa.antimatter.client;

import muramasa.antimatter.Ref;
import muramasa.antimatter.texture.Texture;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

public class ModelUtils {

    public static final Texture ERROR = new Texture(Ref.ID, "other/error");

    private static TextureAtlasSprite ERROR_SPRITE = null;

    private static Function<ResourceLocation, TextureAtlasSprite> TEXTURE_GETTER;

    public static Matrix4f[] FACING_TO_MATRIX = new Matrix4f[] {
        getMat(null, new Quaternion(new Vector3f(1, 0, 0), 4.7124f, true)),
        getMat(null, new Quaternion(new Vector3f(1, 0, 0), 1.5708f, true)),
        getMat(null, new Quaternion(new Vector3f(0, 1, 0), 0f, true)),
        getMat(null, new Quaternion(new Vector3f(0, 1, 0), 3.1416f, true)),
        getMat(null, new Quaternion(new Vector3f(0, 1, 0), 1.5708f, true)),
        getMat(null, new Quaternion(new Vector3f(0, 1, 0), 4.7124f, true)),
    };

    //        for (BakedQuad quad : original.getQuads(state, side, rand)) {
    //            BakedQuadBuilder builder = new BakedQuadBuilder(quad.getSprite());
    //            TRSRTransformer transformer = new TRSRTransformer(builder, transformation.blockCenterToCorner());
    //
    //            quad.pipe(transformer);
    //
    //            quads.add(builder.build());
    //        }

    public static void buildDefaultModels() {

    }

    public static IUnbakedModel getMissingModel() {
        return ModelLoader.instance().getUnbakedModel(new ModelResourceLocation("builtin/missing", "missing"));
    }

    public static IBakedModel getBakedFromQuads(BlockModel model, List<BakedQuad> quads) {
        SimpleBakedModel.Builder builder = new SimpleBakedModel.Builder(model, ItemOverrideList.EMPTY, true);
        quads.forEach(builder::addGeneralQuad);
        return builder.build();
    }

    public static TextureAtlasSprite getErrorSprite() {
        return ERROR_SPRITE != null ? ERROR_SPRITE : (ERROR_SPRITE = ERROR.getSprite());
    }

    public static TextureAtlasSprite getSprite(ResourceLocation loc) {
        //TODO
        //return Minecraft.getInstance().getTextureMap().getSprite(loc);
        return null;
    }

    public static Material getBlockMaterial(ResourceLocation loc) {
        return new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, loc);
    }

    public static Matrix4f getMat(@Nullable Vector3f trans, Quaternion rot) {
        Matrix4f mat = new Matrix4f();
        if (trans != null) mat.translate(trans);
        mat.multiply(rot);
        //mat.func_226591_a_(); //Identity?
        return mat;
    }

    public static IBakedModel getBakedFromState(BlockState state) {
        return Minecraft.getInstance().getModelManager().getModel(BlockModelShapes.getModelLocation(state));
    }

    /** Baked Model Helpers **/
//    public static List<BakedQuad> trans(List<BakedQuad> quads, int rotation) {
//        return trans(quads, FACING_TO_MATRIX[rotation], Ref.DIRECTIONS[rotation]);
//    }
//
//    public static List<BakedQuad> trans(List<BakedQuad> quads, Direction... rotations) {
//        int[] indices = new int[rotations.length];
//        for (int i = 0; i < indices.length; i++) {
//            indices[i] = rotations[i].getIndex();
//        }
//        return trans(quads, 0, indices);
//    }

//    public static List<BakedQuad> trans(List<BakedQuad> quads, int offset, int... rotations) {
//        Matrix4f mat = new Matrix4f(FACING_TO_MATRIX[rotations[offset]]);
//        for (int i = offset + 1; i < rotations.length; i++) {
//            mat.mul(new Matrix4f(FACING_TO_MATRIX[rotations[i]]));
//        }
//        return trans(quads, mat, Ref.DIRECTIONS[rotations[rotations.length - 1]]);
//    }
//
//    //Credit: From AE2
//    public static List<BakedQuad> trans(List<BakedQuad> quads, Matrix4f matrix, Direction side) {
//        List<BakedQuad> transformedQuads = new LinkedList<>();
//        MatrixVertexTransformer transformer = new MatrixVertexTransformer(matrix);
//        UnpackedBakedQuad.Builder builder;
//        for (BakedQuad bakedQuad : quads) {
//            builder = new UnpackedBakedQuad.Builder(bakedQuad.getFormat());
//            transformer.setParent(builder);
//            transformer.setVertexFormat(builder.getVertexFormat());
//            bakedQuad.pipe(transformer);
//            builder.setQuadOrientation(Utils.rotateFacing(bakedQuad.getFace(), side));
//            builder.setTexture(bakedQuad.getSprite() != null ? bakedQuad.getSprite() : ModelUtils.BAKED_MISSING.getParticleTexture());
//            BakedQuad q = builder.build();
//            transformedQuads.add(q);
//        }
//        return transformedQuads;
//    }
}
