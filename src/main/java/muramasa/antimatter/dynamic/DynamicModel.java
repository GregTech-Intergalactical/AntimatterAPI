package muramasa.antimatter.dynamic;

import com.mojang.datafixers.util.Pair;
import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.client.model.AntimatterModel;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import speiger.src.collections.ints.maps.impl.hash.Int2ObjectOpenHashMap;
import speiger.src.collections.objects.sets.ObjectOpenHashSet;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

public class DynamicModel extends AntimatterModel {

    protected AntimatterModel modelDefault;
    protected Int2ObjectOpenHashMap<IModelGeometry<?>[]> modelConfigs;
    protected String staticMapId;

    public DynamicModel(AntimatterModel modelDefault, Int2ObjectOpenHashMap<IModelGeometry<?>[]> modelConfigs, String staticMapId) {
        this.modelDefault = modelDefault;
        this.modelConfigs = modelConfigs;
        this.staticMapId = staticMapId;
    }

    public DynamicModel(DynamicModel copy) {
        this.modelDefault = copy.modelDefault;
        this.modelConfigs = copy.modelConfigs;
        this.staticMapId = copy.staticMapId;
    }

    @Override
    public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> getter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation loc) {
        IBakedModel baked = super.bake(owner, bakery, getter, transform, overrides, loc);
        if (baked instanceof DynamicBakedModel) ((DynamicBakedModel) baked).particle(((DynamicBakedModel) baked).getBakedDefault().getParticleTexture(EmptyModelData.INSTANCE));
        return baked;
    }

    @Override
    public IBakedModel bakeModel(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> getter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation loc) {
        return new DynamicBakedModel(getBakedConfigs(owner, bakery, getter, transform, overrides, loc));
    }

    public Tuple<IBakedModel, Int2ObjectOpenHashMap<IBakedModel[]>> getBakedConfigs(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> getter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation loc) {
        Int2ObjectOpenHashMap<IBakedModel[]> bakedConfigs = AntimatterModelManager.getStaticConfigMap(staticMapId);
        modelConfigs.forEach((k, v) -> {
            IBakedModel[] baked = new IBakedModel[v.length];
            for (int i = 0; i < baked.length; i++) {
                baked[i] = v[i].bake(owner, bakery, getter, transform, overrides, loc);
            }
            bakedConfigs.put((int)k, baked);
        });
        return new Tuple<>(modelDefault.bakeModel(owner, bakery, getter, transform, overrides, loc), bakedConfigs);
    }

    @Override
    public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> getter, Set<Pair<String, String>> errors) {
        Set<RenderMaterial> textures = new ObjectOpenHashSet<>();
        modelConfigs.values().forEach(v -> Arrays.stream(v).forEach(m -> textures.addAll(m.getTextures(owner, getter, errors))));
        textures.addAll(modelDefault.getTextures(owner, getter, errors));
        return textures;
    }
}
