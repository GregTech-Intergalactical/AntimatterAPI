package muramasa.gregtech.integration.jei.category;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import muramasa.gregtech.api.data.Machines;
import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.api.machines.types.Machine;
import muramasa.gregtech.integration.jei.wrapper.RecipeWrapper;

public class MultiMachineRecipeCategory extends MachineRecipeCategory {

    public MultiMachineRecipeCategory(IGuiHelper guiHelper, Machine type) {
        super(guiHelper, type);
        if (!type.getGui().hasSlots()) {
            background = guiHelper.drawableBuilder(MULTI_DISPLAY, 3, 3, 170, 80).addPadding(0, 55, 0, 0).build();
            progressBar = guiHelper.drawableBuilder(MULTI_DISPLAY, 176, 0, 20, 18).buildAnimated(50, IDrawableAnimated.StartDirection.LEFT, false);
        }
        icon = guiHelper.createDrawableIngredient(Machines.get(type, Tier.MULTI).asItemStack());
    }

    @Override
    public void setRecipe(IRecipeLayout layout, RecipeWrapper wrapper, IIngredients ingredients) {
        if (type.getGui().hasSlots()) {
            super.setRecipe(layout, wrapper, ingredients);
        } else {
            drawRecipeDefault(layout, wrapper, ingredients);
        }
    }
}
