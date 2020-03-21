package muramasa.antimatter.client.model;

import com.mojang.datafixers.util.Pair;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.client.ModelUtils;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.SimpleModelTransform;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.common.model.TransformationHelper;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

public class AntimatterModel implements IModelGeometry<AntimatterModel> {

    protected IUnbakedModel model;
    protected int[] rotations = new int[0];

    public AntimatterModel() {

    }

    public AntimatterModel(IUnbakedModel model, int... rotations) {
        this.model = model;
        this.rotations = rotations;
    }

    public IBakedModel bakeModel(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> getter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation loc) {
        return model != null ? model.bakeModel(bakery, getter, getModelTransform(transform, rotations), loc) : ModelUtils.getMissingModel().bakeModel(bakery, getter, transform, loc);
    }

    @Override
    public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> getter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation loc) {
        try {
            return bakeModel(owner, bakery, getter, transform, overrides, loc);
        } catch (Exception e) {
            Antimatter.LOGGER.error("ModelBaking Exception for " + owner.getModelName());
            e.printStackTrace();
            return ModelUtils.getMissingModel().bakeModel(bakery, getter, transform, loc);
        }
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> getter, Set<Pair<String, String>> errors) {
        return model != null ? model.getTextures(getter, errors) : Collections.emptyList();
    }

    public IModelTransform getModelTransform(IModelTransform base, int[] rots) {
        if (rots == null || rots.length != 3 || (rots[0] == 0 && rots[1] == 0 && rots[2] == 0)) return base;
        return new SimpleModelTransform(new TransformationMatrix(null, TransformationHelper.quatFromXYZ(new Vector3f(rots[0], rots[1], rots[2]), true), null, null));
    }
}
