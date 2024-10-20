package muramasa.antimatter.client;

import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import muramasa.antimatter.client.baked.CoverBakedModel;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import muramasa.antimatter.util.ImplLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;

public interface ModelUtils {
    ModelUtils INSTANCE = ImplLoader.load(ModelUtils.class);

    //Assumes from North.
    static Transformation transform(Direction side) {
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

    static UnbakedModel getMissingModel() {
        return INSTANCE.getModelBakery().getModel(new ModelResourceLocation("builtin/missing", "missing"));
    }


    static UnbakedModel getModel(ResourceLocation resourceLocation){
        return INSTANCE.getModelBakery().getModel(resourceLocation);
    }


    SimpleBakedModel.Builder createSimpleModelBuilder(boolean smoothLighting, boolean sideLit, boolean isShadedInGui, ItemTransforms transforms, ItemOverrides overrides);

    static Function<ResourceLocation, UnbakedModel> getDefaultModelGetter(){
        return ModelUtils::getModelOrMissing;
    }

    private static UnbakedModel getModelOrMissing(ResourceLocation location){
        try {
            return ModelUtils.getModel(location);
        }
        catch(Exception e) {
            return ModelUtils.getMissingModel();
        }
    }

    static Function<Material, TextureAtlasSprite> getDefaultTextureGetter(){
        return Material::sprite;
    }

    ModelBakery getModelBakery();

    void setLightData(BakedQuad model, int light);

    List<BakedQuad> getQuadsFromBaked(BakedModel model, BlockState state, @Nullable Direction side, @NotNull Random rand, @NotNull BlockAndTintGetter level, @NotNull BlockPos pos);

    static List<BakedQuad> getQuadsFromBakedCover(BakedModel model, BlockState state, @Nullable Direction side, @NotNull Random rand, @NotNull BlockAndTintGetter level, @NotNull BlockPos pos, Predicate<Map.Entry<String, BakedModel>> coverPredicate){
        if (model instanceof CoverBakedModel coverBakedModel){
            return coverBakedModel.getBlockQuads(state, side, rand, level, pos, coverPredicate);
        }
        return INSTANCE.getQuadsFromBaked(model, state, side, rand, level, pos);
    }

    static BakedModel getBakedFromQuads(BlockModel model, List<BakedQuad> quads, Function<Material, TextureAtlasSprite> getter) {
        SimpleBakedModel.Builder builder = new SimpleBakedModel.Builder(model, ItemOverrides.EMPTY, true).particle(getter.apply(model.getMaterial("particle")));
        quads.forEach(builder::addUnculledFace);
        return builder.build();
    }

    BakedModel getBakedFromModel(BlockModel model, ModelBakery bakery, Function<Material, TextureAtlasSprite> getter, ModelState transform, ResourceLocation loc);

    BakedModel getSimpleBakedModel(BakedModel baked);

    static BakedModel getBaked(ResourceLocation loc) {
        return INSTANCE.getModelBakery().getBakedTopLevelModels().get(loc);// SimpleModelState.IDENTITY, ForgeModelBakery.defaultTextureGetter());
    }

    static BakedModel getBakedFromState(BlockState state) {
        return Minecraft.getInstance().getModelManager().getModel(BlockModelShaper.stateToModelLocation(state));
    }

    static BakedModel getBakedFromItem(Item item) {
        return Minecraft.getInstance().getItemRenderer().getItemModelShaper().getModelManager().getModel(new ModelResourceLocation(AntimatterPlatformUtils.INSTANCE.getIdFromItem(item), "inventory"));
    }

    static TextureAtlasSprite getSprite(ResourceLocation loc) {
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(loc);
    }

    static Material getBlockMaterial(ResourceLocation loc) {
        return new Material(InventoryMenu.BLOCK_ATLAS, loc);
    }

    static List<BakedQuad> trans(List<BakedQuad> quads, Vector3f rotationL, Vector3f rotationR) {
        Quaternion rotL = rotationL == null ? null : INSTANCE.quatFromXYZ(rotationL, true);
        Quaternion rotR = rotationR == null ? null : INSTANCE.quatFromXYZ(rotationR, true);
        return INSTANCE.trans(quads, new Transformation(new Vector3f(0, 0, 0), rotL, null, rotR));
    }

    Quaternion quatFromXYZ(Vector3f xyz, boolean degrees);

    List<BakedQuad> trans(List<BakedQuad> quads, Transformation transform);

    void setRenderLayer(Block block, RenderType renderType);

    void setRenderLayer(Fluid fluid, RenderType renderType);

    void registerProperty(Item item, ResourceLocation location, ClampedItemPropertyFunction function);
}
