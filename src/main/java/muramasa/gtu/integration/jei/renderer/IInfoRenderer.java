package muramasa.gtu.integration.jei.renderer;

import muramasa.antimatter.recipe.Recipe;
import net.minecraft.client.Minecraft;

public interface IInfoRenderer {

    void drawInfo(Recipe recipe, Minecraft minecraft, int startY, int recipeWidth, int recipeHeight, int mouseX, int mouseY);
}
