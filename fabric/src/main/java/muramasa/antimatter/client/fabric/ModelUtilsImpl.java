package muramasa.antimatter.client.fabric;

import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import io.github.fabricators_of_create.porting_lib.util.TransformationHelper;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.Ref;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ModelUtilsImpl {
    /*public static UnbakedModel getMissingModel() {
        return ForgeModelBakery.instance().getModel(new ModelResourceLocation("builtin/missing", "missing"));
    }

    public static UnbakedModel getModel(ResourceLocation resourceLocation){
        return ForgeModelBakery.instance().getModel(resourceLocation);
    }

    public static Function<ResourceLocation, UnbakedModel> getDefaultModelGetter(){
        return ForgeModelBakery.defaultModelGetter();
    }

    public static Function<Material, TextureAtlasSprite> getDefaultTextureGetter(){
        return ForgeModelBakery.defaultTextureGetter();
    }

    public static ModelBakery getModelBakery(){
        return ForgeModelBakery.instance();
    }*/

    public static void setLightData(BakedQuad quad, int light){
        int[] data = quad.getVertices();
        for (int i = 0; i < 4; i++)
        {
            data[getLightOffset(i)] = light;
        }
    }

    private static int getLightOffset(int v)
    {
        return (v * 8) + 6;
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

    /*public static BakedModel getBaked(ResourceLocation loc) {
        return ForgeModelBakery.instance().getBakedTopLevelModels().get(loc);// SimpleModelState.IDENTITY, ForgeModelBakery.defaultTextureGetter());
    }*/

    public static Quaternion quatFromXYZ(Vector3f xyz, boolean degrees){
        return TransformationHelper.quatFromXYZ(xyz, degrees);
    }

    /*public static List<BakedQuad> trans(List<BakedQuad> quads, Transformation transform) {
        return new QuadTransformer(transform.blockCenterToCorner()).processMany(quads);
    }*/

    public static void setRenderLayer(Block block, RenderType renderType){
        BlockRenderLayerMap.INSTANCE.putBlock(block, renderType);
    }

    public static void setRenderLayer(Fluid fluid, RenderType renderType){
        BlockRenderLayerMap.INSTANCE.putFluid(fluid, renderType);
    }
}
