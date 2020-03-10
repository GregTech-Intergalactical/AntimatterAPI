package muramasa.antimatter.client.model;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.client.ModelUtils;
import muramasa.antimatter.client.baked.DynamicBakedModel;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.SimpleModelTransform;
import net.minecraftforge.client.model.data.EmptyModelData;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class DynamicModel extends AntimatterModel {

    protected IUnbakedModel modelDefault;
    protected Int2ObjectOpenHashMap<Triple<String, IUnbakedModel, Direction[]>> modelConfigs;

    public DynamicModel(IUnbakedModel modelDefault, Int2ObjectOpenHashMap<Triple<String, IUnbakedModel, Direction[]>> modelConfigs) {
        this.modelDefault = modelDefault;
        this.modelConfigs = modelConfigs;
    }

    @Override
    public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> getter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation loc) {
        IBakedModel bakedDefault = modelDefault.bakeModel(bakery, getter, transform, loc);
        Int2ObjectOpenHashMap<IBakedModel> bakedConfigs = new Int2ObjectOpenHashMap<>();
        modelConfigs.forEach((k, v) -> bakedConfigs.put((int)k, AntimatterModelManager.getBaked(v.getLeft(), () -> v.getMiddle().bakeModel(bakery, getter, getModelTransform(transform, v.getRight()), loc))));
        return new DynamicBakedModel(bakedDefault, bakedConfigs).particle(bakedDefault.getParticleTexture(EmptyModelData.INSTANCE));
    }

    public IModelTransform getModelTransform(IModelTransform base, Direction[] rotations) {
        TransformationMatrix mat = base.getRotation().blockCenterToCorner();
        if (rotations != null && rotations.length > 0) {
            for (Direction rot : rotations) {
                mat = mat.compose(new TransformationMatrix(ModelUtils.FACING_TO_MATRIX[rot.getIndex()]));
            }
        }
        return new SimpleModelTransform(mat);
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> getter, Set<Pair<String, String>> errors) {
        Set<Material> textures = new HashSet<>();
        modelConfigs.values().forEach(t -> textures.addAll(t.getMiddle().getTextures(getter, errors)));
        textures.addAll(modelDefault.getTextures(getter, errors));
        return textures;
    }
}
