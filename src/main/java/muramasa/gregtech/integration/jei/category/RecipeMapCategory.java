package muramasa.gregtech.integration.jei.category;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import muramasa.gregtech.api.enums.ItemType;
import muramasa.gregtech.api.recipe.RecipeMap;
import muramasa.gregtech.common.utils.Ref;
import muramasa.gregtech.integration.jei.renderer.FluidStackRenderer;
import muramasa.gregtech.integration.jei.wrapper.RecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RecipeMapCategory implements IRecipeCategory<RecipeWrapper> {

    protected static ResourceLocation MULTI_DISPLAY = new ResourceLocation(Ref.MODID, "textures/gui/machine/multi_display.png");

    protected String id, title;
    protected IDrawable background, icon;
    protected IDrawableAnimated progressBar;

    protected int SLOT_OFFSET_X = 4, SLOT_OFFSET_Y = 8;

    public RecipeMapCategory(IGuiHelper guiHelper, RecipeMap map) {
        id = map.getCategoryId();
        title = map.getCategoryName();
        background = guiHelper.drawableBuilder(MULTI_DISPLAY, 3, 10, 170, 75).addPadding(0, 0, 0, 0).build();
        icon = guiHelper.createDrawableIngredient(ItemType.DebugScanner.get(1));
    }

    @Override
    public String getUid() {
        return id;
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
        if (progressBar == null) return;
        progressBar.draw(minecraft, 75, 21);
    }

    @Override
    public void setRecipe(IRecipeLayout layout, RecipeWrapper wrapper, IIngredients ingredients) {
        drawRecipeDefault(layout, wrapper, ingredients);
    }

    //TODO convert this class to also take a GuiData, instead of computing it below
    public void drawRecipeDefault(IRecipeLayout layout, RecipeWrapper wrapper, IIngredients ingredients) {
        IGuiItemStackGroup itemGroup = layout.getItemStacks();
        IGuiFluidStackGroup fluidGroup = layout.getFluidStacks();

        int index = 0;
        if (wrapper.recipe.hasInputStacks()) {
            for (int i = 0; i < wrapper.recipe.getInputStacks().length; i++) {
                int xStart, yStart;
                if (wrapper.recipe.getInputStacks().length <= 3) {
                    xStart = 31 - 9 * (wrapper.recipe.getInputStacks().length - 1);
                    yStart = (22 - SLOT_OFFSET_Y);
                } else {
                    xStart = i >= 3 ? -41 : 13;
                    yStart = i >= 3 ? (29 - SLOT_OFFSET_Y) : (13 - SLOT_OFFSET_Y);
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
                    yStart = (22 - SLOT_OFFSET_Y);
                } else {
                    xStart = i >= 3 ? 49 : 103;
                    yStart = i >= 3 ? (29 - SLOT_OFFSET_Y) : (13 - SLOT_OFFSET_Y);
                }
                itemGroup.init(index, false, xStart + (i * 18), yStart);
                itemGroup.set(index++, wrapper.recipe.getOutputStacks()[i]);
            }
        }
        index = 0;
        if (wrapper.recipe.hasInputFluids()) {
            for (int i = 0; i < wrapper.recipe.getInputFluids().length; i++) {
                int xStart = wrapper.recipe.getInputFluids().length <= 1 ? 50 : 50 - (18 * index);
                fluidGroup.init(index, true, new FluidStackRenderer(), xStart, 60 - SLOT_OFFSET_Y, 16, 16, 0, 0);
                fluidGroup.set(index++, wrapper.recipe.getInputFluids()[i]);
            }
        }
        if (wrapper.recipe.hasOutputFluids()) {
            for (int i = 0; i < wrapper.recipe.getOutputFluids().length; i++) {
                int xStart = 104 + (i * 18);
                fluidGroup.init(index, false, new FluidStackRenderer(), xStart, 60 - SLOT_OFFSET_Y, 16, 16, 0, 0);
                fluidGroup.set(index++, wrapper.recipe.getOutputFluids()[i]);
            }
        }
    }
}
