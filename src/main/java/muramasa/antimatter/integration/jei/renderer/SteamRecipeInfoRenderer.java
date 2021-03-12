package muramasa.antimatter.integration.jei.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import muramasa.antimatter.recipe.Recipe;
import net.minecraft.client.gui.FontRenderer;

public class SteamRecipeInfoRenderer implements IRecipeInfoRenderer {

    public static final SteamRecipeInfoRenderer INSTANCE = new SteamRecipeInfoRenderer();

    @Override
    public void render(MatrixStack stack, Recipe recipe, FontRenderer fontRenderer, int guiOffsetX, int guiOffsetY) {
        String power = "Duration: " + recipe.getDuration() + " ticks";
        String euT = "Steam: ";
        renderString(stack, power,fontRenderer, 5, 5,guiOffsetX,guiOffsetY);
        renderString(stack, euT + recipe.getPower() + "mb/t" ,fontRenderer, 5, 15,guiOffsetX,guiOffsetY);
    }
}
