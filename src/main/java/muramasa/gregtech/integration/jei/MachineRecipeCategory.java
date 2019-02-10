package muramasa.gregtech.integration.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import muramasa.gregtech.api.machines.MachineStack;
import muramasa.gregtech.api.machines.SlotData;
import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.api.machines.types.Machine;
import muramasa.gregtech.common.utils.Ref;
import net.minecraft.client.Minecraft;

import javax.annotation.Nullable;

public class MachineRecipeCategory implements IRecipeCategory<MachineRecipeWrapper> {

    private static final int SLOT_OFFSET_X = 4, SLOT_OFFSET_Y = 4;

    private final Machine type;
    private final String title, uid;
    private final IDrawable background, icon;
    private final IDrawableAnimated progressBar;

    public MachineRecipeCategory(IGuiHelper guiHelper, Machine type) {
        this.type = type;
        title = type.getJeiCategoryName();
        uid = type.getJeiCategoryID();
        background = guiHelper.drawableBuilder(type.getGUITexture(Tier.LV.getName()), 3, 3, 170, 80).addPadding(0, 55, 0, 0).build();
        icon = guiHelper.createDrawableIngredient(new MachineStack(type, Tier.LV).asItemStack());
        progressBar = guiHelper.drawableBuilder(type.getGUITexture(Tier.LV.getName()), 176, 0, 20, 18).buildAnimated(50, IDrawableAnimated.StartDirection.LEFT, false);
//        icon = guiHelper.createDrawableIngredient(MachineStack.get(type, Tier.LV).getStackForm());
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
    public void setRecipe(IRecipeLayout recipeLayout, MachineRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup itemStackGroup = recipeLayout.getItemStacks();
        IGuiFluidStackGroup fluidStackGroup = recipeLayout.getFluidStacks();

        SlotData[] slots = type.getSlots();
        for (int i = 0; i < slots.length; i++) {
            itemStackGroup.init(i, slots[i].type == 0, slots[i].x - SLOT_OFFSET_X, slots[i].y - SLOT_OFFSET_Y);
            int stackIndex = i < type.getInputCount() ? i : i - type.getInputCount(); //TODO register helper?
            itemStackGroup.set(i, i < type.getInputCount() ? ingredients.getInputs(VanillaTypes.ITEM).get(stackIndex) : ingredients.getOutputs(VanillaTypes.ITEM).get(stackIndex));
        }

        if (type.getFluidInputCount() > 0) {
            fluidStackGroup.init(0, true, 50, 60, 16, 16, 1, false, null);
            fluidStackGroup.set(0, ingredients.getInputs(VanillaTypes.FLUID).get(0));
        }
//        if (type.getFluidOutputCount() > 0) {
//            fluidStackGroup.init(1, false, 104, 60, 16, 16, 1, false, null);
//            fluidStackGroup.set(1, ingredients.getOutputs(VanillaTypes.FLUID).get(0));
//        }
    }
}
