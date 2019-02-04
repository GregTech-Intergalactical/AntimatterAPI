package muramasa.gregtech.integration.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import muramasa.gregtech.api.recipe.Recipe;
import net.minecraft.client.Minecraft;

import java.util.Arrays;

public class MachineRecipeWrapper implements IRecipeWrapper {

    private Recipe recipe;

    public MachineRecipeWrapper(Recipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputs(VanillaTypes.ITEM, Arrays.asList(recipe.getInputs()));
        ingredients.setOutputs(VanillaTypes.ITEM, Arrays.asList(recipe.getOutputs()));
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        minecraft.fontRenderer.drawString("Total: " + recipe.getDuration() * recipe.getPower() + " EU", 10, 85, 0x000000);
        minecraft.fontRenderer.drawString("Usage: " + recipe.getPower() + " EU/t", 10, 95, 0x000000);
        minecraft.fontRenderer.drawString("Voltage: " + "32 (LV)" + "", 10, 105, 0x000000);
        minecraft.fontRenderer.drawString("Amperage: " + "1" + "", 10, 115, 0x000000);
        minecraft.fontRenderer.drawString("Time: " + (float)(recipe.getDuration() / 20) + "s", 10, 125, 0x000000);
    }
}
