package muramasa.antimatter.client;

import muramasa.antimatter.Ref;
import muramasa.antimatter.texture.Texture;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import net.minecraftforge.client.model.pipeline.TRSRTransformer;
import net.minecraftforge.common.model.TransformationHelper;

import java.util.*;
import java.util.function.Function;

public class ModelUtils {

    public static final Texture ERROR = new Texture(Ref.ID, "other/error");

    private static TextureAtlasSprite ERROR_SPRITE = null;

    public static TransformationMatrix[] FACING_TO_MATRIX = new TransformationMatrix[] {
        new TransformationMatrix(new Vector3f(0, 0, 0), TransformationHelper.quatFromXYZ(new Vector3f(4.7124f, 0, 0), false), null, null),
        new TransformationMatrix(new Vector3f(0, 0, 0), TransformationHelper.quatFromXYZ(new Vector3f(1.5708f, 0, 0), false), null, null),
        new TransformationMatrix(new Vector3f(0, 0, 0), TransformationHelper.quatFromXYZ(new Vector3f(0, 0f, 0), false), null, null),
        new TransformationMatrix(new Vector3f(0, 0, 0), TransformationHelper.quatFromXYZ(new Vector3f(0, 3.1416f, 0), false), null, null),
        new TransformationMatrix(new Vector3f(0, 0, 0), TransformationHelper.quatFromXYZ(new Vector3f(0, 1.5708f, 0), false), null, null),
        new TransformationMatrix(new Vector3f(0, 0, 0), TransformationHelper.quatFromXYZ(new Vector3f(0, 4.7124f, 0), false), null, null)
    };

    public static IUnbakedModel getMissingModel() {
        return ModelLoader.instance().getUnbakedModel(new ModelResourceLocation("builtin/missing", "missing"));
    }

    public static IBakedModel getBakedFromQuads(BlockModel model, List<BakedQuad> quads, Function<Material, TextureAtlasSprite> getter) {
        SimpleBakedModel.Builder builder = new SimpleBakedModel.Builder(model, ItemOverrideList.EMPTY, true).setTexture(getter.apply(model.resolveTextureName("particle")));
        quads.forEach(builder::addGeneralQuad);
        return builder.build();
    }

    public static IBakedModel getBakedFromModel(BlockModel model, ModelBakery bakery, Function<Material, TextureAtlasSprite> getter, IModelTransform transform, ResourceLocation loc) {
        List<BakedQuad> generalQuads = model.bakeModel(bakery, model, getter, transform, loc, true).getQuads(null, null, Ref.RNG, EmptyModelData.INSTANCE);
        SimpleBakedModel.Builder builder = new SimpleBakedModel.Builder(model, ItemOverrideList.EMPTY, true).setTexture(getter.apply(model.resolveTextureName("particle")));
        generalQuads.forEach(builder::addGeneralQuad);
        return builder.build();
    }

    public static IBakedModel getSimpleBakedModel(IBakedModel baked) {
        Map<Direction, List<BakedQuad>> faceQuads = new HashMap<>();
        Arrays.stream(Ref.DIRECTIONS).forEach(d -> faceQuads.put(d, baked.getQuads(null, d, Ref.RNG, EmptyModelData.INSTANCE)));
        return new SimpleBakedModel(baked.getQuads(null, null, Ref.RNG, EmptyModelData.INSTANCE), faceQuads, baked.isAmbientOcclusion(), baked.isGui3d(), baked.func_230044_c_(), baked.getParticleTexture(), baked.getItemCameraTransforms(), baked.getOverrides());
    }

    public static IBakedModel getBakedFromState(BlockState state) {
        return Minecraft.getInstance().getModelManager().getModel(BlockModelShapes.getModelLocation(state));
    }

    public static IBakedModel getBakedFromItem(Item item) {
        return Minecraft.getInstance().getItemRenderer().getItemModelMesher().getModelManager().getModel(new ModelResourceLocation(item.getRegistryName(), "inventory"));
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

    public static TransformationMatrix getTransForDir(Direction dir) {
        if (dir.getAxis() == Direction.Axis.Y) {
            float r = dir == Direction.DOWN ? 4.7124f : 1.5708f;
            return new TransformationMatrix(new Vector3f(0, 0, 0), TransformationHelper.quatFromXYZ(new Vector3f(r, 0, 0), false), null, null);
        } else {
            double r = Math.PI * (360 - dir.getOpposite().getHorizontalIndex() * 90) / 180d;
            return new TransformationMatrix(new Vector3f(0, 0, 0), TransformationHelper.quatFromXYZ(new Vector3f(0, (float) r, 0), false), null, null);
        }
    }

    public static List<BakedQuad> trans(List<BakedQuad> quads, Direction[] rotations) {
        if (rotations.length == 0) return quads;
        TransformationMatrix trans = FACING_TO_MATRIX[rotations[0].getIndex()];
        for (int i = 1; i < rotations.length; i++) {
            trans = trans.compose(FACING_TO_MATRIX[rotations[0].getIndex()]);
        }
        List<BakedQuad> newQuads = new ArrayList<>();
        for (BakedQuad quad : quads) {
            BakedQuadBuilder builder = new BakedQuadBuilder(quad.func_187508_a());
            TRSRTransformer transformer = new TRSRTransformer(builder, trans.blockCenterToCorner());
            quad.pipe(transformer);
            newQuads.add(builder.build());
        }
        return newQuads;
    }
}
