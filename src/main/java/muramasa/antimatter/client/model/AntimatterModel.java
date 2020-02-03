package muramasa.antimatter.client.model;

import com.mojang.datafixers.util.Pair;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.client.ModelUtils;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

public class AntimatterModel<T extends IModelGeometry<T>> implements IModelGeometry<T> {

    public IBakedModel bakeModel(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> getter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation loc) {
        return ModelUtils.getMissingModel().bakeModel(bakery, getter, transform, loc);
    }

    @Override
    public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> getter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation loc) {
        try {

            return bakeModel(owner, bakery, getter, transform, overrides, loc);
        } catch (Exception e) {
            Antimatter.LOGGER.error("ModelBaking Exception for AntimatterModel");
            e.printStackTrace();
            return ModelUtils.getMissingModel().bakeModel(bakery, getter, transform, loc);
        }
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        return Collections.emptyList();
    }
}
