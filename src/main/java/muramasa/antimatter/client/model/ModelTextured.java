package muramasa.antimatter.client.model;

import muramasa.gtu.Ref;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.function.Function;

public class ModelTextured extends ModelBase {

    protected ResourceLocation path;
    protected Function<ModelContainer, ModelContainer> function;

    public ModelTextured(ResourceLocation path) {
        this.path = path;
    }

    public ModelTextured(String domain, String path) {
        this(new ResourceLocation(domain, path));
    }

    public ModelTextured(String path) {
        this(Ref.MODID, path);
    }

    public ModelTextured of(Function<ModelContainer, ModelContainer> function) {
        this.function = function;
        return this;
    }

    @Nullable
    @Override
    public IBakedModel bake(ModelBakery bakery, Function<ResourceLocation, TextureAtlasSprite> getter, ISprite sprite, VertexFormat format) {
        return function.apply(load(path)).bake(bakery, getter, sprite, format);
    }
}
