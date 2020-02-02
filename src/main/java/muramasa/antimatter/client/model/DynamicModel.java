package muramasa.antimatter.client.model;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.client.baked.DynamicBaked;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class DynamicModel implements IModelGeometry<DynamicModel> {

    protected IUnbakedModel baseModel;
    protected Int2ObjectOpenHashMap<IUnbakedModel> configModels;

    public DynamicModel(IUnbakedModel baseModel, Int2ObjectOpenHashMap<IUnbakedModel> configModels) {
        this.baseModel = baseModel;
        this.configModels = configModels;
    }

    @Override
    public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> getter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation loc) {
        IBakedModel baseModel = this.baseModel.bakeModel(bakery, getter, transform, loc);
        Int2ObjectOpenHashMap<IBakedModel> bakedModels = new Int2ObjectOpenHashMap<>();
        configModels.forEach((k, v) -> bakedModels.put((int)k, v.bakeModel(bakery, getter, transform, loc)));
        return new DynamicBaked(baseModel, bakedModels, baseModel.getParticleTexture(EmptyModelData.INSTANCE));
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> getter, Set<Pair<String, String>> errors) {
        Set<Material> textures = new HashSet<>();
        configModels.values().forEach(m -> textures.addAll(m.getTextures(getter, errors)));
        textures.addAll(baseModel.getTextures(getter, errors));
        return textures;
    }
}
