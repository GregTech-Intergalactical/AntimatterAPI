package muramasa.gtu.client.render;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import muramasa.gtu.Ref;
import muramasa.gtu.api.texture.Texture;
import muramasa.gtu.api.texture.TextureData;
import muramasa.gtu.api.texture.TextureMode;
import muramasa.gtu.api.util.Utils;
import muramasa.gtu.client.render.bakedmodels.BakedBase;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.client.model.pipeline.VertexTransformer;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.util.*;
import java.util.function.Function;

public class ModelUtils {

    private static Function<ResourceLocation, TextureAtlasSprite> TEXTURE_GETTER;

    private static EnumMap<ItemCameraTransforms.TransformType, Matrix4f> TRANSFORM_MAP_ITEM = new EnumMap<>(ItemCameraTransforms.TransformType.class);
    private static EnumMap<ItemCameraTransforms.TransformType, Matrix4f> TRANSFORM_MAP_BLOCK = new EnumMap<>(ItemCameraTransforms.TransformType.class);

    public static IModel MODEL_BASIC, MODEL_LAYERED, MODEL_COMPLEX;
    public static IBakedModel BAKED_MISSING, BAKED_BASIC, BAKED_LAYERED, BAKED_COMPLEX;

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

    public static void onModelBake(ModelBakeEvent e) {
        MODEL_BASIC = load("basic");
        MODEL_LAYERED = load("layered");
        MODEL_COMPLEX = load("complex");
        BAKED_BASIC = MODEL_BASIC.bake(TRSRTransformation.identity(), DefaultVertexFormats.BLOCK, getTextureGetter());
        BAKED_LAYERED = MODEL_LAYERED.bake(TRSRTransformation.identity(), DefaultVertexFormats.BLOCK, getTextureGetter());
        BAKED_COMPLEX = MODEL_COMPLEX.bake(TRSRTransformation.identity(), DefaultVertexFormats.BLOCK, getTextureGetter());
        BAKED_MISSING = ModelLoaderRegistry.getMissingModel().bake(TRSRTransformation.identity(), DefaultVertexFormats.BLOCK, getTextureGetter());
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

    //Broken
    public static IBakedModel getBaked(IBakedModel baked, List<BakedQuad> quads) {
        Map faceQuads = Maps.newEnumMap(EnumFacing.class);
        for (EnumFacing s : EnumFacing.values()) {
            faceQuads.put(s, Lists.newArrayList());
        }
        return new BakedBase(new SimpleBakedModel(quads, faceQuads, baked.isAmbientOcclusion(), baked.isGui3d(), baked.getParticleTexture(), baked.getItemCameraTransforms(), baked.getOverrides()));
    }

    //TODO expand to support dynamic baking of TextureData objects and its modes
    public static IBakedModel getBakedTextureData(TextureData data) {
        if (data.getOverlay() != null) {
            return ModelUtils.tex(ModelUtils.MODEL_LAYERED, new String[]{"0", "1"}, new Texture[]{data.getBase()[0], data.getOverlay()[0]}).bake(TRSRTransformation.identity(), DefaultVertexFormats.BLOCK, ModelUtils.getTextureGetter());
        }
        return ModelUtils.tex(ModelUtils.MODEL_BASIC, "0", data.getBase()[0]).bake(TRSRTransformation.identity(), DefaultVertexFormats.BLOCK, ModelUtils.getTextureGetter());
    }

    /** Model Helpers **/
    public static IModel load(String path) {
        return load(new ModelResourceLocation(Ref.MODID + ":" + path));
    }

    public static IModel load(String domain, String path) {
        return load(new ModelResourceLocation(domain + ":" + path));
    }

    public static IModel load(ModelResourceLocation loc) {
        try {
            return ModelLoaderRegistry.getModel(loc);
        } catch (Exception e) {
            System.err.println("ModelBase.load() failed due to " + e + ":");
            e.printStackTrace();
            return ModelLoaderRegistry.getMissingModel();
        }
    }

    public static IModel tex(IModel model, String[] elements, Texture[] textures) {
        for (int i = 0; i < elements.length; i++) {
            model = tex(model, elements[i], textures[i]);
        }
        return model;
    }

    public static IModel tex(IModel model, String element, ResourceLocation loc) {
        try {
            return model.retexture(ImmutableMap.of(element, loc.toString()));
        } catch (Exception e) {
            System.err.println("ModelBase.tex() failed due to " + e + ":");
            e.printStackTrace();
            return model;
        }
    }

    /** Baked Model Helpers **/
    public static List<BakedQuad> trans(List<BakedQuad> quads, int rotation) {
        return trans(quads, FACING_TO_MATRIX[rotation], EnumFacing.VALUES[rotation]);
    }

    public static List<BakedQuad> trans(List<BakedQuad> quads, int offset, int... rotations) {
        Matrix4f mat = new Matrix4f(FACING_TO_MATRIX[rotations[offset]]);
        for (int i = offset + 1; i < rotations.length; i++) {
            mat.mul(new Matrix4f(FACING_TO_MATRIX[rotations[i]]));
        }
        return trans(quads, mat, EnumFacing.VALUES[rotations[rotations.length - 1]]);
    }

    //Credit: From AE2
    public static List<BakedQuad> trans(List<BakedQuad> quads, Matrix4f matrix, EnumFacing facing) {
        List<BakedQuad> transformedQuads = new LinkedList<>();
        MatrixVertexTransformer transformer = new MatrixVertexTransformer(matrix);
        UnpackedBakedQuad.Builder builder;
        for (BakedQuad bakedQuad : quads) {
            builder = new UnpackedBakedQuad.Builder(bakedQuad.getFormat());
            transformer.setParent(builder);
            transformer.setVertexFormat(builder.getVertexFormat());
            bakedQuad.pipe(transformer);
            builder.setQuadOrientation(Utils.rotateFacing(bakedQuad.getFace(), facing));
            builder.setTexture(bakedQuad.getSprite() != null ? bakedQuad.getSprite() : ModelUtils.BAKED_MISSING.getParticleTexture());
            BakedQuad q = builder.build();
            transformedQuads.add(q);
        }
        return transformedQuads;
    }


    public static List<BakedQuad> transform(List<BakedQuad> quads, final EnumFacing rotation) {
        List<BakedQuad> result = new ArrayList<>();

        for (BakedQuad quad : quads) {
            UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(DefaultVertexFormats.ITEM);
            final IVertexConsumer consumer = new VertexTransformer(builder) {
                @Override
                public void put(int element, float... data) {
                    VertexFormatElement formatElement = DefaultVertexFormats.ITEM.getElement(element);
                    switch (formatElement.getUsage()) {
                        case POSITION: {
                            float[] newData = new float[4];
                            Vector4f vec = new Vector4f(data);
                            TRSRTransformation.from(rotation).getMatrix().transform(vec);
                            switch (rotation) {
                                case UP:
                                    vec.add(new Vector4f(0, 1, 0, 0));
                                    break;
                                case DOWN:
                                    vec.add(new Vector4f(0, 0, 1, 0));
                                    break;
                                case NORTH:
                                    vec.add(new Vector4f(0, 0, 0, 0));
                                    break;
                                case EAST:
                                    vec.add(new Vector4f(1, 0, 0, 0));
                                    break;
                                case SOUTH:
                                    vec.add(new Vector4f(1, 0, 1, 0));
                                    break;
                                case WEST:
                                    vec.add(new Vector4f(0, 0, 1, 0));
                                    break;
                            }
                            vec.get(newData);
                            parent.put(element, newData);
                            break;
                        }
                        default: {
                            parent.put(element, data);
                            break;
                        }
                    }
                }
            };

            quad.pipe(consumer);
            result.add(builder.build());
        }
        return result;
    }


    public static List<BakedQuad> tex(List<BakedQuad> quads, TextureMode mode, Texture[] textures, QuadLayer layer) {
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

    //TODO convert layer onto an Enum
    public static List<BakedQuad> tex(List<BakedQuad> quads, QuadLayer layer, Texture texture) {
        return tex(quads, layer.getIndex(), texture.getSprite());
    }

    public static List<BakedQuad> tex(List<BakedQuad> quads, int layer, Texture texture) {
        return tex(quads, layer, texture.getSprite());
    }

    public static List<BakedQuad> tex(List<BakedQuad> quads, QuadLayer layer1, QuadLayer layer2, Texture texture) {
        return tex(quads, layer1, layer2, texture.getSprite());
    }

    public static List<BakedQuad> tex(List<BakedQuad> quads, int layer, TextureAtlasSprite sprite) {
        int size = quads.size();
        for (int i = 0; i < size; i++) {
            if (quads.get(i).getTintIndex() != layer) continue;
            quads.set(i, new BakedQuadRetextured(quads.get(i), sprite));
        }
        return quads;
    }

    public static List<BakedQuad> tex(List<BakedQuad> quads, QuadLayer layer1, QuadLayer layer2, TextureAtlasSprite sprite) {
        int size = quads.size();
        for (int i = 0; i < size; i++) {
            if (!(quads.get(i).getTintIndex() == layer1.getIndex() || quads.get(i).getTintIndex() == layer2.getIndex())) continue;
            quads.set(i, new BakedQuadRetextured(quads.get(i), sprite));
        }
        return quads;
    }

    public static List<BakedQuad> tint(List<BakedQuad> quads, QuadLayer layer, int rgb) {
        List<BakedQuad> tintedQuads = new LinkedList<>();
        int size = quads.size();
        for (int i = 0; i < size; i++) {
            if (quads.get(i).getTintIndex() != layer.getIndex()) continue;
            tintedQuads.add(new BakedQuadTinted(quads.get(i), rgb));
        }
        return quads;
    }

    public static List<BakedQuad> texAndTint(List<BakedQuad> quads, int rgb, Texture texture) {
        return texAndTint(quads, rgb, texture.getSprite());
    }

    public static List<BakedQuad> texAndTint(List<BakedQuad> quads, int rgb, TextureAtlasSprite sprite) {
        List<BakedQuad> quadsTemp = new LinkedList<>();
        int size = quads.size();
        for (int i = 0; i < size; i++) {
            BakedQuad quad = new BakedQuadRetextured(quads.get(i), sprite);
            quadsTemp.add(new BakedQuadTinted(quad, rgb));
        }
        return quadsTemp;
    }

    public static List<BakedQuad> remove(List<BakedQuad> quads, QuadLayer layer) {
        List<BakedQuad> quadsTemp = new LinkedList<>();
        int size = quads.size();
        for (int i = 0; i < size; i++) {
            if (quads.get(i).getTintIndex() != layer.getIndex()) quadsTemp.add(quads.get(i));
        }
        return quadsTemp;
    }
}
