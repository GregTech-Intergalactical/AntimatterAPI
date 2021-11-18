package muramasa.antimatter.integration.jei.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import muramasa.antimatter.recipe.Recipe;
import net.minecraft.client.gui.FontRenderer;

public interface IRecipeInfoRenderer {
    void render(MatrixStack stack, Recipe recipe, FontRenderer fontRenderer, int guiOffsetX, int guiOffsetY);

    default void renderString(MatrixStack stack, String string, FontRenderer render, float x, float y, int guiOffsetX, int guiOffsetY) {
        renderString(stack, string, render, x, y, 0xFFFFFF, guiOffsetX, guiOffsetY);
    }

    default void renderString(MatrixStack stack, String string, FontRenderer render, float x, float y, int color, int guiOffsetX, int guiOffsetY) {
        render.drawShadow(stack, string, (guiOffsetX + x), guiOffsetY + y, color);
    }

    default int stringWidth(String string, FontRenderer renderer) {
        return renderer.width(string);
    }
}
