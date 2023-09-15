package muramasa.antimatter.client.model;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import muramasa.antimatter.client.IAntimatterModel;
import muramasa.antimatter.client.baked.BakedMachineSide;
import muramasa.antimatter.client.baked.CoverBakedModel;
import muramasa.antimatter.client.baked.GroupedBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AntimatterGroupedModel implements IAntimatterModel {
    final Map<String, IAntimatterModel> models;
    final ResourceLocation particle;

    public AntimatterGroupedModel(ResourceLocation particle, Map<String, IAntimatterModel> models) {
        this.models = models;
        this.particle = particle;
    }

    @Override
    public BakedModel bakeModel(ModelBakery bakery, Function<Material, TextureAtlasSprite> getter, ModelState transform, ResourceLocation loc) {
        return null;
    }

    @Override
    public BakedModel bakeModel(IModelConfiguration configuration, ModelBakery bakery, Function<Material, TextureAtlasSprite> getter, ModelState transform, ItemOverrides overrides, ResourceLocation loc) {
        ImmutableMap.Builder<String, BakedModel> builder = buildParts(configuration, bakery, getter, transform, overrides, loc, this.models.entrySet());
        return new GroupedBakedModel(getter.apply(new Material(InventoryMenu.BLOCK_ATLAS, MissingTextureAtlasSprite.getLocation())), builder.build());
    }

    protected static ImmutableMap.Builder<String, BakedModel> buildParts(IModelConfiguration configuration, ModelBakery bakery, Function<Material, TextureAtlasSprite> getter, ModelState transform, ItemOverrides overrides, ResourceLocation loc, Set<Map.Entry<String, IAntimatterModel>> entries) {
        ImmutableMap.Builder<String, BakedModel> builder = ImmutableMap.builder();
        for (Map.Entry<String, IAntimatterModel> entry : entries) {
            builder.put(entry.getKey(), entry.getValue().bake(configuration, bakery, getter, transform, overrides, loc));
        }
        return builder;
    }

    @Override
    public Collection<Material> getMaterials(IModelConfiguration configuration, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        return models.values().stream().flatMap(t -> t.getMaterials(configuration, modelGetter, missingTextureErrors).stream()).collect(Collectors.toList());
    }

    public static class CoverModel extends AntimatterGroupedModel {

        public CoverModel(AntimatterGroupedModel model) {
            super(model.particle, model.models);
        }

        @Override
        public BakedModel bakeModel(IModelConfiguration configuration, ModelBakery bakery, Function<Material, TextureAtlasSprite> getter, ModelState transform, ItemOverrides overrides, ResourceLocation loc) {
            ImmutableMap.Builder<String, BakedModel> builder = buildParts(configuration, bakery, getter, transform, overrides, loc, this.models.entrySet());
            return new CoverBakedModel(getter.apply(new Material(InventoryMenu.BLOCK_ATLAS, MissingTextureAtlasSprite.getLocation())), builder.build());
        }
    }

    public static class MachineSideModel extends AntimatterGroupedModel {

        public MachineSideModel(AntimatterGroupedModel model) {
            super(model.particle, model.models);
        }

        @Override
        public BakedModel bakeModel(IModelConfiguration configuration, ModelBakery bakery, Function<Material, TextureAtlasSprite> getter, ModelState transform, ItemOverrides overrides, ResourceLocation loc) {
            ImmutableMap.Builder<String, BakedModel> builder = buildParts(configuration, bakery, getter, transform, overrides, loc, this.models.entrySet());
            return new BakedMachineSide(getter.apply(new Material(InventoryMenu.BLOCK_ATLAS, MissingTextureAtlasSprite.getLocation())), builder.build());
        }
    }
}
