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
import muramasa.gregtech.integration.jei.renderer.FluidStackRenderer;

public class MultiMachineRecipeCategory extends MachineRecipeCategory {

    public MultiMachineRecipeCategory(IGuiHelper guiHelper, Machine type) {
        super(guiHelper, type);
        background = guiHelper.drawableBuilder(type.getGui().getTexture(Tier.MULTI), 3, 3, 170, 80).addPadding(0, 55, 0, 0).build();
        icon = guiHelper.createDrawableIngredient(Machines.get(type, Tier.MULTI).asItemStack());
    }

    @Override
    public void setRecipe(IRecipeLayout layout, MachineRecipeWrapper wrapper, IIngredients ingredients) {
        if (type.getGui().hasSlots()) {
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
                        xStart = 121 - 9 * (wrapper.recipe.getOutputStacks().length - 1);
                        yStart = 22;
                    } else {
                        xStart = i >= 3 ? 49 : 103;
                        yStart = i >= 3 ? 29 : 13;
                    }
                    itemGroup.init(index, false, xStart + (i * 18), yStart);
                    itemGroup.set(index++, wrapper.recipe.getOutputStacks()[i]);
                }
            }
            index = 0;
            if (wrapper.recipe.hasInputFluids()) {
                for (int i = 0; i < wrapper.recipe.getInputFluids().length; i++) {
                    int xStart = wrapper.recipe.getInputFluids().length <= 1 ? 50 : 50 - (18 * index);
                    fluidGroup.init(index, true, new FluidStackRenderer(), xStart, 60, 16, 16, 0, 0);
                    fluidGroup.set(index++, wrapper.recipe.getInputFluids()[i]);
                }
            }
            if (wrapper.recipe.hasOutputFluids()) {
                for (int i = 0; i < wrapper.recipe.getOutputFluids().length; i++) {
                    int xStart = 104 + (i * 18);
                    fluidGroup.init(index, false, new FluidStackRenderer(), xStart, 60, 16, 16, 0, 0);
                    fluidGroup.set(index++, wrapper.recipe.getOutputFluids()[i]);
                }
            }
        }
    }
}
