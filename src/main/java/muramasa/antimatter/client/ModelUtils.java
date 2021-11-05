package muramasa.antimatter.client;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.Ref;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.Item;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.QuadTransformer;
import net.minecraftforge.client.model.SimpleModelTransform;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.common.model.TransformationHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ModelUtils {

    //Assumes from North.
    public static TransformationMatrix transform(Direction side) {
        switch (side) {
            case DOWN:
                return new TransformationMatrix(null, new Quaternion(new Quaternion(new Vector3f(1.0F, 0.0F, 0.0F), 90.0F, true)), null, null);
            case UP:
                return new TransformationMatrix(null, new Quaternion(new Quaternion(new Vector3f(1.0F, 0.0F, 0.0F), -90.0F, true)), null, null);
            case NORTH:
                return TransformationMatrix.identity();
            case SOUTH:
                return new TransformationMatrix(null, new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), 180.0F, true), null, null);
            case WEST:
                return new TransformationMatrix(null, new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), 90.0f, true), null, null);
            case EAST:
                return new TransformationMatrix(null, new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), -90.0f, true), null, null);
            default:
                throw new RuntimeException("Invalid direction/null sent to transform.");
        }
    }

    public static IUnbakedModel getMissingModel() {
        return ModelLoader.instance().getUnbakedModel(new ModelResourceLocation("builtin/missing", "missing"));
    }

    public static IBakedModel getBakedFromQuads(BlockModel model, List<BakedQuad> quads, Function<RenderMaterial, TextureAtlasSprite> getter) {
        SimpleBakedModel.Builder builder = new SimpleBakedModel.Builder(model, ItemOverrideList.EMPTY, true).setTexture(getter.apply(model.resolveTextureName("particle")));
        quads.forEach(builder::addGeneralQuad);
        return builder.build();
    }

    public static IBakedModel getBakedFromModel(BlockModel model, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> getter, IModelTransform transform, ResourceLocation loc) {
        List<BakedQuad> generalQuads = model.bakeModel(bakery, model, getter, transform, loc, true).getQuads(null, null, Ref.RNG, EmptyModelData.INSTANCE);
        SimpleBakedModel.Builder builder = new SimpleBakedModel.Builder(model, ItemOverrideList.EMPTY, true).setTexture(getter.apply(model.resolveTextureName("particle")));
        generalQuads.forEach(builder::addGeneralQuad);
        return builder.build();
    }

    public static IBakedModel getSimpleBakedModel(IBakedModel baked) {
        Map<Direction, List<BakedQuad>> faceQuads = new Object2ObjectOpenHashMap<>();
        Arrays.stream(Ref.DIRS).forEach(d -> faceQuads.put(d, baked.getQuads(null, d, Ref.RNG, EmptyModelData.INSTANCE)));
        return new SimpleBakedModel(baked.getQuads(null, null, Ref.RNG, EmptyModelData.INSTANCE), faceQuads, baked.isAmbientOcclusion(), baked.isSideLit(), baked.isGui3d(), baked.getParticleTexture(), baked.getItemCameraTransforms(), baked.getOverrides());
    }

    public static IBakedModel getBaked(ResourceLocation loc) {
        return ModelLoader.instance().getBakedModel(loc, SimpleModelTransform.IDENTITY, ModelLoader.defaultTextureGetter());
    }

    public static IBakedModel getBakedFromState(BlockState state) {
        return Minecraft.getInstance().getModelManager().getModel(BlockModelShapes.getModelLocation(state));
    }

    public static IBakedModel getBakedFromItem(Item item) {
        return Minecraft.getInstance().getItemRenderer().getItemModelMesher().getModelManager().getModel(new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }

    public static TextureAtlasSprite getSprite(ResourceLocation loc) {
        return Minecraft.getInstance().getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE).apply(loc);
    }

    public static RenderMaterial getBlockMaterial(ResourceLocation loc) {
        return new RenderMaterial(PlayerContainer.LOCATION_BLOCKS_TEXTURE, loc);
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
