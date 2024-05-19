package muramasa.antimatter.client.model.loader.forge;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.math.Transformation;
import muramasa.antimatter.client.IAntimatterModel;
import muramasa.antimatter.client.model.loader.IAntimatterModelLoader;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.BlockGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

public record ModelLoaderWrapper(IAntimatterModelLoader<?> loader) implements IGeometryLoader {
    @Override
    public IUnbakedGeometry read(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        IAntimatterModel model = loader.readModel(jsonDeserializationContext, jsonObject);
        return new ModelGeometryWrapper(model);
    }

    private record ModelGeometryWrapper(IAntimatterModel model) implements IUnbakedGeometry<ModelGeometryWrapper> {
        @Override
        public BakedModel bake(IGeometryBakingContext iModelConfiguration, ModelBakery modelBakery, Function function, ModelState modelState, ItemOverrides itemOverrides, ResourceLocation resourceLocation) {
            return model.bake(new GeometryBakingContextWrapper(iModelConfiguration),modelBakery, function, modelState, itemOverrides, resourceLocation);
        }

        @Override
        public Collection<Material> getMaterials(IGeometryBakingContext iModelConfiguration, Function function, Set set) {
            return model.getMaterials(new GeometryBakingContextWrapper(iModelConfiguration), function, set);
        }
    }

    private record GeometryBakingContextWrapper(IGeometryBakingContext configuration) implements muramasa.antimatter.client.model.IGeometryBakingContext {

        @Nullable
        @Override
        public UnbakedModel getOwnerModel() {
            return configuration instanceof BlockGeometryBakingContext context ? context.owner : null;
        }

        @Override
        public String getModelName() {
            return configuration.getModelName();
        }

        @Override
        public boolean hasMaterial(String name) {
            return configuration.hasMaterial(name);
        }

        @Override
        public Material getMaterial(String name) {
            return configuration.getMaterial(name);
        }

        @Override
        public boolean isGui3d() {
            return configuration.isGui3d();
        }

        @Override
        public boolean useBlockLight() {
            return configuration.useBlockLight();
        }

        @Override
        public boolean useAmbientOcclusion() {
            return configuration.useAmbientOcclusion();
        }

        @Override
        public ItemTransforms getTransforms() {
            return configuration.getTransforms();
        }

        @Override
        public Transformation getRootTransform() {
            return configuration.getRootTransform();
        }

        @Override
        public @Nullable ResourceLocation getRenderTypeHint() {
            return configuration.getRenderTypeHint();
        }

        @Override
        public boolean isComponentVisible(String component, boolean fallback) {
            return configuration.isComponentVisible(component, fallback);
        }
    }
}
