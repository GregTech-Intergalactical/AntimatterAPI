package muramasa.antimatter.client.model;

import com.mojang.datafixers.util.Pair;
import muramasa.antimatter.client.ModelBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

public class TestModel implements IModelGeometry<TestModel> {

    public TestModel() {

    }

    @Override
    public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> getter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation loc) {
        //Function<ModelBuilder, AntimatterModel> modelFunc = AntimatterModelLoader.get(owner.getModelName());
        //if (modelFunc != null) return modelFunc.apply(new ModelBuilder()).bake(owner, bakery, getter, transform, overrides, loc);
        //return owner.getOwnerModel().func_225613_a_(bakery, getter, transform, loc);
        return new ModelBuilder().of("block/preset/simple").tex("all", Blocks.DIAMOND_BLOCK).bake(owner, bakery, getter, transform, overrides, loc);
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        return Collections.emptyList();
    }
}
