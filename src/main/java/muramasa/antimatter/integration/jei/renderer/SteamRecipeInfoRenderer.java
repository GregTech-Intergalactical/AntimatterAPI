package muramasa.antimatter.integration.jei.renderer;

import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.recipe.Recipe;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fluids.FluidStack;

public class SteamRecipeInfoRenderer implements IRecipeInfoRenderer {

    public static final SteamRecipeInfoRenderer INSTANCE = new SteamRecipeInfoRenderer();

    @Override
    public void render(Recipe recipe, FontRenderer fontRenderer, int guiOffsetX, int guiOffsetY) {
        String power = "Duration: " + recipe.getDuration() + " ticks";
        String euT = "Steam/t: ";
        renderString(power,fontRenderer, 5, 5,guiOffsetX,guiOffsetY);
        renderString(euT + " " + recipe.getPower() + "mb" ,fontRenderer, 5, 15,guiOffsetX,guiOffsetY);
    }
}
