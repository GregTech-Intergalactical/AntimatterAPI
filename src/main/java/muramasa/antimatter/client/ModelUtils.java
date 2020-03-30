package muramasa.antimatter.client;

import muramasa.antimatter.Ref;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.QuadTransformer;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.common.model.TransformationHelper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ModelUtils {

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

    public static TextureAtlasSprite getSprite(ResourceLocation loc) {
        //TODO
        //return Minecraft.getInstance().getTextureMap().getSprite(loc);
        return null;
    }

    public static Material getBlockMaterial(ResourceLocation loc) {
        return new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, loc);
    }

    public static List<BakedQuad> trans(List<BakedQuad> quads, Vector3f rotationL, Vector3f rotationR) {
        Quaternion rotL = rotationL == null ? null : TransformationHelper.quatFromXYZ(rotationL, true);
        Quaternion rotR = rotationR == null ? null : TransformationHelper.quatFromXYZ(rotationR, true);
        return trans(quads, new TransformationMatrix(new Vector3f(0, 0, 0), rotL, null, rotR));
    }

    public static List<BakedQuad> trans(List<BakedQuad> quads, TransformationMatrix transform) {
        return new QuadTransformer(transform.blockCenterToCorner()).processMany(quads);
    }
}
