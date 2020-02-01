package muramasa.antimatter.client.model;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

public class LoaderModel implements IModelGeometry<LoaderModel> {

    protected BlockModel base;

    public LoaderModel(BlockModel base) {
        this.base = base;
    }

    @Override
    public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> getter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation loc) {
        //Tuple<Block, Function<ModelBuilder, AntimatterModel>> override = AntimatterModelLoader.get(owner.getModelName());
        //ModelBuilder builder = base != null ? new ModelBuilder(base) : new ModelBuilder().simple();
        //if (override != null) return override.getB().apply(builder).bake(owner, bakery, getter, transform, overrides, loc);
        //return builder.bake(owner, bakery, getter, transform, overrides, loc);
        return base.bakeModel(bakery, getter, transform, loc);
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> getter, Set<Pair<String, String>> errors) {
        return base.getTextures(getter, errors);
    }
}
