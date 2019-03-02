package muramasa.gregtech.integration.jei.wrapper;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import muramasa.gregtech.api.recipe.Recipe;
import net.minecraft.client.Minecraft;

import java.util.Arrays;

public class RecipeWrapper implements IRecipeWrapper {

    public Recipe recipe;

    public RecipeWrapper(Recipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        if (recipe.hasInputStacks()) {
            ingredients.setInputs(VanillaTypes.ITEM, Arrays.asList(recipe.getInputStacks()));
        }
        if (recipe.hasOutputStacks()) {
            ingredients.setOutputs(VanillaTypes.ITEM, Arrays.asList(recipe.getOutputStacksJEI()));
        }
        if (recipe.hasInputFluids()) {
            ingredients.setInputs(VanillaTypes.FLUID, Arrays.asList(recipe.getInputFluids()));
        }
        if (recipe.hasOutputFluids()) {
            ingredients.setOutputs(VanillaTypes.FLUID, Arrays.asList(recipe.getOutputFluids()));
        }
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        if (recipe.getTotalPower() > 0) {
            minecraft.fontRenderer.drawString("Total: " + recipe.getTotalPower() + " EU", 10, 85, 0x000000);
        }
        if (recipe.getPower() > 0) {
            minecraft.fontRenderer.drawString("Usage: " + recipe.getPower() + " EU/t", 10, 95, 0x000000);
            minecraft.fontRenderer.drawString("Voltage: " + "32 (LV)" + "", 10, 105, 0x000000);
            minecraft.fontRenderer.drawString("Amperage: " + "1" + "", 10, 115, 0x000000);
        }
        if (recipe.getDuration() > 0) {
            minecraft.fontRenderer.drawString("Time: " + (recipe.getDuration() / (float)20) + "s", 10, 125, 0x000000);
        }
    }
}
