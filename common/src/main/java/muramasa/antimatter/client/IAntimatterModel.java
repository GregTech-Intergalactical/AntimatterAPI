package muramasa.antimatter.client;

import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import muramasa.antimatter.Antimatter;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.common.model.TransformationHelper;

import java.util.function.Function;

public interface IAntimatterModel<T extends IAntimatterModel<T>> extends IModelGeometry<T> {

    default ModelState getModelTransform(ModelState base, int[] rots) {
        if (rots == null || rots.length != 3 || (rots[0] == 0 && rots[1] == 0 && rots[2] == 0)) return base;
        return new SimpleModelState(new Transformation(null, TransformationHelper.quatFromXYZ(new Vector3f(rots[0], rots[1], rots[2]), true), null, null));
    }

    BakedModel bakeModel(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> getter, ModelState transform, ItemOverrides overrides, ResourceLocation loc);

    @Override
    default BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> getter, ModelState transform, ItemOverrides overrides, ResourceLocation loc) {
        try {
            return bakeModel(owner, bakery, getter, transform, overrides, loc);
        } catch (Exception e) {
            Antimatter.LOGGER.error("ModelBaking Exception for " + owner.getModelName());
            e.printStackTrace();
            return ModelUtils.getMissingModel().bake(bakery, getter, transform, loc);
        }
    }
}
