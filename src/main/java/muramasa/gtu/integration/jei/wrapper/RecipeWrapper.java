package muramasa.gtu.integration.jei.wrapper;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import muramasa.gtu.api.recipe.Recipe;
import muramasa.gtu.api.util.int4;
import net.minecraft.client.Minecraft;

import java.util.Arrays;

public class RecipeWrapper implements IRecipeWrapper {

    public Recipe recipe;
    private int4 padding;

    public RecipeWrapper(Recipe recipe) {
        this.recipe = recipe;
    }

    public void setPadding(int4 padding) {
        this.padding = padding;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        if (recipe.hasInputItems()) {
            ingredients.setInputs(VanillaTypes.ITEM, Arrays.asList(recipe.getInputItems()));
        }
        if (recipe.hasOutputItems()) {
            ingredients.setOutputs(VanillaTypes.ITEM, Arrays.asList(recipe.getOutputItemsJEI()));
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
        if (padding == null) return;
        int lineCount = 0, startY = (recipeHeight - padding.y) + 5;
        if (recipe.getTotalPower() > 0) {
            minecraft.fontRenderer.drawString("Total: " + recipe.getTotalPower() + " EU", 10, startY + (lineCount++ * 10), 0x000000);
        }
        if (recipe.getPower() > 0) {
            minecraft.fontRenderer.drawString("Usage: " + recipe.getPower() + " EU/t", 10, startY + (lineCount++ * 10), 0x000000);
            minecraft.fontRenderer.drawString("Voltage: " + "32 (LV)" + "", 10, startY + (lineCount++ * 10), 0x000000);
            minecraft.fontRenderer.drawString("Amperage: " + "1" + "", 10, startY + (lineCount++ * 10), 0x000000);
        }
        if (recipe.getDuration() > 0) {
            minecraft.fontRenderer.drawString("Time: " + (recipe.getDuration() / (float)20) + "s (" + recipe.getDuration() + " ticks)", 10, startY + (lineCount++ * 10), 0x000000);
        }
        if (recipe.getSpecialValue() > 0) {
            minecraft.fontRenderer.drawString("Special: " + recipe.getSpecialValue(), 10, startY + (lineCount++ * 10), 0x000000);
        }
    }
}
