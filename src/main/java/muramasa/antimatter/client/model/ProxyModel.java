package muramasa.antimatter.client.model;

import com.mojang.datafixers.util.Pair;
import muramasa.antimatter.client.baked.ProxyBakedModel;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

public class ProxyModel extends AntimatterModel {

    public ProxyModel() {
        super();
    }

    @Override
    public IBakedModel bakeModel(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> getter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation loc) {
        return new ProxyBakedModel();
    }

    @Override
    public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> getter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation loc) {
        return super.bake(owner, bakery, getter, transform, overrides, loc);
    }

    @Override
    public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> getter, Set<Pair<String, String>> errors) {
        return super.getTextures(owner, getter, errors);
    }

    @Override
    public IModelTransform getModelTransform(IModelTransform base, int[] rots) {
        return super.getModelTransform(base, rots);
    }
}
