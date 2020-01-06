package muramasa.antimatter.client.model;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.client.ModelBuilder;
import muramasa.antimatter.client.baked.BakedDynamic;
import muramasa.antimatter.registration.ITextureProvider;
import muramasa.antimatter.texture.Texture;
import muramasa.gtu.Ref;
import muramasa.gtu.data.Textures;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

//TODO Support multi layer models
public class ModelDynamic extends ModelBase {

    protected Int2ObjectOpenHashMap<Texture[]> configs = new Int2ObjectOpenHashMap<>();
    protected Int2ObjectOpenHashMap<IUnbakedModel> models = new Int2ObjectOpenHashMap<>();
    protected Int2ObjectOpenHashMap<Function<ModelBuilder, ModelBuilder>> builders = new Int2ObjectOpenHashMap<>();
    protected Int2ObjectOpenHashMap<IBakedModel> baked = new Int2ObjectOpenHashMap<>();

    protected Set<ResourceLocation> configTextures = new HashSet<>();
    protected Consumer<ModelDynamic> configConsumer = b -> {};
    protected Function<ModelBuilder, ModelBuilder> baseBuilder;
    protected BiFunction<Tuple<Integer, Texture[]>, ModelBuilder, ModelBuilder> configBuilder;
    protected Texture[] defaultTextures;

    protected boolean shouldBakeStatically;
    protected IBakedModel bakedModel;

    public ModelDynamic(Texture... textures) {
        defaultTextures = textures.length > 0 ? textures : new Texture[]{Textures.ERROR};
        baseBuilder = b -> b.of("block/preset/simple").tex("all", defaultTextures[0]);
        configBuilder = (t, b) -> b.of("block/preset/simple").tex(Ref.DIRECTIONS, t.getB());
    }

    public ModelDynamic(ITextureProvider provider) {
        this(provider.getTextures());
    }

    public ModelDynamic staticBaking() {
        shouldBakeStatically = true;
        return this;
    }

    public ModelDynamic base(Function<ModelBuilder, ModelBuilder> builder) {
        baseBuilder = builder;
        return this;
    }

    public ModelDynamic add(int config, Texture... textures) {
        configs.put(config, textures);
        configTextures.addAll(Arrays.asList(textures));
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

    public ModelDynamic add(ResourceLocation... textures) {
        configTextures.addAll(Arrays.asList(textures));
        return this;
    }

    public ModelDynamic config(Consumer<ModelDynamic> configConsumer) {
        this.configConsumer = configConsumer;
        return this;
    }

    public void onConfigConsume() {
        configConsumer.accept(this);
    }

    @Nullable
    @Override
    public IBakedModel bakeModel(ModelBakery bakery, Function<ResourceLocation, TextureAtlasSprite> getter, ISprite sprite, VertexFormat format) {
        if (bakedModel != null) return bakedModel;
        configs.forEach((i, t) -> baked.put((int) i, configBuilder.apply(new Tuple<>(i, t), new ModelBuilder()).bake(bakery, getter, sprite, format)));
        models.forEach((i, m) -> baked.put((int) i, m.bake(bakery, getter, sprite, format)));
        builders.forEach((i, b) -> baked.put((int) i, b.apply(new ModelBuilder()).bake(bakery, getter, sprite, format)));
        return new BakedDynamic(baked, baseBuilder.apply(new ModelBuilder()).bake(bakery, getter, sprite, format));
    }

    @Override
    public Collection<ResourceLocation> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors) {
        onConfigConsume();
        return configTextures;
    }

    public int getModelCount() {
        return baked.size();
    }
}
