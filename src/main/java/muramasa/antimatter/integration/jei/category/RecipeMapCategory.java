package muramasa.antimatter.integration.jei.category;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.gui.SlotData;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.recipe.RecipeMap;
import muramasa.antimatter.util.Utils;
import muramasa.antimatter.util.int4;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.List;

//import mezz.jei.plugins.vanilla.ingredients.fluid.FluidStackRenderer;


public class RecipeMapCategory implements IRecipeCategory<Recipe> {

    protected static int JEI_OFFSET_X = 1, JEI_OFFSET_Y = 1;
    //protected static FluidStackRenderer fluidRenderer = new FluidStackRenderer();
    protected static IGuiHelper guiHelper;

    protected String id, title;
    protected IDrawable background, icon;
    protected IDrawableAnimated progressBar;
    protected GuiData gui;
    protected Tier guiTier;

//    public RecipeMapCategory(MachineStack stack, Tier guiTier) {
//        this(stack.getType().getRecipeMap(), stack.getType().getGui(), guiTier);
//        icon = guiHelper.createDrawableIngredient(stack.asItemStack());
//    }
//
//    public RecipeMapCategory(MachineStack stack, GuiData gui, Tier guiTier) {
//        this(stack.getType().getRecipeMap(), gui, guiTier);
//        icon = guiHelper.createDrawableIngredient(stack.asItemStack());
//    }

    public RecipeMapCategory(RecipeMap<?> map, GuiData gui, Tier guiTier) {
        id = map.getId();
        title = map.getDisplayName().getFormattedText();
        int4 padding = gui.getPadding(), area = gui.getArea(), progress = gui.getDir().getUV();
        background = guiHelper.drawableBuilder(gui.getTexture(guiTier), area.x, area.y, area.z, area.w).addPadding(padding.x, padding.y, padding.z, padding.w).build();
        progressBar = guiHelper.drawableBuilder(gui.getTexture(guiTier), progress.x, progress.y, progress.z, progress.w).buildAnimated(50, IDrawableAnimated.StartDirection.LEFT, false);
        icon = guiHelper.createDrawableIngredient(Data.DEBUG_SCANNER.get(1));
        this.gui = gui;
        this.guiTier = guiTier;
    }

    @Override
    public ResourceLocation getUid() {
        return new ResourceLocation(Ref.ID, id);
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setIngredients(Recipe recipe, IIngredients ingredients) {
        if (recipe.hasInputItems()) {
            ingredients.setInputs(VanillaTypes.ITEM, Arrays.asList(recipe.getInputItems()));
        }
        if (recipe.hasOutputItems()) {
            ingredients.setOutputs(VanillaTypes.ITEM, Arrays.asList(recipe.getOutputItems()));
        }
        if (recipe.hasInputFluids()) {
            ingredients.setInputs(VanillaTypes.FLUID, Arrays.asList(recipe.getInputFluids()));
        }
        if (recipe.hasOutputFluids()) {
            ingredients.setOutputs(VanillaTypes.FLUID, Arrays.asList(recipe.getOutputFluids()));
        }
    }

    @Override
    public Class getRecipeClass(){
        return Recipe.class;
    }

    @Override
    public void draw(Recipe recipe, double mouseX, double mouseY) {
        if (progressBar != null)
            progressBar.draw(gui.getDir().getPos().x + gui.getArea().x, gui.getDir().getPos().y + gui.getArea().y);
    }

    @Override
    public void setRecipe(IRecipeLayout layout, Recipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup itemGroup = layout.getItemStacks();
        IGuiFluidStackGroup fluidGroup = layout.getFluidStacks();
        List<SlotData> slots;
        int groupIndex = 0, slotCount;
        int offsetX = gui.getArea().x + JEI_OFFSET_X, offsetY = gui.getArea().y + JEI_OFFSET_Y;

        int inputItems = 0, inputFluids = 0;
        if (recipe.hasInputItems()) {
            slots = gui.getSlots(SlotType.IT_IN, guiTier);
            slotCount = slots.size();
            if (slotCount > 0) {
                ItemStack[] stacks = recipe.getInputItems();
                slotCount = Math.min(slotCount, stacks.length);
                for (int s = 0; s < slotCount; s++) {
                    itemGroup.init(groupIndex, true, slots.get(s).x - offsetX, slots.get(s).y - offsetY);
                    itemGroup.set(groupIndex++, stacks[s]);
                    inputItems++;
                }
            }
        }
        if (recipe.hasOutputItems()) {
            slots = gui.getSlots(SlotType.IT_OUT, guiTier);
            slotCount = slots.size();
            if (slotCount > 0) {
                ItemStack[] stacks = recipe.getOutputItems();
                slotCount = Math.min(slotCount, stacks.length);
                for (int s = 0; s < slotCount; s++) {
                    itemGroup.init(groupIndex, false, slots.get(s).x - offsetX, slots.get(s).y - offsetY);
                    itemGroup.set(groupIndex++, stacks[s]);
                }
            }
        }

        groupIndex = 0;
        if (recipe.hasInputFluids()) {
            slots = gui.getSlots(SlotType.FL_IN, guiTier);
            slotCount = slots.size();
            if (slotCount > 0) {
                FluidStack[] fluids = recipe.getInputFluids();
                slotCount = Math.min(slotCount, fluids.length);
                for (int s = 0; s < slotCount; s++) {
                    fluidGroup.init(groupIndex, true, slots.get(s).x - (offsetX - 1), slots.get(s).y - (offsetY - 1), 16, 16, 0, false, null);
                    fluidGroup.set(groupIndex++, fluids[s]);
                    inputFluids++;
                }
            }
        }
        if (recipe.hasOutputFluids()) {
            slots = gui.getSlots(SlotType.FL_OUT, guiTier);
            slotCount = slots.size();
            if (slotCount > 0) {
                FluidStack[] fluids = recipe.getOutputFluids();
                slotCount = Math.min(slotCount, fluids.length);
                for (int s = 0; s < slotCount; s++) {
                    fluidGroup.init(groupIndex, false, slots.get(s).x - (offsetX - 1), slots.get(s).y - (offsetY - 1), 16, 16, 0, false, null);
                    fluidGroup.set(groupIndex++, fluids[s]);
                }
            }
        }

        final int finalInputItems = inputItems;
//        final int finalInputFluids = inputFluids;
        itemGroup.addTooltipCallback((index, input, stack, tooltip) -> {
            if (input && Utils.hasNoConsumeTag(stack)) tooltip.add(TextFormatting.WHITE + "Does not get consumed in the process");
            if (recipe.hasChances() && !input) {
                int chanceIndex = index - finalInputItems;
                if (recipe.getChances()[chanceIndex] < 100) {
                    tooltip.add(TextFormatting.WHITE + "Chance: " + recipe.getChances()[chanceIndex] + "%");
                }
            }
        });
        fluidGroup.addTooltipCallback((index, input, stack, tooltip) -> {
            if (input && Utils.hasNoConsumeTag(stack)) tooltip.add(TextFormatting.WHITE + "Does not get consumed in the process");
            //TODO add fluid chances to recipe
//            if (wrapper.recipe.hasChances() && !input) {
//                int chanceIndex = index - finalInputFluids;
//                if (wrapper.recipe.getChances()[chanceIndex] < 100) {
//                    tooltip.add(TextFormatting.WHITE + "Chance: " + wrapper.recipe.getChances()[chanceIndex] + "%");
//                }
//            }
        });
    }

    public static void setGuiHelper(IGuiHelper helper) {
        guiHelper = helper;
    }
}
