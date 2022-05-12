package muramasa.antimatter.client;

import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.Ref;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.client.model.QuadTransformer;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.common.model.TransformationHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ModelUtils {

    //Assumes from North.
    public static Transformation transform(Direction side) {
        switch (side) {
            case DOWN:
                return new Transformation(null, new Quaternion(new Quaternion(new Vector3f(1.0F, 0.0F, 0.0F), 90.0F, true)), null, null);
            case UP:
                return new Transformation(null, new Quaternion(new Quaternion(new Vector3f(1.0F, 0.0F, 0.0F), -90.0F, true)), null, null);
            case NORTH:
                return Transformation.identity();
            case SOUTH:
                return new Transformation(null, new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), 180.0F, true), null, null);
            case WEST:
                return new Transformation(null, new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), 90.0f, true), null, null);
            case EAST:
                return new Transformation(null, new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), -90.0f, true), null, null);
            default:
                throw new RuntimeException("Invalid direction/null sent to transform.");
        }
    }

    public static UnbakedModel getMissingModel() {
        return ForgeModelBakery.instance().getModel(new ModelResourceLocation("builtin/missing", "missing"));
    }

    public static BakedModel getBakedFromQuads(BlockModel model, List<BakedQuad> quads, Function<Material, TextureAtlasSprite> getter) {
        SimpleBakedModel.Builder builder = new SimpleBakedModel.Builder(model, ItemOverrides.EMPTY, true).particle(getter.apply(model.getMaterial("particle")));
        quads.forEach(builder::addUnculledFace);
        return builder.build();
    }

    public static BakedModel getBakedFromModel(BlockModel model, ModelBakery bakery, Function<Material, TextureAtlasSprite> getter, ModelState transform, ResourceLocation loc) {
        List<BakedQuad> generalQuads = model.bake(bakery, model, getter, transform, loc, true).getQuads(null, null, Ref.RNG, EmptyModelData.INSTANCE);
        SimpleBakedModel.Builder builder = new SimpleBakedModel.Builder(model, ItemOverrides.EMPTY, true).particle(getter.apply(model.getMaterial("particle")));
        generalQuads.forEach(builder::addUnculledFace);
        return builder.build();
    }

    public static BakedModel getSimpleBakedModel(BakedModel baked) {
        Map<Direction, List<BakedQuad>> faceQuads = new Object2ObjectOpenHashMap<>();
        Arrays.stream(Ref.DIRS).forEach(d -> faceQuads.put(d, baked.getQuads(null, d, Ref.RNG, EmptyModelData.INSTANCE)));
        return new SimpleBakedModel(baked.getQuads(null, null, Ref.RNG, EmptyModelData.INSTANCE), faceQuads, baked.useAmbientOcclusion(), baked.usesBlockLight(), baked.isGui3d(), baked.getParticleIcon(), baked.getTransforms(), baked.getOverrides());
    }

    public static BakedModel getBaked(ResourceLocation loc) {
        return ForgeModelBakery.instance().getBakedTopLevelModels().get(loc);// SimpleModelState.IDENTITY, ForgeModelBakery.defaultTextureGetter());
    }

    public static BakedModel getBakedFromState(BlockState state) {
        return Minecraft.getInstance().getModelManager().getModel(BlockModelShaper.stateToModelLocation(state));
    }

    public static BakedModel getBakedFromItem(Item item) {
        return Minecraft.getInstance().getItemRenderer().getItemModelShaper().getModelManager().getModel(new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }

    public static TextureAtlasSprite getSprite(ResourceLocation loc) {
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(loc);
    }

    public static Material getBlockMaterial(ResourceLocation loc) {
        return new Material(InventoryMenu.BLOCK_ATLAS, loc);
    }

    public static List<BakedQuad> trans(List<BakedQuad> quads, Vector3f rotationL, Vector3f rotationR) {
        Quaternion rotL = rotationL == null ? null : TransformationHelper.quatFromXYZ(rotationL, true);
        Quaternion rotR = rotationR == null ? null : TransformationHelper.quatFromXYZ(rotationR, true);
        return trans(quads, new Transformation(new Vector3f(0, 0, 0), rotL, null, rotR));
    }

    public static List<BakedQuad> trans(List<BakedQuad> quads, Transformation transform) {
        return new QuadTransformer(transform.blockCenterToCorner()).processMany(quads);
    }
}
