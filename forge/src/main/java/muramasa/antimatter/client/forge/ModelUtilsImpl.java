package muramasa.antimatter.client.forge;

import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.Ref;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.client.model.QuadTransformer;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.common.model.TransformationHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ModelUtilsImpl {

    public static Function<ResourceLocation, UnbakedModel> getDefaultModelGetter(){
        return ForgeModelBakery.defaultModelGetter();
    }

    public static Function<Material, TextureAtlasSprite> getDefaultTextureGetter(){
        return ForgeModelBakery.defaultTextureGetter();
    }

    public static ModelBakery getModelBakery(){
        return ForgeModelBakery.instance();
    }

    public static void setLightData(BakedQuad quad, int light){
        LightUtil.setLightData(quad, light);
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

    public static Quaternion quatFromXYZ(Vector3f xyz, boolean degrees){
        return TransformationHelper.quatFromXYZ(xyz, degrees);
    }

    public static List<BakedQuad> trans(List<BakedQuad> quads, Transformation transform) {
        return new QuadTransformer(transform.blockCenterToCorner()).processMany(quads);
    }

    public static void setRenderLayer(Block block, RenderType renderType){
        ItemBlockRenderTypes.setRenderLayer(block, renderType);
    }

    public static void setRenderLayer(Fluid fluid, RenderType renderType){
        ItemBlockRenderTypes.setRenderLayer(fluid, renderType);
    }
}
