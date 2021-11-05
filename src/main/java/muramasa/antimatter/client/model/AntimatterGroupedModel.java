package muramasa.antimatter.client.model;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import muramasa.antimatter.client.IAntimatterModel;
import muramasa.antimatter.client.baked.CoverBakedModel;
import muramasa.antimatter.client.baked.GroupedBakedModel;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AntimatterGroupedModel implements IAntimatterModel<AntimatterGroupedModel> {
    final Map<String, IModelGeometry<?>> models;
    final ResourceLocation particle;

    public AntimatterGroupedModel(ResourceLocation particle, Map<String, IModelGeometry<?>> models) {
        this.models = models;
        this.particle = particle;
    }

    @Override
    public IBakedModel bakeModel(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> getter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation loc) {
        ImmutableMap.Builder<String, IBakedModel> builder = buildParts(owner, bakery, getter, transform, overrides, loc, this.models.entrySet());
        return new GroupedBakedModel(getter.apply(new RenderMaterial(PlayerContainer.LOCATION_BLOCKS_TEXTURE, MissingTextureSprite.getLocation())), builder.build());
    }

    protected static ImmutableMap.Builder<String, IBakedModel> buildParts(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> getter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation loc, Set<Map.Entry<String, IModelGeometry<?>>> entries) {
        ImmutableMap.Builder<String, IBakedModel> builder = ImmutableMap.builder();
        for (Map.Entry<String, IModelGeometry<?>> entry : entries) {
            builder.put(entry.getKey(), entry.getValue().bake(owner, bakery, getter, transform, overrides, loc));
        }
        return builder;
    }

    @Override
    public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        return models.values().stream().flatMap(t -> t.getTextures(owner, modelGetter, missingTextureErrors).stream()).collect(Collectors.toList());
    }

    public static class CoverModel extends AntimatterGroupedModel {

        public CoverModel(AntimatterGroupedModel model) {
            super(model.particle, model.models);
        }

        @Override
        public IBakedModel bakeModel(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> getter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation loc) {
            ImmutableMap.Builder<String, IBakedModel> builder = buildParts(owner, bakery, getter, transform, overrides, loc, this.models.entrySet());
            return new CoverBakedModel(getter.apply(new RenderMaterial(PlayerContainer.LOCATION_BLOCKS_TEXTURE, MissingTextureSprite.getLocation())), builder.build());
        }
    }
}
