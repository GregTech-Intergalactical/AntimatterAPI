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
import net.minecraft.item.Item;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.data.EmptyModelData;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public class ModelUtils {

    public static final Texture ERROR = new Texture(Ref.ID, "other/error");

    private static TextureAtlasSprite ERROR_SPRITE = null;

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

    public static Matrix4f getMat(@Nullable Vector3f trans, Quaternion rot) {
        Matrix4f mat = new Matrix4f();
        if (trans != null) mat.translate(trans);
        mat.mul(rot);
        //mat.func_226591_a_(); //Identity?
        return mat;
    }
}
