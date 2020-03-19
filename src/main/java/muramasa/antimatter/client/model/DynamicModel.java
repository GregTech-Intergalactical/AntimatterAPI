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
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.SimpleModelTransform;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.common.model.TransformationHelper;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class DynamicModel extends AntimatterModel {

    protected AntimatterModel modelBase;
    protected Int2ObjectOpenHashMap<Triple<String, IUnbakedModel, int[]>> modelConfigs;

    public DynamicModel(AntimatterModel modelBase, Int2ObjectOpenHashMap<Triple<String, IUnbakedModel, int[]>> modelConfigs) {
        this.modelBase = modelBase;
        this.modelConfigs = modelConfigs;
    }

    @Override
    public IBakedModel bakeModel(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> getter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation loc) {
        IBakedModel bakedDefault = modelBase.bakeModel(owner, bakery, getter, transform, overrides, loc);
        Int2ObjectOpenHashMap<IBakedModel> bakedConfigs = new Int2ObjectOpenHashMap<>();
        modelConfigs.forEach((k, v) -> bakedConfigs.put((int)k, AntimatterModelManager.getBaked(v.getLeft(), () -> v.getMiddle().bakeModel(bakery, getter, getModelTransform(transform, v.getRight()), loc))));
        return new DynamicBakedModel(bakedDefault, bakedConfigs).particle(bakedDefault.getParticleTexture(EmptyModelData.INSTANCE));
    }

    //TODO should rotations be handled by AntimatterModel?
    public IModelTransform getModelTransform(IModelTransform base, int[] rots) {
        if (rots[0] == 0 && rots[1] == 0 && rots[2] == 0) return base;
        return new SimpleModelTransform(new TransformationMatrix(null, TransformationHelper.quatFromXYZ(new Vector3f(rots[0], rots[1], rots[2]), true), null, null));
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> getter, Set<Pair<String, String>> errors) {
        Set<Material> textures = new HashSet<>();
        modelConfigs.values().forEach(t -> textures.addAll(t.getMiddle().getTextures(getter, errors)));
        textures.addAll(modelBase.getTextures(owner, getter, errors));
        return textures;
    }
}
