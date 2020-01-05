package muramasa.antimatter.client.model;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.blocks.BlockDynamic;
import muramasa.antimatter.client.baked.BakedDynamic;
import muramasa.antimatter.texture.Texture;
import muramasa.gtu.Ref;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

//TODO 1.14-NewModelLoaders: The Dynamic loader could have a "defaultModel" property
//TODO defaultModel needs a better solution
//TODO Support multi layer models
public class ModelDynamic extends ModelBase implements IModelDynamicConfig {

    protected Int2ObjectOpenHashMap<Texture[]> configLookup = new Int2ObjectOpenHashMap<>();
    protected Int2ObjectOpenHashMap<IUnbakedModel> modelLookup = new Int2ObjectOpenHashMap<>();
    protected Int2ObjectOpenHashMap<IBakedModel> bakedLookup = new Int2ObjectOpenHashMap<>();

    protected Set<ResourceLocation> configTextures = new HashSet<>();
    protected Consumer<ModelDynamic> configConsumer = b -> {};
    protected BlockDynamic block;

    public ModelDynamic(BlockDynamic block) {
        this.block = block;
    }

    public BlockDynamic getBlock() {
        return block;
    }

    public ModelDynamic add(int config, Texture... textures) {
        configLookup.put(config, textures);
        configTextures.addAll(Arrays.asList(textures));
        return this;
    }

    public ModelDynamic add(int config, IUnbakedModel model) {
        modelLookup.put(config, model);
        return this;
    }

    public ModelDynamic setConfig(Consumer<ModelDynamic> configConsumer) {
        this.configConsumer = configConsumer;
        return this;
    }

    protected ModelContainer getDefaultModel() {
        return load(mod("block/preset/simple")).tex("all", block.getTextures()[0]);
    }

    protected ModelContainer getConfigModel(int config, Texture[] textures) {
        ModelContainer model = load(mod("block/preset/simple"));
        for (int j = 0; j < textures.length; j++) {
            model.tex(Ref.DIRECTIONS[j].getName(), textures[j]);
        }
        return model;
    }

    @Nullable
    @Override
    public IBakedModel bake(ModelBakery bakery, Function<ResourceLocation, TextureAtlasSprite> getter, ISprite sprite, VertexFormat format) {
        configLookup.forEach((i, t) -> {
            IUnbakedModel model = modelLookup.get((int) i);
            bakedLookup.put((int) i, (model != null ? model : getConfigModel(i, t).get()).bake(bakery, getter, sprite, format));
        });
        return new BakedDynamic(block, bakedLookup, getDefaultModel().bake(bakery, getter, sprite, format));
    }

    @Override
    public Collection<ResourceLocation> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors) {
        configConsumer.accept(this);
        return configTextures;
    }
}
