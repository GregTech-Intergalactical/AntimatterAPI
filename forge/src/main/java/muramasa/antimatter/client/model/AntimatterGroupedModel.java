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
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
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
    public BakedModel bakeModel(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> getter, ModelState transform, ItemOverrides overrides, ResourceLocation loc) {
        ImmutableMap.Builder<String, BakedModel> builder = buildParts(owner, bakery, getter, transform, overrides, loc, this.models.entrySet());
        return new GroupedBakedModel(getter.apply(new Material(InventoryMenu.BLOCK_ATLAS, MissingTextureAtlasSprite.getLocation())), builder.build());
    }

    protected static ImmutableMap.Builder<String, BakedModel> buildParts(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> getter, ModelState transform, ItemOverrides overrides, ResourceLocation loc, Set<Map.Entry<String, IModelGeometry<?>>> entries) {
        ImmutableMap.Builder<String, BakedModel> builder = ImmutableMap.builder();
        for (Map.Entry<String, IModelGeometry<?>> entry : entries) {
            builder.put(entry.getKey(), entry.getValue().bake(owner, bakery, getter, transform, overrides, loc));
        }
        return builder;
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        return models.values().stream().flatMap(t -> t.getTextures(owner, modelGetter, missingTextureErrors).stream()).collect(Collectors.toList());
    }

    public static class CoverModel extends AntimatterGroupedModel {

        public CoverModel(AntimatterGroupedModel model) {
            super(model.particle, model.models);
        }

        @Override
        public BakedModel bakeModel(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> getter, ModelState transform, ItemOverrides overrides, ResourceLocation loc) {
            ImmutableMap.Builder<String, BakedModel> builder = buildParts(owner, bakery, getter, transform, overrides, loc, this.models.entrySet());
            return new CoverBakedModel(getter.apply(new Material(InventoryMenu.BLOCK_ATLAS, MissingTextureAtlasSprite.getLocation())), builder.build());
        }
    }

    public static class MachineSideModel extends AntimatterGroupedModel {

        public MachineSideModel(AntimatterGroupedModel model) {
            super(model.particle, model.models);
        }

        @Override
        public BakedModel bakeModel(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> getter, ModelState transform, ItemOverrides overrides, ResourceLocation loc) {
            ImmutableMap.Builder<String, BakedModel> builder = buildParts(owner, bakery, getter, transform, overrides, loc, this.models.entrySet());
            return new BakedMachineSide(getter.apply(new Material(InventoryMenu.BLOCK_ATLAS, MissingTextureAtlasSprite.getLocation())), builder.build());
        }
    }
}
