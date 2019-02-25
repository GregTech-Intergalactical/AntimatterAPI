package muramasa.gregtech.integration.jei.categories;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import muramasa.gregtech.api.data.Machines;
import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.api.machines.types.Machine;
import muramasa.gregtech.integration.jei.MachineRecipeWrapper;

public class MultiMachineRecipeCategory extends MachineRecipeCategory {

    public MultiMachineRecipeCategory(IGuiHelper guiHelper, Machine type) {
        super(guiHelper, type);
        background = guiHelper.drawableBuilder(type.getGUITexture(Tier.MULTI), 3, 3, 170, 80).addPadding(0, 55, 0, 0).build();
        icon = guiHelper.createDrawableIngredient(Machines.get(type, Tier.MULTI).asItemStack());
    }

    @Override
    public void setRecipe(IRecipeLayout layout, MachineRecipeWrapper wrapper, IIngredients ingredients) {
        if (type.getSlots() != null) {
            super.setRecipe(layout, wrapper, ingredients);
        } else {
            IGuiItemStackGroup itemGroup = layout.getItemStacks();
            IGuiFluidStackGroup fluidGroup = layout.getFluidStacks();

            int index = 0;
            if (wrapper.recipe.hasInputStacks()) {
                for (int i = 0; i < wrapper.recipe.getInputStacks().length; i++) {
                    int xStart, yStart;
                    if (wrapper.recipe.getInputStacks().length <= 3) {
                        xStart = 31 - 9 * (wrapper.recipe.getInputStacks().length - 1);
                        yStart = 22;
                    } else {
                        xStart = i >= 3 ? -41 : 13;
                        yStart = i >= 3 ? 29 : 13;
                    }
                    itemGroup.init(index, true, xStart + (i * 18), yStart);
                    itemGroup.set(index++, wrapper.recipe.getInputStacks()[i]);
                }
            }
            if (wrapper.recipe.hasOutputStacks()) {
                for (int i = 0; i < wrapper.recipe.getOutputStacks().length; i++) {
                    int xStart, yStart;
                    if (wrapper.recipe.getOutputStacks().length <= 3) {
                        xStart = 121 - 9 * (wrapper.recipe.getInputStacks().length - 1);
                        yStart = 22;
                    } else {
                        xStart = i >= 3 ? 49 : 103;
                        yStart = i >= 3 ? 29 : 13;
                    }
                    itemGroup.init(index, false, xStart + (i * 18), yStart);
                    itemGroup.set(index++, wrapper.recipe.getOutputStacks()[i]);
                }
            }
        }
    }
}
