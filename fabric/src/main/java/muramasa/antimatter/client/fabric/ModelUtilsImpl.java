package muramasa.antimatter.client.fabric;

import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import io.github.fabricators_of_create.porting_lib.mixin.client.accessor.SimpleBakedModel$BuilderAccessor;
import io.github.fabricators_of_create.porting_lib.util.LightUtil;
import io.github.fabricators_of_create.porting_lib.util.TransformationHelper;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.Ref;
import muramasa.antimatter.client.ModelUtils;
import muramasa.antimatter.client.baked.IAntimatterBakedModel;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

public class ModelUtilsImpl {
    public static ModelBakery BAKERY = null;

    public static Function<ResourceLocation, UnbakedModel> getDefaultModelGetter(){
        return ModelUtilsImpl::getModelOrMissing;
    }

    private static UnbakedModel getModelOrMissing(ResourceLocation location){
        try
        {
            return ModelUtils.getModel(location);
        }
        catch(Exception e)
        {
            return ModelUtils.getMissingModel();
        }
    }

    public static Function<Material, TextureAtlasSprite> getDefaultTextureGetter(){
        return Material::sprite;
    }

    public static ModelBakery getModelBakery(){
        return BAKERY;
    }

    public static void setLightData(BakedQuad quad, int light){
        LightUtil.setLightData(quad, light);
    }


    public static SimpleBakedModel.Builder createSimpleModelBuilder(boolean smoothLighting, boolean sideLit, boolean isShadedInGui, ItemTransforms transforms, ItemOverrides overrides){
        return SimpleBakedModel$BuilderAccessor.port_lib$create(smoothLighting, sideLit, isShadedInGui, transforms, overrides);
    }
    public static List<BakedQuad> getQuadsFromBaked(BakedModel model, BlockState state, @Nullable Direction side, @NotNull Random rand, @NotNull BlockAndTintGetter level, @NotNull BlockPos pos){
        if (model instanceof IAntimatterBakedModel antimatterBaked){
            return antimatterBaked.getQuads(state, side, rand, level, pos);
        } else {
            return model.getQuads(state, side, rand);
        }
    }
    public static BakedModel getBakedFromModel(BlockModel model, ModelBakery bakery, Function<Material, TextureAtlasSprite> getter, ModelState transform, ResourceLocation loc) {
        List<BakedQuad> generalQuads = model.bake(bakery, model, getter, transform, loc, true).getQuads(null, null, Ref.RNG);
        SimpleBakedModel.Builder builder = new SimpleBakedModel.Builder(model, ItemOverrides.EMPTY, true).particle(getter.apply(model.getMaterial("particle")));
        generalQuads.forEach(builder::addUnculledFace);
        return builder.build();
    }

    public static BakedModel getSimpleBakedModel(BakedModel baked) {
        Map<Direction, List<BakedQuad>> faceQuads = new Object2ObjectOpenHashMap<>();
        Arrays.stream(Ref.DIRS).forEach(d -> faceQuads.put(d, baked.getQuads(null, d, Ref.RNG)));
        return new SimpleBakedModel(baked.getQuads(null, null, Ref.RNG), faceQuads, baked.useAmbientOcclusion(), baked.usesBlockLight(), baked.isGui3d(), baked.getParticleIcon(), baked.getTransforms(), baked.getOverrides());
    }

    public static Quaternion quatFromXYZ(Vector3f xyz, boolean degrees){
        return TransformationHelper.quatFromXYZ(xyz, degrees);
    }

    public static List<BakedQuad> trans(List<BakedQuad> quads, Transformation transform) {
        return new QuadTransformer(transform.blockCenterToCorner()).processMany(quads);
    }

    public static void setRenderLayer(Block block, RenderType renderType){
        BlockRenderLayerMap.INSTANCE.putBlock(block, renderType);
    }

    public static void setRenderLayer(Fluid fluid, RenderType renderType){
        BlockRenderLayerMap.INSTANCE.putFluid(fluid, renderType);
    }

    public static void registerProperty(Item item, ResourceLocation location, ClampedItemPropertyFunction function){
        ItemProperties.register(item, location, function);
    }
}
