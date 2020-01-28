//package muramasa.antimatter.integration.jei.category;
//
//import mezz.jei.api.IGuiHelper;
//import mezz.jei.api.gui.*;
//import mezz.jei.api.ingredients.IIngredients;
//import mezz.jei.api.recipe.IRecipeCategory;
//import muramasa.gtu.Ref;
//import muramasa.gtu.api.guiold.GuiData;
//import muramasa.gtu.api.guiold.SlotData;
//import muramasa.gtu.api.guiold.SlotType;
//import muramasa.antimatter.machines.Tier;
//import muramasa.antimatter.recipe.RecipeMap;
//import muramasa.antimatter.util.Utils;
//import muramasa.antimatter.util.int4;
//import muramasa.gtu.common.Data;
//import muramasa.antimatter.integration.jei.renderer.FluidStackRenderer;
//import muramasa.antimatter.integration.jei.wrapper.RecipeWrapper;
//import net.minecraft.client.Minecraft;
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.text.TextFormatting;
//import net.minecraftforge.fluids.FluidStack;
//
//import javax.annotation.Nullable;
//import java.util.List;
//
//public class RecipeMapCategory implements IRecipeCategory<RecipeWrapper> {
//
//    protected static int JEI_OFFSET_X = 1, JEI_OFFSET_Y = 1;
//    protected static FluidStackRenderer fluidRenderer = new FluidStackRenderer();
//    protected static IGuiHelper guiHelper;
//
//    protected String id, title;
//    protected IDrawable background, icon;
//    protected IDrawableAnimated progressBar;
//    protected GuiData gui;
//    protected Tier guiTier;
//
//    public RecipeMapCategory(MachineStack stack, Tier guiTier) {
//        this(stack.getType().getRecipeMap(), stack.getType().getGui(), guiTier);
//        icon = guiHelper.createDrawableIngredient(stack.asItemStack());
//    }
//
//    public RecipeMapCategory(MachineStack stack, GuiData gui, Tier guiTier) {
//        this(stack.getType().getRecipeMap(), gui, guiTier);
//        icon = guiHelper.createDrawableIngredient(stack.asItemStack());
//    }
//
//    public RecipeMapCategory(RecipeMap map, GuiData gui, Tier guiTier) {
//        id = map.getId();
//        title = map.getDisplayName();
//        int4 padding = gui.getPadding(), area = gui.getArea(), progress = gui.getDir().getUV();
//        background = guiHelper.drawableBuilder(gui.getTexture(guiTier), area.x, area.y, area.z, area.w).addPadding(padding.x, padding.y, padding.z, padding.w).build();
//        progressBar = guiHelper.drawableBuilder(gui.getTexture(guiTier), progress.x, progress.y, progress.z, progress.w).buildAnimated(50, IDrawableAnimated.StartDirection.LEFT, false);
//        icon = guiHelper.createDrawableIngredient(Data.DebugScanner.get(1));
//        this.gui = gui;
//        this.guiTier = guiTier;
//    }
//
//    @Override
//    public String getUid() {
//        return id;
//    }
//
//    @Override
//    public String getTitle() {
//        return title;
//    }
//
//    @Override
//    public String getModName() {
//        return Ref.NAME;
//    }
//
//    @Override
//    public IDrawable getBackground() {
//        return background;
//    }
//
//    @Nullable
//    @Override
//    public IDrawable getIcon() {
//        return icon;
//    }
//
//    @Override
//    public void drawExtras(Minecraft minecraft) {
//        if (progressBar == null) return;
//        progressBar.draw(minecraft, gui.getDir().getPos().x + gui.getArea().x, gui.getDir().getPos().y + gui.getArea().y);
//    }
//
//    @Override
//    public void setRecipe(IRecipeLayout layout, RecipeWrapper wrapper, IIngredients ingredients) {
//        wrapper.setPadding(gui.getPadding());
//        wrapper.setInfoRenderer(gui.getInfoRenderer());
//        IGuiItemStackGroup itemGroup = layout.getItemStacks();
//        IGuiFluidStackGroup fluidGroup = layout.getFluidStacks();
//        List<SlotData> slots;
//        int groupIndex = 0, slotCount;
//        int offsetX = gui.getArea().x + JEI_OFFSET_X, offsetY = gui.getArea().y + JEI_OFFSET_Y;
//
//        int inputItems = 0, inputFluids = 0;
//        if (wrapper.recipe.hasInputItems()) {
//            slots = gui.getSlots(SlotType.IT_IN, guiTier);
//            slotCount = slots.size();
//            if (slotCount > 0) {
//                ItemStack[] stacks = wrapper.recipe.getInputItems();
//                slotCount = Math.min(slotCount, stacks.length);
//                for (int s = 0; s < slotCount; s++) {
//                    itemGroup.init(groupIndex, true, slots.get(s).x - offsetX, slots.get(s).y - offsetY);
//                    itemGroup.set(groupIndex++, stacks[s]);
//                    inputItems++;
//                }
//            }
//        }
//        if (wrapper.recipe.hasOutputItems()) {
//            slots = gui.getSlots(SlotType.IT_OUT, guiTier);
//            slotCount = slots.size();
//            if (slotCount > 0) {
//                ItemStack[] stacks = wrapper.recipe.getOutputItems();
//                slotCount = Math.min(slotCount, stacks.length);
//                for (int s = 0; s < slotCount; s++) {
//                    itemGroup.init(groupIndex, false, slots.get(s).x - offsetX, slots.get(s).y - offsetY);
//                    itemGroup.set(groupIndex++, stacks[s]);
//                }
//            }
//        }
//
//        groupIndex = 0;
//        if (wrapper.recipe.hasInputFluids()) {
//            slots = gui.getSlots(SlotType.FL_IN, guiTier);
//            slotCount = slots.size();
//            if (slotCount > 0) {
//                FluidStack[] fluids = wrapper.recipe.getInputFluids();
//                slotCount = Math.min(slotCount, fluids.length);
//                for (int s = 0; s < slotCount; s++) {
//                    fluidGroup.init(groupIndex, true, fluidRenderer, slots.get(s).x - (offsetX - 1), slots.get(s).y - (offsetY - 1), 16, 16, 0, 0);
//                    fluidGroup.set(groupIndex++, fluids[s]);
//                    inputFluids++;
//                }
//            }
//        }
//        if (wrapper.recipe.hasOutputFluids()) {
//            slots = gui.getSlots(SlotType.FL_OUT, guiTier);
//            slotCount = slots.size();
//            if (slotCount > 0) {
//                FluidStack[] fluids = wrapper.recipe.getOutputFluids();
//                slotCount = Math.min(slotCount, fluids.length);
//                for (int s = 0; s < slotCount; s++) {
//                    fluidGroup.init(groupIndex, false, fluidRenderer, slots.get(s).x - (offsetX - 1), slots.get(s).y - (offsetY - 1), 16, 16, 0, 0);
//                    fluidGroup.set(groupIndex++, fluids[s]);
//                }
//            }
//        }
//
//        final int finalInputItems = inputItems;
//        final int finalInputFluids = inputFluids;
//        itemGroup.addTooltipCallback((index, input, stack, tooltip) -> {
//            if (input && Utils.hasNoConsumeTag(stack)) tooltip.add(TextFormatting.WHITE + "Does not get consumed in the process");
//            if (wrapper.recipe.hasChances() && !input) {
//                int chanceIndex = index - finalInputItems;
//                if (wrapper.recipe.getChances()[chanceIndex] < 100) {
//                    tooltip.add(TextFormatting.WHITE + "Chance: " + wrapper.recipe.getChances()[chanceIndex] + "%");
//                }
//            }
//        });
//        fluidGroup.addTooltipCallback((index, input, stack, tooltip) -> {
//            if (input && Utils.hasNoConsumeTag(stack)) tooltip.add(TextFormatting.WHITE + "Does not get consumed in the process");
//            //TODO add fluid chances to recipe
////            if (wrapper.recipe.hasChances() && !input) {
////                int chanceIndex = index - finalInputFluids;
////                if (wrapper.recipe.getChances()[chanceIndex] < 100) {
////                    tooltip.add(TextFormatting.WHITE + "Chance: " + wrapper.recipe.getChances()[chanceIndex] + "%");
////                }
////            }
//        });
//    }
//
//    public static void setGuiHelper(IGuiHelper helper) {
//        guiHelper = helper;
//    }
//}
