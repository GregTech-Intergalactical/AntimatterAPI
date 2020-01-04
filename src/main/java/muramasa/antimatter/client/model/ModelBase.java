package muramasa.antimatter.client.model;

import com.google.common.collect.ImmutableMap;
import muramasa.gtu.Ref;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

public abstract class ModelBase implements IUnbakedModel {

    @Override
    public Collection<ResourceLocation> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors) {
        return Collections.emptyList();
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return Collections.emptyList();
    }

    /** Model Helpers **/
    public static ResourceLocation mc(String path) {
        return new ResourceLocation(path);
    }

    public static ResourceLocation mod(String path) {
        return new ResourceLocation(Ref.MODID, path);
    }

    public static ModelContainer load(ResourceLocation loc) {
        try {
            return new ModelContainer(ModelLoaderRegistry.getModel(loc));
        } catch (Exception e) {
            System.err.println("ModelBase.load() failed due to " + e + ":");
            e.printStackTrace();
            return new ModelContainer(ModelLoaderRegistry.getMissingModel());
        }
    }

    public static class ModelContainer {

        private IModel model;

        public ModelContainer(IModel model) {
            this.model = model;
        }

        public IModel get() {
            return model;
        }

        public ModelContainer tex(ResourceLocation loc, String... elements) {
            for (int i = 0; i < elements.length; i++) {
                tex(elements[i], loc);
            }
            return this;
        }

        public ModelContainer tex(String[] elements, ResourceLocation[] textures) {
            for (int i = 0; i < elements.length; i++) {
                tex(elements[i], textures[i]);
            }
            return this;
        }

        public ModelContainer tex(String element, ResourceLocation texture) {
            return tex(element, texture.toString());
        }

        public ModelContainer tex(String element, String texture) {
            try {
                model = model.retexture(ImmutableMap.of(element, texture));
                return this;
            } catch (Exception e) {
                System.err.println("ModelContainer.tex() failed due to " + e + ":");
                e.printStackTrace();
                return this;
            }
        }

        public IBakedModel bake(ModelBakery bakery, Function<ResourceLocation, TextureAtlasSprite> spriteGetter, ISprite sprite, VertexFormat format) {
            return model.bake(bakery, spriteGetter, sprite, format);
        }
    }
}
