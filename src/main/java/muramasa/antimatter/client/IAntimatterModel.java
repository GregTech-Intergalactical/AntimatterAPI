package muramasa.antimatter.client;

import muramasa.antimatter.Antimatter;
import muramasa.antimatter.dynamic.DynamicBakedModel;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.SimpleModelTransform;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.common.model.TransformationHelper;

import java.util.function.Function;

public interface IAntimatterModel<T extends IAntimatterModel<T>> extends IModelGeometry<T> {

    default IModelTransform getModelTransform(IModelTransform base, int[] rots) {
        if (rots == null || rots.length != 3 || (rots[0] == 0 && rots[1] == 0 && rots[2] == 0)) return base;
        return new SimpleModelTransform(new TransformationMatrix(null, TransformationHelper.quatFromXYZ(new Vector3f(rots[0], rots[1], rots[2]), true), null, null));
    }
    IBakedModel bakeModel(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> getter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation loc);
    @Override
    default IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> getter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation loc) {
        try {
            return bakeModel(owner, bakery, getter, transform, overrides, loc);
        } catch (Exception e) {
            Antimatter.LOGGER.error("ModelBaking Exception for " + owner.getModelName());
            e.printStackTrace();
            return ModelUtils.getMissingModel().bakeModel(bakery, getter, transform, loc);
        }
    }
}
