package muramasa.antimatter.client.model;

import com.mojang.datafixers.util.Pair;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.client.IAntimatterModel;
import muramasa.antimatter.client.ModelUtils;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.common.model.TransformationHelper;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

public class AntimatterModel implements IAntimatterModel<AntimatterModel> {

    protected UnbakedModel model;
    protected int[] rotations = new int[0];

    public AntimatterModel() {

    }

    public AntimatterModel(UnbakedModel model, int... rotations) {
        this.model = model;
        this.rotations = rotations;
    }

    public BakedModel bakeModel(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> getter, ModelState transform, ItemOverrides overrides, ResourceLocation loc) {
        return model != null ? model.bake(bakery, getter, getModelTransform(transform, rotations), loc) : ModelUtils.getMissingModel().bake(bakery, getter, transform, loc);
    }

    @Override
    public final BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> getter, ModelState transform, ItemOverrides overrides, ResourceLocation loc) {
        try {
            return bakeModel(owner, bakery, getter, transform, overrides, loc);
        } catch (Exception e) {
            Antimatter.LOGGER.error("ModelBaking Exception for " + owner.getModelName());
            e.printStackTrace();
            return ModelUtils.getMissingModel().bake(bakery, getter, transform, loc);
        }
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> getter, Set<Pair<String, String>> errors) {
        return model != null ? model.getMaterials(getter, errors) : Collections.emptyList();
    }

    public ModelState getModelTransform(ModelState base, int[] rots) {
        if (rots == null || rots.length != 3 || (rots[0] == 0 && rots[1] == 0 && rots[2] == 0)) return base;
        return new SimpleModelState(new Transformation(null, TransformationHelper.quatFromXYZ(new Vector3f(rots[0], rots[1], rots[2]), true), null, null));
    }
}
