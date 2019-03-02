package muramasa.gregtech.integration.jei.category;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import muramasa.gregtech.api.data.Machines;
import muramasa.gregtech.api.gui.SlotData;
import muramasa.gregtech.api.gui.SlotType;
import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.api.machines.types.Machine;
import muramasa.gregtech.integration.jei.wrapper.RecipeWrapper;
import net.minecraft.item.ItemStack;

import java.util.List;

public class MachineRecipeCategory extends RecipeMapCategory {

    protected Machine type;

    public MachineRecipeCategory(IGuiHelper guiHelper, Machine type) {
        super(guiHelper, type.getRecipeMap());
        this.type = type;
        background = guiHelper.drawableBuilder(type.getGui().getTexture(Tier.IV), 3, 3, 170, 80).addPadding(0, 55, 0, 0).build();
        progressBar = guiHelper.drawableBuilder(type.getGui().getTexture(Tier.IV), 176, 0, 20, 18).buildAnimated(50, IDrawableAnimated.StartDirection.LEFT, false);
        icon = guiHelper.createDrawableIngredient(Machines.get(type, Tier.LV).asItemStack());
        SLOT_OFFSET_Y = 4;
    }

    @Override
    public void setRecipe(IRecipeLayout layout, RecipeWrapper wrapper, IIngredients ingredients) {
        IGuiItemStackGroup itemGroup = layout.getItemStacks();
        IGuiFluidStackGroup fluidGroup = layout.getFluidStacks();
        List<SlotData> slots;
        int i = 0, slotIndex = 0;

        slots = type.getGui().getTypes(SlotType.IT_IN, Tier.IV);
        if (wrapper.recipe.hasInputStacks()) {
            for (ItemStack stack : wrapper.recipe.getInputStacks()) {
                itemGroup.init(i, true, slots.get(slotIndex).x - SLOT_OFFSET_X, slots.get(slotIndex++).y - SLOT_OFFSET_Y);
                itemGroup.set(i++, stack);
            }
        }

        slotIndex = 0;
        slots = type.getGui().getTypes(SlotType.IT_OUT, Tier.IV);
        if (wrapper.recipe.hasOutputStacks()) {
            for (ItemStack stack : wrapper.recipe.getOutputStacks()) {
                itemGroup.init(i, false, slots.get(slotIndex).x - SLOT_OFFSET_X, slots.get(slotIndex++).y - SLOT_OFFSET_Y);
                itemGroup.set(i++, stack);
            }
        }

//        i = 0;
//        if (wrapper.recipe.hasInputFluids()) {
//            for (FluidStack stack : wrapper.recipe.getInputFluids()) {
////                fluidGroup.init(i, true, );
//            }
//        }
//
//        if (type.getFluidInputCount() > 0) {
//            fluidGroup.init(0, true, 50, 60, 16, 16, 1, false, null);
//            fluidGroup.set(0, ingredients.getInputs(VanillaTypes.FLUID).get(0));
//        }
////        if (type.getFluidOutputCount() > 0) {
////            fluidGroup.init(1, false, 104, 60, 16, 16, 1, false, null);
////            fluidGroup.set(1, ingredients.getOutputs(VanillaTypes.FLUID).get(0));
////        }
    }
}
