package muramasa.antimatter.client.model;

import com.mojang.datafixers.util.Pair;
import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import muramasa.antimatter.client.IAntimatterModel;
import muramasa.antimatter.client.ModelUtils;
import muramasa.antimatter.client.SimpleModelState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

public class AntimatterModel implements IAntimatterModel {

    protected UnbakedModel model;
    protected int[] rotations = new int[0];

    public AntimatterModel() {

    }

    public AntimatterModel(UnbakedModel model, int... rotations) {
        this.model = model;
        this.rotations = rotations;
    }

    public BakedModel bakeModel(ModelBakery bakery, Function<Material, TextureAtlasSprite> getter, ModelState transform, ResourceLocation loc) {
        return model != null ? model.bake(bakery, getter, getModelTransform(transform, rotations), loc) : ModelUtils.getMissingModel().bake(bakery, getter, transform, loc);
    }

    @Override
    public Collection<Material> getMaterials(IModelConfiguration configuration, Function<ResourceLocation, UnbakedModel> getter, Set<Pair<String, String>> errors) {
        return model != null ? model.getMaterials(getter, errors) : Collections.emptyList();
    }

    public ModelState getModelTransform(ModelState base, int[] rots) {
        if (rots == null || rots.length != 3 || (rots[0] == 0 && rots[1] == 0 && rots[2] == 0)) return base;
        return new SimpleModelState(new Transformation(null, new Quaternion(rots[0], rots[1], rots[2], true), null, null));
    }
}
