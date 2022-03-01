package muramasa.antimatter.integration.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.gui.SlotData;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.integration.jei.renderer.IRecipeInfoRenderer;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.machine.BlockMultiMachine;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.recipe.ingredient.FluidIngredient;
import muramasa.antimatter.recipe.map.RecipeMap;
import muramasa.antimatter.util.int4;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RecipeMapCategory implements IRecipeCategory<Recipe> {

    protected static int JEI_OFFSET_X = 1, JEI_OFFSET_Y = 1;
    //protected static FluidStackRenderer fluidRenderer = new FluidStackRenderer();
    protected static IGuiHelper guiHelper;

    protected String title;
    protected final ResourceLocation loc;
    protected IDrawable background, icon;
    protected IDrawableAnimated progressBar;
    protected GuiData gui;
    protected Tier guiTier;
    private final IRecipeInfoRenderer infoRenderer;

    public RecipeMapCategory(RecipeMap<?> map, GuiData gui, Tier defaultTier, ResourceLocation blockItemModel) {
        loc = map.getLoc();
        this.guiTier = map.getGuiTier() == null ? defaultTier : map.getGuiTier();
        title = map.getDisplayName().getString();
        int4 padding = gui.getPadding(), area = gui.getArea(), progress = gui.dir.getUV();
        background = guiHelper.drawableBuilder(gui.getTexture(guiTier, "machine"), area.x, area.y, area.z, area.w).addPadding(padding.x, padding.y, padding.z, padding.w).build();
        progressBar = guiHelper.drawableBuilder(gui.getTexture(guiTier, "machine"), progress.x, progress.y, progress.z, progress.w).buildAnimated(50, IDrawableAnimated.StartDirection.LEFT, false);
        Object icon = map.getIcon();
        if (icon != null) {
            if (icon instanceof ItemStack) {
                this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, (ItemStack) icon);
            }
            if (icon instanceof ItemLike) {
                this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack((ItemLike) icon));
            }
            if (icon instanceof IDrawable) {
                this.icon = (IDrawable) icon;
            }
        } else {
            Block block = AntimatterAPI.get(BlockMachine.class, blockItemModel == null ? "" : blockItemModel.getPath() + "_" + defaultTier.getId(), blockItemModel == null ? "" : blockItemModel.getNamespace());
            if (block == null)
                block = AntimatterAPI.get(BlockMultiMachine.class, blockItemModel == null ? "" : blockItemModel.getPath() + "_" + defaultTier.getId(), blockItemModel == null ? "" : blockItemModel.getNamespace());
            this.icon = block == null ? guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(Data.DEBUG_SCANNER, 1)) : guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(block.asItem(), 1));
        }
        this.gui = gui;
        this.infoRenderer = map.getInfoRenderer();
    }

    @Override
    public ResourceLocation getUid() {
        return loc;
    }

    @Override
    public Component getTitle() {
        return new TextComponent(title);
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
    public void setRecipe(IRecipeLayoutBuilder builder, Recipe recipe, IFocusGroup focuses) {
        List<List<ItemStack>> inputs = recipe.hasInputItems() ? recipe.getInputItems().stream().map(t -> Arrays.asList(t.get().getItems())).collect(Collectors.toList()) : Collections.emptyList();
        List<ItemStack> outputs = recipe.hasOutputItems() ? Arrays.stream(recipe.getOutputItems()).toList() : Collections.emptyList();
        List<SlotData<?>> slots;
        int groupIndex = 0, slotCount;
        int offsetX = gui.getArea().x + JEI_OFFSET_X, offsetY = gui.getArea().y + JEI_OFFSET_Y;
        int inputItems = 0, inputFluids = 0;
        if (recipe.hasInputItems()) {
            slots = gui.getSlots().getSlots(SlotType.IT_IN, guiTier);
            slotCount = slots.size();
            if (slotCount > 0) {
                int s = 0;
                if (inputs.size() > 0) {
                    slotCount = Math.min(slotCount, inputs.size());
                    for (; s < slotCount; s++) {
                        IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.INPUT, slots.get(s).getX() - (offsetX - 1), slots.get(s).getY() - (offsetY - 1));
                        List<ItemStack> input = inputs.get(s);
                        if (input.size() == 0) {
                            List<ItemStack> st = new ObjectArrayList<>(1);
                            st.add(new ItemStack(Data.DEBUG_SCANNER, 1));
                            slot.addIngredients(VanillaTypes.ITEM, st);
                        } else {
                            slot.addIngredients(VanillaTypes.ITEM, input);
//                            itemGroup.set(groupIndex++, input);
                            inputItems++;
                        }
                    }
                }
            }
        }
        if (recipe.hasOutputItems()) {
            slots = gui.getSlots().getSlots(SlotType.IT_OUT, guiTier);
            slotCount = slots.size();
            if (slotCount > 0) {
                slotCount = Math.min(slotCount, outputs.size());
                for (int s = 0; s < slotCount; s++) {
                    IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.OUTPUT, slots.get(s).getX() - (offsetX - 1), slots.get(s).getY() - (offsetY - 1));
                    slot.addIngredient(VanillaTypes.ITEM, outputs.get(s));
                }
            }
        }

        groupIndex = 0;
        if (recipe.hasInputFluids()) {
            slots = gui.getSlots().getSlots(SlotType.FL_IN, guiTier);
            slotCount = slots.size();
            if (slotCount > 0) {
                List<FluidIngredient> fluids = recipe.getInputFluids();
                slotCount = Math.min(slotCount, fluids.size());
                for (int s = 0; s < slotCount; s++) {
                    IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.INPUT, slots.get(s).getX() - (offsetX - 1), slots.get(s).getY() - (offsetY - 1));
                    slot.addIngredients(VanillaTypes.FLUID, Arrays.asList(fluids.get(s).getStacks()));
                    slot.setFluidRenderer(fluids.get(s).getAmount(), true, 16, 16);
                    inputFluids++;
                }
            }
        }
        if (recipe.hasOutputFluids()) {
            slots = gui.getSlots().getSlots(SlotType.FL_OUT, guiTier);
            slotCount = slots.size();
            if (slotCount > 0) {
                FluidStack[] fluids = recipe.getOutputFluids();
                slotCount = Math.min(slotCount, fluids.length);
                for (int s = 0; s < slotCount; s++) {
                    IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.INPUT, slots.get(s).getX() - (offsetX - 1), slots.get(s).getY() - (offsetY - 1));
                    slot.setFluidRenderer(fluids[s].getAmount(), true, 16, 16);
                    slot.addIngredient(VanillaTypes.FLUID, fluids[s]);
                }
            }
        }

        final int finalInputItems = inputItems;
       /* itemGroup.addTooltipCallback((index, input, stack, tooltip) -> {
            if (input) {
                if (recipe.hasInputItems()) {
                    if (recipe.getInputItems().size() >= index && recipe.getInputItems().get(index).ignoreConsume()) {
                        tooltip.add(new TextComponent("Does not get consumed in the process.").withStyle(ChatFormatting.WHITE));
                    }
                    if (recipe.getInputItems().size() >= index && recipe.getInputItems().get(index).ignoreNbt()) {
                        tooltip.add(new TextComponent("Ignores NBT.").withStyle(ChatFormatting.WHITE));
                    }
                    if (recipe.getInputItems().size() >= index) {
                        Ingredient i = recipe.getInputItems().get(index).get();
                        if (RecipeMap.isIngredientSpecial(i)) {
                            tooltip.add(new TextComponent("Special ingredient. Class name: ").withStyle(ChatFormatting.GRAY).append(new TextComponent(i.getClass().getSimpleName()).withStyle(ChatFormatting.GOLD)));
                        }
                    }
                }
            }
            if (recipe.hasChances() && !input) {
                int chanceIndex = index - finalInputItems;
                if (recipe.getChances()[chanceIndex] < 100) {
                    tooltip.add(new TextComponent("Chance: " + recipe.getChances()[chanceIndex] + "%").withStyle(ChatFormatting.WHITE));
                }
            }
        });
        fluidGroup.addTooltipCallback((index, input, stack, tooltip) -> {
            if (input && Utils.hasNoConsumeTag(stack))
                tooltip.add(new TextComponent("Does not get consumed in the process").withStyle(ChatFormatting.WHITE));
            //TODO add fluid chances to recipe
//            if (wrapper.recipe.hasChances() && !input) {
//                int chanceIndex = index - finalInputFluids;
//                if (wrapper.recipe.getChances()[chanceIndex] < 100) {
//                    tooltip.add(TextFormatting.WHITE + "Chance: " + wrapper.recipe.getChances()[chanceIndex] + "%");
//                }
//            }
        });
        */
    }
    /*
    private static IRecipeSlotTooltipCallback itemCallback(Recipe recipe, boolean input) {
        return (a,b) ->
            if (input) {
                if (recipe.hasInputItems()) {
                    a.getDisplayedIngredient().flatMap(ing -> {
                        Ingredient i = ing.getIngredient();
                    })
                    if (recipe.getInputItems().get(index).ignoreConsume()) {
                        tooltip.add(new TextComponent("Does not get consumed in the process.").withStyle(ChatFormatting.WHITE));
                    }
                    if (recipe.getInputItems().size() >= index && recipe.getInputItems().get(index).ignoreNbt()) {
                        tooltip.add(new TextComponent("Ignores NBT.").withStyle(ChatFormatting.WHITE));
                    }
                    if (recipe.getInputItems().size() >= index) {
                        Ingredient i = recipe.getInputItems().get(index).get();
                        if (RecipeMap.isIngredientSpecial(i)) {
                            tooltip.add(new TextComponent("Special ingredient. Class name: ").withStyle(ChatFormatting.GRAY).append(new TextComponent(i.getClass().getSimpleName()).withStyle(ChatFormatting.GOLD)));
                        }
                    }
                }
            }
            if (recipe.hasChances() && !input) {
                int chanceIndex = index - finalInputItems;
                if (recipe.getChances()[chanceIndex] < 100) {
                    tooltip.add(new TextComponent("Chance: " + recipe.getChances()[chanceIndex] + "%").withStyle(ChatFormatting.WHITE));
                }
            }
        }
    }*/

    @Override
    public Class getRecipeClass() {
        return Recipe.class;
    }

    @Override
    public void draw(Recipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        if (progressBar != null)
            progressBar.draw(stack, gui.dir.getPos().x + gui.getArea().x, gui.dir.getPos().y + gui.getArea().y);
        infoRenderer.render(stack, recipe, Minecraft.getInstance().font, JEI_OFFSET_X, gui.getArea().y + JEI_OFFSET_Y + gui.getArea().z / 2);    }

    public static void setGuiHelper(IGuiHelper helper) {
        guiHelper = helper;
    }


}
