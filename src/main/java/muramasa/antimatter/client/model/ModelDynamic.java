package muramasa.antimatter.client.model;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.client.IDynamicModelBaker;
import muramasa.antimatter.client.ModelBuilder;
import muramasa.antimatter.client.baked.BakedDynamic;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;
import muramasa.gtu.Ref;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.model.IModelConfiguration;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

//TODO Support multi layer models
public class ModelDynamic extends AntimatterModel {

    protected Int2ObjectOpenHashMap<Texture[]> configs = new Int2ObjectOpenHashMap<>();
    protected Int2ObjectOpenHashMap<IUnbakedModel> models = new Int2ObjectOpenHashMap<>();
    protected Int2ObjectOpenHashMap<Function<ModelBuilder, ModelBuilder>> builders = new Int2ObjectOpenHashMap<>();
    protected Int2ObjectOpenHashMap<IBakedModel> baked = new Int2ObjectOpenHashMap<>();

    protected Consumer<ModelDynamic> configConsumer = b -> {};
    protected BiFunction<Tuple<Integer, Texture[]>, ModelBuilder, ModelBuilder> configBuilder;
    protected IDynamicModelBaker modelBaker = BakedDynamic::new;

    protected IBakedModel bakedModel;

    public ModelDynamic(Texture... textures) {
        add(textures);
        configBuilder = (t, b) -> b.simple().tex(Ref.DIRECTIONS, t.getB());
    }

    public ModelDynamic(ITextureProvider provider) {
        this(provider.getTextures());
    }

    public ModelDynamic add(int config, Texture... textures) {
        configs.put(config, textures);
        add(textures);
        return this;
    }

    public ModelDynamic add(int config, Function<ModelBuilder, ModelBuilder> builder) {
        builders.put(config, builder);
        return this;
    }

    public ModelDynamic add(int config, IUnbakedModel model) {
        models.put(config, model);
        return this;
    }

    public ModelDynamic config(Consumer<ModelDynamic> configConsumer) {
        this.configConsumer = configConsumer;
        return this;
    }

    public void onConfigConsume() {
        configConsumer.accept(this);
    }

    public ModelDynamic bake(IDynamicModelBaker baker) {
        modelBaker = baker;
        return this;
    }

    @Nullable
    @Override
    public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> getter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation loc) {
        if (bakedModel != null) return bakedModel;
        configs.forEach((i, t) -> baked.put((int) i, configBuilder.apply(new Tuple<>(i, t), new ModelBuilder()).bake(owner, bakery, getter, transform, overrides, loc)));
        models.forEach((i, m) -> baked.put((int) i, m.func_225613_a_(bakery, getter, transform, loc)));
        builders.forEach((i, b) -> {
            ModelBuilder builder = b.apply(new ModelBuilder());
            allTextures.addAll(builder.getTextures());
            baked.put((int) i, builder.bake(owner, bakery, getter, transform, overrides, loc));
        });
        configs.clear();
        models.clear();
        return (bakedModel = modelBaker.get(baked, super.bake(owner, bakery, getter, transform, overrides, loc), particle));
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        onConfigConsume();
        return allTextures;
    }
}
