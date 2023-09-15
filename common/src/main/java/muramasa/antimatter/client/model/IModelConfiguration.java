package muramasa.antimatter.client.model;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import org.jetbrains.annotations.Nullable;

public interface IModelConfiguration {
    @Nullable
    UnbakedModel getOwnerModel();

    /**
     * @return The name of the model being baked, for logging and cache purposes.
     */
    String getModelName();

    /**
     * Checks if a texture is present in the model.
     * @param name The name of a texture channel.
     */
    boolean isTexturePresent(String name);

    /**
     * Resolves the final texture name, taking into account texture aliases and replacements.
     * @param name The name of a texture channel.
     * @return The location of the texture, or the missing texture if not found.
     */
    Material resolveTexture(String name);

    /**
     * @return True if the item is a 3D model, false if it's a generated item model.
     * TODO: Rename.
     * This value has nothing to do with shading anymore, and this name is misleading.
     * It's actual purpose seems to be relegated to translating the model during rendering, so that it's centered.
     */
    boolean isShadedInGui();

    /**
     * @return True if the item is lit from the side
     */
    boolean isSideLit();

    /**
     * @return True if the item requires per-vertex lighting.
     */
    boolean useSmoothLighting();

    /**
     * Gets the vanilla camera transforms data.
     * Do not use for non-vanilla code. For general usage, prefer getCombinedState.
     */
    ItemTransforms getCameraTransforms();

    /**
     * @return The combined transformation state including vanilla and forge transforms data.
     */
    ModelState getCombinedTransform();
}
