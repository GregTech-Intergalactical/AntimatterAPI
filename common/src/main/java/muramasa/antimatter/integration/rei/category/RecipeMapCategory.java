package muramasa.antimatter.integration.rei.category;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.recipe.RecipeIngredientRole;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.gui.BarDir;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.gui.SlotData;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.integration.jei.AntimatterJEIPlugin;
import muramasa.antimatter.integration.jeirei.renderer.IRecipeInfoRenderer;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.recipe.ingredient.FluidIngredient;
import muramasa.antimatter.recipe.ingredient.RecipeIngredient;
import muramasa.antimatter.recipe.map.IRecipeMap;
import muramasa.antimatter.recipe.map.RecipeMap;
import muramasa.antimatter.util.Utils;
import muramasa.antimatter.util.int4;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RecipeMapCategory implements DisplayCategory<RecipeMapDisplay> {

    protected static int JEI_OFFSET_X = 1, JEI_OFFSET_Y = 1;
    protected Component title;
    protected final CategoryIdentifier<RecipeMapDisplay> loc;
    protected Renderer icon;
    protected Parameters progressBar;
    protected GuiData gui;
    protected Tier guiTier;
    private final IRecipeInfoRenderer infoRenderer;

    public RecipeMapCategory(IRecipeMap map, GuiData gui, Tier defaultTier, ResourceLocation blockItemModel) {
        loc = CategoryIdentifier.of(map.getLoc());
        this.guiTier = map.getGuiTier() == null ? defaultTier : map.getGuiTier();
        title = map.getDisplayName();
        int4 progress = gui.dir.getUV();
        progressBar = new Parameters(gui.getTexture(guiTier, "machine"), gui.dir.getPos().x + 6, gui.dir.getPos().y + 6, progress.z, progress.w, progress.x, progress.y, gui.dir, gui.barFill);
        Object icon = map.getIcon();
        if (icon != null) {
            if (icon instanceof ItemStack stack) {
                this.icon = EntryStacks.of(stack);
            }
            if (icon instanceof ItemLike itemLike) {
                this.icon = EntryStacks.of(itemLike);
            }
        } else {
            Block block = AntimatterAPI.get(Block.class, blockItemModel == null ? "" : blockItemModel.getPath() + "_" + defaultTier.getId(), blockItemModel == null ? "" : blockItemModel.getNamespace());
            this.icon = block == null ? EntryStacks.of(Data.DEBUG_SCANNER) : EntryStacks.of(block.asItem());
        }
        this.gui = gui;
        this.infoRenderer = map.getInfoRenderer();
    }

    @Override
    public int getDisplayHeight() {
        return gui.getArea().w + 4;
    }

    @Override
    public int getDisplayWidth(RecipeMapDisplay display) {
        return gui.getArea().z + 4;
    }

    @Override
    public List<Widget> setupDisplay(RecipeMapDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createDrawableWidget((helper, matrices, mouseX, mouseY, delta) -> {
            drawTexture(matrices, gui.getTexture(guiTier, "machine"), bounds.x + 3, bounds.y + 3, gui.getArea().x + 1, gui.getArea().y + 1, bounds.getWidth() - 6, bounds.getHeight() - 6);
        }));
        widgets.addAll(setupSlots(display, bounds));
        double recipeMillis = (double) display.getRecipe().getDuration() * 50;
        if (recipeMillis < 250)
            recipeMillis = 250;
        double finalRecipeMillis = recipeMillis;
        widgets.add(Widgets.createDrawableWidget((helper, matrices, mouseX, mouseY, delta) -> {
            renderProgress(matrices, bounds, progressBar,
                    (float) (System.currentTimeMillis() / finalRecipeMillis % 1.0));
        }));
        widgets.add(Widgets.createDrawableWidget((helper, matrices, mouseX, mouseY, delta) -> {
            infoRenderer.render(matrices, display.getRecipe(), Minecraft.getInstance().font, bounds.x + 1, bounds.y + bounds.getHeight() - 3);
        }));
        return widgets;
    }

    private List<Widget> setupSlots(RecipeMapDisplay display, Rectangle bounds){
        List<Widget> widgets = new ArrayList<>();
        List<List<ItemStack>> inputs = display.getRecipe().hasInputItems() ? display.getRecipe().getInputItems().stream().map(t -> Arrays.asList(t.getItems())).toList() : Collections.emptyList();
        List<ItemStack> outputs = display.getRecipe().hasOutputItems() ? Arrays.stream(display.getRecipe().getOutputItems()).toList() : Collections.emptyList();
        List<SlotData<?>> slots;
        int inputFluidOffset = 0, outputFluidOffset = 0, slotCount;
        int offsetX = gui.getArea().x - 2, offsetY = gui.getArea().y - 2;
        int inputItems = 0, inputFluids = 0;
        if (display.getRecipe().hasInputItems()) {
            slots = gui.getSlots().getSlots(SlotType.IT_IN, guiTier);
            slotCount = slots.size();
            if (slotCount > 0) {
                int s = 0;
                if (inputs.size() > 0) {
                    slotCount = Math.min(slotCount, inputs.size());
                    inputFluidOffset = slotCount;
                    for (; s < slotCount; s++) {
                        Point point = new Point(slots.get(s).getX() - (offsetX) + bounds.x, slots.get(s).getY() - (offsetY) + bounds.y);
                        Slot slot = Widgets.createSlot(point).disableBackground();
                        List<ItemStack> input = inputs.get(s);
                        if (input.size() == 0) {
                            slot.entries(EntryIngredients.of(Data.DEBUG_SCANNER));
                        } else {
                            slot.entries(getInput(display, s));
                            inputItems++;
                        }
                        widgets.add(slot.markInput());
                    }
                }
            }
        }
        if (display.getRecipe().hasOutputItems()) {
            slots = gui.getSlots().getSlots(SlotType.IT_OUT, guiTier);
            slotCount = slots.size();
            if (slotCount > 0) {
                slotCount = Math.min(slotCount, outputs.size());
                outputFluidOffset = slotCount;
                for (int s = 0; s < slotCount; s++) {
                    Point point = new Point(slots.get(s).getX() - (offsetX) + bounds.x, slots.get(s).getY() - (offsetY) + bounds.y);
                    widgets.add(Widgets.createSlot(point).entries(getOutput(display, s)).disableBackground().markOutput());
                }
            }
        }

        if (display.getRecipe().hasInputFluids()) {
            slots = gui.getSlots().getSlots(SlotType.FL_IN, guiTier);
            slotCount = slots.size();
            if (slotCount > 0) {
                List<FluidIngredient> fluids = display.getRecipe().getInputFluids();
                slotCount = Math.min(slotCount, fluids.size());
                for (int s = 0; s < slotCount; s++) {
                    Point point = new Point(slots.get(s).getX() - (offsetX) + bounds.x, slots.get(s).getY() - (offsetY) + bounds.y);
                    widgets.add(Widgets.createSlot(point).entries(getInput(display, s + inputFluidOffset)).disableBackground().markInput());
                    /*slot.setFluidRenderer((int)fluids.get(s).getAmount(), true, 16, 16);
                    slot.addTooltipCallback((ing, list) -> {
                        if (Utils.hasNoConsumeTag(AntimatterJEIPlugin.getIngredient(ing.getDisplayedIngredient().get())))
                            list.add(new TextComponent("Does not get consumed in the process").withStyle(ChatFormatting.WHITE));
                    });*/
                    inputFluids++;
                }
            }
        }
        if (display.getRecipe().hasOutputFluids()) {
            slots = gui.getSlots().getSlots(SlotType.FL_OUT, guiTier);
            slotCount = slots.size();
            if (slotCount > 0) {
                FluidStack[] fluids = display.getRecipe().getOutputFluids();
                slotCount = Math.min(slotCount, fluids.length);
                for (int s = 0; s < slotCount; s++) {
                    Point point = new Point(slots.get(s).getX() - (offsetX) + bounds.x, slots.get(s).getY() - (offsetY) + bounds.y);
                    widgets.add(Widgets.createSlot(point).entries(getOutput(display, s + outputFluidOffset)).disableBackground().markOutput());
                    //slot.setFluidRenderer(fluids[s].getAmount(), true, 16, 16);
                }
            }
        }
        return widgets;
    }

    public static void renderProgress(PoseStack matrices, Rectangle bounds, Parameters params, float percent) {
        int progressTime;
        int realX = bounds.x + params.x - 1, realY = bounds.y + params.y - 1;
        int x = realX, y = realY, xLocation = params.posX, yLocation = params.posY, length = params.length, width = params.width;
        switch (params.dir) {
            case TOP -> {
                progressTime = (int) (params.width * percent);
                if (!params.fill) {
                    progressTime = width - progressTime;
                }
                y = (y + width) - progressTime;
                yLocation = (yLocation + width) - progressTime;
                width = progressTime;
            }
            case LEFT -> {
                progressTime = (int) (params.length * percent);
                if (params.fill) {
                    length = progressTime;
                } else {
                    length = length - progressTime;
                }
            }
            case BOTTOM -> {
                progressTime = (int) (params.width * percent);
                if (params.fill) {
                    width = progressTime;
                } else {
                    width = width - progressTime;
                }
            }
            default -> {
                progressTime = (int) (params.length * percent);
                if (!params.fill) {
                    progressTime = length - progressTime;
                }
                x = (x + length) - progressTime;
                xLocation = (xLocation + length) - progressTime;
                length = progressTime;
            }
        }
        if (percent > 0) {
            drawTexture(matrices, params.texture, realX,  realY, xLocation, yLocation, length, width);
        }
    }

    private static void drawTexture(PoseStack stack, ResourceLocation loc, int left, int top, int x, int y, int sizeX, int sizeY) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, loc);
        //AbstractGui.blit(stack, left, top, x, y, sizeX, sizeY);
        GuiComponent.blit(stack, left, top, 0, x, y, sizeX, sizeY, 256, 256);
    }

    public record Parameters(ResourceLocation texture, int x, int y, int length, int width, int posX, int posY, BarDir dir, boolean fill){

    }

    @Override
    public Renderer getIcon() {
        return icon;
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public CategoryIdentifier<? extends RecipeMapDisplay> getCategoryIdentifier() {
        return loc;
    }

    public EntryIngredient getInput(RecipeMapDisplay recipeDisplay, int index) {
        List<EntryIngredient> inputs = recipeDisplay.getInputEntries();
        return inputs.size() > index ? inputs.get(index) : EntryIngredient.empty();
    }

    public EntryIngredient getOutput(RecipeMapDisplay recipeDisplay, int index) {
        List<EntryIngredient> outputs = recipeDisplay.getOutputEntries();
        return outputs.size() > index ? outputs.get(index) : EntryIngredient.empty();
    }
}
