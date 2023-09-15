package muramasa.antimatter.client.model.loader.fabric;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import io.github.fabricators_of_create.porting_lib.model.IModelConfiguration;
import io.github.fabricators_of_create.porting_lib.model.IModelGeometry;
import io.github.fabricators_of_create.porting_lib.model.IModelLoader;
import muramasa.antimatter.client.IAntimatterModel;
import muramasa.antimatter.client.model.loader.IAntimatterModelLoader;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

public record ModelLoaderWrapper(IAntimatterModelLoader<?> loader) implements IModelLoader {
    @Override
    public IModelGeometry read(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
        IAntimatterModel model = loader.readModel(jsonDeserializationContext, jsonObject);
        return new ModelGeometryWrapper(model);
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {

    }

    private record ModelGeometryWrapper(IAntimatterModel model) implements IModelGeometry<ModelGeometryWrapper>{

        @Override
        public BakedModel bake(IModelConfiguration iModelConfiguration, ModelBakery modelBakery, Function function, ModelState modelState, ItemOverrides itemOverrides, ResourceLocation resourceLocation) {
            return model.bake(new ModelConfigurationWrapper(iModelConfiguration), modelBakery, function, modelState, itemOverrides, resourceLocation);
        }

        @Override
        public Collection<Material> getTextures(IModelConfiguration iModelConfiguration, Function function, Set set) {
            return model.getMaterials(new ModelConfigurationWrapper(iModelConfiguration), function, set);
        }
    }

    private record ModelConfigurationWrapper(IModelConfiguration configuration) implements muramasa.antimatter.client.model.IModelConfiguration {

        @Nullable
        @Override
        public UnbakedModel getOwnerModel() {
            return configuration.getOwnerModel();
        }

        @Override
        public String getModelName() {
            return configuration.getModelName();
        }

        @Override
        public boolean isTexturePresent(String name) {
            return configuration.isTexturePresent(name);
        }

        @Override
        public Material resolveTexture(String name) {
            return configuration.resolveTexture(name);
        }

        @Override
        public boolean isShadedInGui() {
            return configuration.isShadedInGui();
        }

        @Override
        public boolean isSideLit() {
            return configuration.isSideLit();
        }

        @Override
        public boolean useSmoothLighting() {
            return configuration.useSmoothLighting();
        }

        @Override
        public ItemTransforms getCameraTransforms() {
            return configuration.getCameraTransforms();
        }

        @Override
        public ModelState getCombinedTransform() {
            return configuration.getCombinedTransform();
        }
    }
}
