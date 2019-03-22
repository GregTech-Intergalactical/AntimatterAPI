package muramasa.gregtech.client.render;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import muramasa.gregtech.api.texture.Texture;
import muramasa.gregtech.api.texture.TextureMode;
import muramasa.gregtech.client.render.bakedmodels.BakedBase;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import java.util.*;
import java.util.function.Function;

public class ModelUtils {

    private static HashMap<String, IBakedModel> CACHE = new HashMap<>();

    private static Function<ResourceLocation, TextureAtlasSprite> TEXTURE_GETTER;

    private static EnumMap<ItemCameraTransforms.TransformType, Matrix4f> TRANSFORM_MAP_ITEM = new EnumMap<>(ItemCameraTransforms.TransformType.class);
    private static EnumMap<ItemCameraTransforms.TransformType, Matrix4f> TRANSFORM_MAP_BLOCK = new EnumMap<>(ItemCameraTransforms.TransformType.class);

    private static Matrix4f[] FACING_TO_MATRIX = new Matrix4f[] {
        getMat(new AxisAngle4f(new Vector3f(1, 0, 0), 4.7124f)),
        getMat(new AxisAngle4f(new Vector3f(1, 0, 0), 1.5708f)),
        getMat(new AxisAngle4f(new Vector3f(0, 1, 0), 0f)),
        getMat(new AxisAngle4f(new Vector3f(0, 1, 0), 3.1416f)),
        getMat(new AxisAngle4f(new Vector3f(0, 1, 0), 1.5708f)),
        getMat(new AxisAngle4f(new Vector3f(0, 1, 0), 4.7124f)),
    };

    static {
        TRANSFORM_MAP_ITEM.put(ItemCameraTransforms.TransformType.GUI, getTransform(0, 0, 0, 0, 0, 0, 1f).getMatrix());
        TRANSFORM_MAP_ITEM.put(ItemCameraTransforms.TransformType.GROUND, getTransform(0, 2, 0, 0, 0, 0, 0.5f).getMatrix());
        TRANSFORM_MAP_ITEM.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, getTransform(1.13f, 3.2f, 1.13f, 0, -90, 25, 0.68f).getMatrix());
        TRANSFORM_MAP_ITEM.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, getTransform(0, 3, 1, 0, 0, 0, 0.55f).getMatrix());
        TRANSFORM_MAP_ITEM.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, getTransform(1.13f, 3.2f, 1.13f, 0, 90, -25, 0.68f).getMatrix());
        TRANSFORM_MAP_ITEM.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, getTransform(0f, 4.0f, 0.5f, 0, 90, -55, 0.85f).getMatrix());

        TRANSFORM_MAP_BLOCK.put(ItemCameraTransforms.TransformType.GUI, getTransform(0, 0, 0, 30, 225, 0, 0.625f).getMatrix());
        TRANSFORM_MAP_BLOCK.put(ItemCameraTransforms.TransformType.GROUND, getTransform(0, 2, 0, 0, 0, 0, 0.25f).getMatrix());
        TRANSFORM_MAP_BLOCK.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, getTransform(0, 0, 0, 0, 45, 0, 0.4f).getMatrix());
        TRANSFORM_MAP_BLOCK.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, getTransform(0, 0, 0, 0, 0, 0, 0.4f).getMatrix());
        TRANSFORM_MAP_BLOCK.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, getTransform(0, 0, 0, 45, 0, 0, 0.4f).getMatrix());
        TRANSFORM_MAP_BLOCK.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, getTransform(0, 0, 0, 45, 0, 0, 0.4f).getMatrix());
    }

    public static boolean hasCache(String key) {
        return CACHE.containsKey(key);
    }

    public static IBakedModel getCache(String key) {
        return CACHE.get(key);
    }

    public static void putCache(String key, IBakedModel baked) {
        CACHE.put(key, baked);
    }

    public static Function<ResourceLocation, TextureAtlasSprite> getTextureGetter() {
        if (TEXTURE_GETTER == null) TEXTURE_GETTER = location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
        return TEXTURE_GETTER;
    }

    public static Matrix4f getItemTransform(ItemCameraTransforms.TransformType type) {
        Matrix4f mat = TRANSFORM_MAP_ITEM.get(type);
        return mat != null ? mat : TRSRTransformation.identity().getMatrix();
    }

    public static Matrix4f getBlockTransform(ItemCameraTransforms.TransformType type) {
        Matrix4f mat = TRANSFORM_MAP_BLOCK.get(type);
        return mat != null ? mat : TRSRTransformation.identity().getMatrix();
    }

    public static TRSRTransformation getTransform(float tx, float ty, float tz, float ax, float ay, float az, float s) {
        return new TRSRTransformation(new Vector3f(tx / 16, ty / 16, tz / 16), TRSRTransformation.quatFromXYZDegrees(new Vector3f(ax, ay, az)), new Vector3f(s, s, s), null);
    }

    public static Matrix4f getMat(AxisAngle4f angle) {
        Matrix4f mat = new Matrix4f();
        mat.setIdentity();
        mat.setRotation(angle);
        return mat;
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
        MatrixVertexTransformer transformer = new MatrixVertexTransformer(FACING_TO_MATRIX[rotation]);
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

    public static List<BakedQuad> texCell(List<BakedQuad> quads, int rgb, TextureAtlasSprite sprite) {
        List<BakedQuad> quadsTemp = new LinkedList<>();
        int size = quads.size();
        for (int i = 0; i < size; i++) {
            BakedQuad quad = new BakedQuadRetextured(quads.get(i), sprite);
            quadsTemp.add(new BakedQuadTinted(quad, rgb));
        }
        return quadsTemp;
    }

    public static List<BakedQuad> tint(List<BakedQuad> quads, int rgb) {
        int size = quads.size();
        for (int i = 0; i < size; i++) {
            quads.set(i, new BakedQuadTinted(quads.get(i), rgb));
        }
        return quads;
    }
}
