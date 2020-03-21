package muramasa.antimatter.client.model;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.client.baked.DynamicBakedModel;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.SimpleModelTransform;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.common.model.TransformationHelper;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class DynamicModel extends AntimatterModel {

    protected AntimatterModel modelDefault;
    protected Int2ObjectOpenHashMap<Tuple<IUnbakedModel, int[]>> modelConfigs;
    protected String staticMapId;

    public DynamicModel(AntimatterModel modelDefault, Int2ObjectOpenHashMap<Tuple<IUnbakedModel, int[]>> modelConfigs, String staticMapId) {
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
    public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> getter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation loc) {
        IBakedModel baked = super.bake(owner, bakery, getter, transform, overrides, loc);
        if (baked instanceof DynamicBakedModel) ((DynamicBakedModel) baked).particle(((DynamicBakedModel) baked).getBakedDefault().getParticleTexture(EmptyModelData.INSTANCE));
        return baked;
    }

    @Override
    public IBakedModel bakeModel(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> getter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation loc) {
        return new DynamicBakedModel(getBakedConfigs(owner, bakery, getter, transform, overrides, loc));
    }

    public Tuple<IBakedModel, Int2ObjectOpenHashMap<IBakedModel>> getBakedConfigs(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> getter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation loc) {
        Int2ObjectOpenHashMap<IBakedModel> bakedConfigs = AntimatterModelManager.getStaticConfigMap(staticMapId);
        modelConfigs.forEach((k, v) -> bakedConfigs.put((int)k, v.getA().bakeModel(bakery, getter, getModelTransform(transform, v.getB()), loc)));
        return new Tuple<>(modelDefault.bakeModel(owner, bakery, getter, transform, overrides, loc), bakedConfigs);
    }

    //TODO should rotations be handled by AntimatterModel?
    public IModelTransform getModelTransform(IModelTransform base, int[] rots) {
        if (rots[0] == 0 && rots[1] == 0 && rots[2] == 0) return base;
        return new SimpleModelTransform(new TransformationMatrix(null, TransformationHelper.quatFromXYZ(new Vector3f(rots[0], rots[1], rots[2]), true), null, null));
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> getter, Set<Pair<String, String>> errors) {
        Set<Material> textures = new HashSet<>();
        modelConfigs.values().forEach(t -> textures.addAll(t.getA().getTextures(getter, errors)));
        textures.addAll(modelDefault.getTextures(owner, getter, errors));
        return textures;
    }
}
