package muramasa.antimatter.integration.jei.renderer;

import muramasa.antimatter.recipe.Recipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public interface IRecipeInfoRenderer {
    void render(Recipe recipe, FontRenderer fontRenderer, int guiOffsetX, int guiOffsetY);

    default void renderString(String string,FontRenderer render, float x, float y,int guiOffsetX, int guiOffsetY) {
        renderString(string,render,x,y,0xFFFFFF, guiOffsetX, guiOffsetY);
    }

    default void renderString(String string, FontRenderer render, float x, float y, int color, int guiOffsetX, int guiOffsetY) {
        render.drawStringWithShadow(string, (guiOffsetX + x), guiOffsetY + y, color);
    }

    default int stringWidth(String string, FontRenderer renderer) {
        return renderer.getStringWidth(string);
    }

    public static final IRecipeInfoRenderer EMPTY_RENDERER = new IRecipeInfoRenderer() {
        @Override
        public void render(Recipe recipe, FontRenderer fontRenderer, int guiOffsetX, int guiOffsetY) {

        }
    };
}
