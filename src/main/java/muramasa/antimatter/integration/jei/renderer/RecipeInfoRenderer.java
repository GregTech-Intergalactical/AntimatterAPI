package muramasa.antimatter.integration.jei.renderer;

import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.recipe.Recipe;
import net.minecraft.client.gui.FontRenderer;

public class RecipeInfoRenderer implements IRecipeInfoRenderer {

    public final static RecipeInfoRenderer INSTANCE = new RecipeInfoRenderer();
    @Override
    public void render(Recipe recipe, FontRenderer fontRenderer, int guiOffsetX, int guiOffsetY) {
        String power = "Duration: " + recipe.getDuration() + " ticks";
        String euT = "EU/t: " + recipe.getPower();
        String formattedText = " (" + Tier.getTier((int) recipe.getPower()).getId().toUpperCase() + ")";
        renderString(power,fontRenderer, 5, 5,guiOffsetX,guiOffsetY);
        renderString(euT,fontRenderer, 5, 15,guiOffsetX,guiOffsetY);
        renderString(formattedText, fontRenderer,5+stringWidth(euT,fontRenderer), 15,Tier.EV.getRarityFormatting().getColor(), guiOffsetX,guiOffsetY);
    }
}
