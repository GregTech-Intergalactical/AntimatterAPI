package muramasa.gregtech.integration.jei.categories;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import muramasa.gregtech.api.data.Machines;
import muramasa.gregtech.api.machines.Slot;
import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.api.machines.types.Machine;
import muramasa.gregtech.common.utils.Ref;
import muramasa.gregtech.integration.jei.MachineRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class MachineRecipeCategory implements IRecipeCategory<MachineRecipeWrapper> {

    private static final int SLOT_OFFSET_X = 4, SLOT_OFFSET_Y = 4;

    protected Machine type;
    protected String title, uid;
    protected IDrawable background, icon;
    protected IDrawableAnimated progressBar;

    public MachineRecipeCategory(IGuiHelper guiHelper, Machine type) {
        this.type = type;
        title = type.getJeiCategoryName();
        uid = type.getJeiCategoryID();
        background = guiHelper.drawableBuilder(type.getGUITexture(Tier.LV), 3, 3, 170, 80).addPadding(0, 55, 0, 0).build();
        progressBar = guiHelper.drawableBuilder(type.getGUITexture(Tier.LV), 176, 0, 20, 18).buildAnimated(50, IDrawableAnimated.StartDirection.LEFT, false);
        icon = guiHelper.createDrawableIngredient(Machines.get(type, Tier.LV).asItemStack());
    }

    @Override
    public String getUid() {
        return uid;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getModName() {
        return Ref.NAME;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Nullable
    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        progressBar.draw(minecraft, 75, 21);
    }

    @Override
    public void setRecipe(IRecipeLayout layout, MachineRecipeWrapper wrapper, IIngredients ingredients) {
        IGuiItemStackGroup itemGroup = layout.getItemStacks();
        IGuiFluidStackGroup fluidGroup = layout.getFluidStacks();

        int i = 0;
        ArrayList<Slot> slots = type.getSlots();
        if (wrapper.recipe.hasInputStacks()) {
            for (ItemStack stack : wrapper.recipe.getInputStacks()) {
                itemGroup.init(i, true, slots.get(i).x - SLOT_OFFSET_X, slots.get(i).y - SLOT_OFFSET_Y);
                itemGroup.set(i++, stack);
            }
        }
        if (wrapper.recipe.hasOutputStacks()) {
            for (ItemStack stack : wrapper.recipe.getOutputStacks()) {
                itemGroup.init(i, false, slots.get(i).x - SLOT_OFFSET_X, slots.get(i).y - SLOT_OFFSET_Y);
                itemGroup.set(i++, stack);
            }
        }
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
