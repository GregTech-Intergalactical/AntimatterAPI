package muramasa.antimatter.integration.jei.category;

import com.mojang.blaze3d.matrix.MatrixStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.gui.SlotData;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.integration.jei.renderer.IRecipeInfoRenderer;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.recipe.map.RecipeMap;
import muramasa.antimatter.recipe.ingredient.AntimatterIngredient;
import muramasa.antimatter.recipe.ingredient.StackListIngredient;
import muramasa.antimatter.recipe.ingredient.TagIngredient;
import muramasa.antimatter.util.Utils;
import muramasa.antimatter.util.int4;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RecipeMapCategory implements IRecipeCategory<Recipe> {

    protected static int JEI_OFFSET_X = 1, JEI_OFFSET_Y = 1;
    //protected static FluidStackRenderer fluidRenderer = new FluidStackRenderer();
    protected static IGuiHelper guiHelper;

    protected String id, title;
    protected IDrawable background, icon;
    protected IDrawableAnimated progressBar;
    protected GuiData gui;
    protected Tier guiTier;
    private final IRecipeInfoRenderer infoRenderer;

    public RecipeMapCategory(RecipeMap<?> map, GuiData gui, Tier defaultTier, String blockItemModel) {
        id = map.getId();
        this.guiTier = map.getGuiTier() == null ? defaultTier : map.getGuiTier();
        title = map.getDisplayName().getString();
        int4 padding = gui.getPadding(), area = gui.getArea(), progress = gui.getDir().getUV();
        background = guiHelper.drawableBuilder(gui.getTexture(guiTier,"machine"), area.x, area.y, area.z, area.w).addPadding(padding.x, padding.y, padding.z, padding.w).build();
        progressBar = guiHelper.drawableBuilder(gui.getTexture(guiTier,"machine"), progress.x, progress.y, progress.z, progress.w).buildAnimated(50, IDrawableAnimated.StartDirection.LEFT, false);
        Block block = AntimatterAPI.get(BlockMachine.class, blockItemModel == null ? "" : blockItemModel + "_" + defaultTier.getId());
        icon = block == null ? guiHelper.createDrawableIngredient(new ItemStack(Data.DEBUG_SCANNER,1)) : guiHelper.createDrawableIngredient(new ItemStack(block.asItem(),1));
        this.gui = gui;
        this.infoRenderer = map.getInfoRenderer();
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
            List<Ingredient> inputs = new ObjectArrayList<>(recipe.getInputItems().size());
            for (AntimatterIngredient ing : recipe.compileInput()) {
                if (ing instanceof TagIngredient) {
                    ResourceLocation rl = ((TagIngredient) ing).getTag();
                    if (rl != null) {
                        recipe.tagsToRender.putIfAbsent(inputs.size(), rl);
                    }
                }
                if (ing instanceof StackListIngredient) {
                    recipe.infoToRender.putIfAbsent(inputs.size(), ing.getMatchingStacks().length);
                }
                inputs.add(ing);
            }
            ingredients.setInputLists(VanillaTypes.ITEM,inputs.stream().map(t -> Arrays.asList(t.getMatchingStacks())).collect(Collectors.toList()));
        }
        if (recipe.hasOutputItems()) {
            ingredients.setOutputLists(VanillaTypes.ITEM, Arrays.stream(recipe.getOutputItems(false)).map(Collections::singletonList).collect(Collectors.toList()));
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
    public void draw(Recipe recipe, MatrixStack stack, double mouseX, double mouseY) {
        if (progressBar != null)
            progressBar.draw(stack, gui.getDir().getPos().x + gui.getArea().x, gui.getDir().getPos().y + gui.getArea().y);
        infoRenderer.render(stack, recipe, Minecraft.getInstance().fontRenderer, JEI_OFFSET_X, gui.getArea().y + JEI_OFFSET_Y + gui.getArea().z/2);
    }

    @Override
    public void setRecipe(IRecipeLayout layout, Recipe recipe, @Nonnull IIngredients ingredients) {
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
                int s = 0;
                List<List<ItemStack>> stacks = ingredients.getInputs(VanillaTypes.ITEM);
                if (stacks.size() > 0) {
                    slotCount = Math.min(slotCount, stacks.size());
                    for (; s < slotCount; s++) {
                        itemGroup.init(groupIndex, true, slots.get(s).getX() - offsetX, slots.get(s).getY() - offsetY);
                        List<ItemStack> input = stacks.get(s);
                        ItemStack whichStack = input.size() == 1 ? input.get(0) : input.get(Ref.RNG.nextInt(stacks.get(s).size()));
                        itemGroup.set(groupIndex++, whichStack);
                        inputItems++;
                    }
                }
            }
        }
        if (recipe.hasOutputItems()) {
            slots = gui.getSlots(SlotType.IT_OUT, guiTier);
            slotCount = slots.size();
            if (slotCount > 0) {
                List<List<ItemStack>> stacks = ingredients.getOutputs(VanillaTypes.ITEM);
                slotCount = Math.min(slotCount, stacks.size());
                for (int s = 0; s < slotCount; s++) {
                    itemGroup.init(groupIndex, false, slots.get(s).getX() - offsetX, slots.get(s).getY() - offsetY);
                    itemGroup.set(groupIndex++, stacks.get(s));
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
                    fluidGroup.init(groupIndex, true, slots.get(s).getX() - (offsetX - 1), slots.get(s).getY() - (offsetY - 1), 16, 16, fluids[s].getAmount(), false, null);
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
                    fluidGroup.init(groupIndex, false, slots.get(s).getX() - (offsetX - 1), slots.get(s).getY() - (offsetY - 1), 16, 16, fluids[s].getAmount(), false, null);
                    fluidGroup.set(groupIndex++, fluids[s]);
                }
            }
        }

        final int finalInputItems = inputItems;
        itemGroup.addTooltipCallback((index, input, stack, tooltip) -> {
            ResourceLocation rl;
            if ((rl = recipe.tagsToRender.get(index)) != null) {
                tooltip.add(new StringTextComponent("Accepts any " + rl).mergeStyle(TextFormatting.GOLD));
            }
            int i;
            if ((i = recipe.infoToRender.get(index)) != 0) {
                tooltip.add(new StringTextComponent("Accepts " + i + " different items.").mergeStyle(TextFormatting.GOLD));
            }

            if (input && (Utils.hasNoConsumeTag(stack) || stack.getCount() == 0)) tooltip.add(new StringTextComponent("Does not get consumed in the process.").mergeStyle(TextFormatting.WHITE));
            if (recipe.hasChances() && !input) {
                int chanceIndex = index - finalInputItems;
                if (recipe.getChances()[chanceIndex] < 100) {
                    tooltip.add(new StringTextComponent("Chance: " + recipe.getChances()[chanceIndex] + "%").mergeStyle(TextFormatting.WHITE));
                }
            }
        });
        fluidGroup.addTooltipCallback((index, input, stack, tooltip) -> {
            if (input && Utils.hasNoConsumeTag(stack)) tooltip.add(new StringTextComponent("Does not get consumed in the process").mergeStyle(TextFormatting.WHITE));
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
