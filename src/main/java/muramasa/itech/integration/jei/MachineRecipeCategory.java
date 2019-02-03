package muramasa.itech.integration.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import muramasa.itech.api.machines.Machine;
import muramasa.itech.api.machines.SlotData;
import muramasa.itech.api.machines.Tier;
import muramasa.itech.common.utils.Ref;
import net.minecraft.client.Minecraft;

import java.util.List;

public class MachineRecipeCategory implements IRecipeCategory<MachineRecipeWrapper> {

    private static final int SLOT_OFFSET_X = 4, SLOT_OFFSET_Y = 4;

    private final Machine type;
    private final String title, uid;
    private final IDrawable background/*, icon*/;
    private final IDrawableAnimated progressBar;

    public MachineRecipeCategory(IGuiHelper guiHelper, Machine type) {
        this.type = type;
        title = type.getJeiCategoryName();
        uid = type.getJeiCategoryID();
        background = guiHelper.drawableBuilder(type.getGUITexture(Tier.LV.getName()), 3, 3, 170, 80).addPadding(0, 55, 0, 0).build();
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

//    @Nullable
//    @Override
//    public IDrawable getIcon() {
//        return icon;
//    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        progressBar.draw(minecraft, 75, 21);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, MachineRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup itemStackGroup = recipeLayout.getItemStacks();

        SlotData[] slots = type.getSlots();
        List<IIngredients> ingredientList;
        for (int i = 0; i < slots.length; i++) {
            itemStackGroup.init(i, slots[i].type == 0, slots[i].x - SLOT_OFFSET_X, slots[i].y - SLOT_OFFSET_Y);
            int stackIndex = i < type.getInputCount() ? i : i - type.getInputCount(); //TODO register helper?
            itemStackGroup.set(i, i < type.getInputCount() ? ingredients.getInputs(VanillaTypes.ITEM).get(stackIndex) : ingredients.getOutputs(VanillaTypes.ITEM).get(stackIndex));
        }
    }
}
