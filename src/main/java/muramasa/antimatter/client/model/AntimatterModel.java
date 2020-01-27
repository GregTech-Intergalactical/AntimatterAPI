package muramasa.antimatter.client.model;

import com.mojang.datafixers.util.Pair;
import muramasa.antimatter.client.ModelBuilder;
import muramasa.antimatter.client.ModelUtils;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class AntimatterModel implements IModelGeometry<AntimatterModel> {

    protected ModelBuilder baseBuilder;
    protected Set<Material> allTextures = new HashSet<>();
    protected ResourceLocation particle = ModelUtils.ERROR;

    public AntimatterModel() {
        baseBuilder = new ModelBuilder().simple();
    }

    public AntimatterModel(ModelBuilder builder) {
        baseBuilder = builder;
    }

    public void add(ResourceLocation... textures) {
        Arrays.stream(textures).forEach(t -> allTextures.add(ModelUtils.getBlockMaterial(t)));
    }

    @Override
    public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> getter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation loc) {
        return baseBuilder.bake(owner, bakery, getter, transform, overrides, loc);
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        return allTextures;
    }
}
