package muramasa.antimatter.integration.jeirei.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import muramasa.antimatter.recipe.IRecipe;
import muramasa.antimatter.recipe.Recipe;
import net.minecraft.client.gui.Font;

public interface IRecipeInfoRenderer {
    void render(PoseStack stack, IRecipe recipe, Font fontRenderer, int guiOffsetX, int guiOffsetY);

    default void renderString(PoseStack stack, String string, Font render, float x, float y, int guiOffsetX, int guiOffsetY) {
        renderString(stack, string, render, x, y, 0xFFFFFF, guiOffsetX, guiOffsetY);
    }

    default void renderString(PoseStack stack, String string, Font render, float x, float y, int color, int guiOffsetX, int guiOffsetY) {
        render.drawShadow(stack, string, (guiOffsetX + x), guiOffsetY + y, color);
    }

    default int stringWidth(String string, Font renderer) {
        return renderer.width(string);
    }
}
