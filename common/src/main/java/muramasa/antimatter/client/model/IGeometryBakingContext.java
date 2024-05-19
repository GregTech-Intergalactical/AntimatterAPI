package muramasa.antimatter.client.model;

import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public interface IGeometryBakingContext {
    @Nullable
    UnbakedModel getOwnerModel();

    /**
     * {@return the name of the model being baked for logging and caching purposes.}
     */
    String getModelName();

    /**
     * Checks if a material is present in the model.
     *
     * @param name The name of the material
     * @return true if the material is present, false otherwise
     */
    boolean hasMaterial(String name);

    /**
     * Resolves the final texture name, taking into account texture aliases and replacements.
     *
     * @param name The name of the material
     * @return The material, or the missing texture if not found
     */
    Material getMaterial(String name);

    /**
     * {@return true if this model should render in 3D in a GUI, false otherwise}
     */
    boolean isGui3d();

    /**
     * {@return true if block lighting should be used for this model, false otherwise}
     */
    boolean useBlockLight();

    /**
     * {@return true if per-vertex ambient occlusion should be used for this model, false otherwise}
     */
    boolean useAmbientOcclusion();

    /**
     * {@return the transforms for display in item form.}
     */
    ItemTransforms getTransforms();

    /**
     * {@return the root transformation to be applied to all variants of this model, regardless of item transforms.}
     */
    Transformation getRootTransform();

    /**
     * {@return a hint of the render type this model should use. Custom loaders may ignore this.}
     */
    @Nullable
    ResourceLocation getRenderTypeHint();

    /**
     * Queries the visibility of a component of this model.
     *
     * @param component The component for which to query visibility
     * @param fallback  The default visibility if an override isn't found
     * @return The visibility of the component
     */
    boolean isComponentVisible(String component, boolean fallback);
}
