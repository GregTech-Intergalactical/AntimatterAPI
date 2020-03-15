package muramasa.antimatter.client.model;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.client.baked.DynamicBakedModel;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
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

public TransformationMatrix getRotation(Direction dir) {
    switch (dir) {
        case DOWN: return new TransformationMatrix(new Vector3f(0, 0, 0), TransformationHelper.quatFromXYZ(new Vector3f(4.7124f, 0, 0), false), null, null);
        case UP: return new TransformationMatrix(new Vector3f(0, 0, 0), TransformationHelper.quatFromXYZ(new Vector3f(1.5708f, 0, 0), false), null, null);
        case NORTH: return new TransformationMatrix(new Vector3f(0, 0, 0), TransformationHelper.quatFromXYZ(new Vector3f(0, 0f, 0), false), null, null);
        case SOUTH: return new TransformationMatrix(new Vector3f(0, 0, 0), TransformationHelper.quatFromXYZ(new Vector3f(0, 3.1416f, 0), false), null, null);
        case WEST: return new TransformationMatrix(new Vector3f(0, 0, 0), TransformationHelper.quatFromXYZ(new Vector3f(0, 1.5708f, 0), false), null, null);
        case EAST: return new TransformationMatrix(new Vector3f(0, 0, 0), TransformationHelper.quatFromXYZ(new Vector3f(0, 4.7124f, 0), false), null, null);
        default: throw new IllegalStateException("Unhandled direction!");
    }
}

public IModelTransform getModelTransform(IModelTransform base, Direction[] rotations) {
    if (rotations == null || rotations.length == 0) return base;
    TransformationMatrix mat = base.getRotation().blockCornerToCenter();
    for (int i = 0; i < rotations.length; i++) {
        mat = mat.compose(getRotation(rotations[i]));
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
