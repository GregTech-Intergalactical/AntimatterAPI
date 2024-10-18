package muramasa.antimatter.integration.rei.category;

import com.google.common.collect.ImmutableList;
import dev.architectury.fluid.FluidStack;
import me.shedaniel.rei.api.client.gui.widgets.Tooltip;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import muramasa.antimatter.Ref;
import muramasa.antimatter.integration.rei.REIUtils;
import muramasa.antimatter.recipe.IRecipe;
import muramasa.antimatter.recipe.ingredient.RecipeIngredient;
import muramasa.antimatter.recipe.map.RecipeMap;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import muramasa.antimatter.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;
import tesseract.FluidPlatformUtils;
import tesseract.TesseractGraphWrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static muramasa.antimatter.integration.jeirei.AntimatterJEIREIPlugin.intToSuperScript;

public class RecipeMapDisplay implements Display {
    private final CategoryIdentifier<RecipeMapDisplay> id;
    private final List<EntryIngredient> input, output;
    private final IRecipe recipe;

    public RecipeMapDisplay(IRecipe recipe){
        this.id = CategoryIdentifier.of(Ref.SHARED_ID, recipe.getMapId());
        this.recipe = recipe;
        List<EntryIngredient> fluidInputs = createFluidInputEntries(recipe.getInputFluids().stream().map(fluidIngredient -> Arrays.stream(fluidIngredient.getStacks()).map(REIUtils::toREIFLuidStack).toList()).toList());
        List<EntryIngredient> itemInputs = createInputEntries(recipe.getInputItems(), recipe);
        this.input = new ArrayList<>(itemInputs);
        input.addAll(fluidInputs);
        ImmutableList.Builder<EntryIngredient> builder = ImmutableList.builder();
        ItemStack[] stacks = recipe.getOutputItems(false);
        if (stacks != null){
            builder.addAll(createOutputEntries(Arrays.asList(stacks), recipe));
        }
        if (recipe.getOutputFluids() != null){
            builder.addAll(createFluidOutputEntries(Arrays.stream(recipe.getOutputFluids()).map(REIUtils::toREIFLuidStack).toList(), recipe));
        }

        this.output = builder.build();
    }
    public static List<EntryIngredient> createOutputEntries(List<ItemStack> input, IRecipe recipe) {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        return input.stream().map(i -> {
            int chance = recipe.hasOutputChances() ? Objects.requireNonNull(recipe.getOutputChances())[atomicInteger.getAndIncrement()] : 10000;
            return EntryStacks.of(i).setting(EntryStack.Settings.TOOLTIP_APPEND_EXTRA, f -> {
                List<Component> components = new ArrayList<>();
                Component c = getProbabilityTooltip(chance, false);
                if (c != null){
                    components.add(c);
                }
                if (recipe.getId() != null){
                    components.add(Utils.literal("Recipe by: ").append(Utils.literal(AntimatterPlatformUtils.INSTANCE.getModName(recipe.getId().getNamespace())).withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC)));
                    Minecraft minecraft = Minecraft.getInstance();
                    boolean showAdvanced = minecraft.options.advancedItemTooltips || Screen.hasShiftDown();
                    if (showAdvanced){
                        components.add(Utils.literal("Recipe Id: " + recipe.getId().toString()).withStyle(ChatFormatting.DARK_GRAY));
                    }
                }
                return components;
            });
        }).map(EntryIngredient::of).toList();
    }

    public static List<EntryIngredient> createFluidOutputEntries(List<FluidStack> input, IRecipe recipe) {
        return input.stream().map(i -> {
            EntryStack<FluidStack> fluidStackEntryStack = EntryStacks.of(i);
            fluidStackEntryStack.setting(EntryStack.Settings.TOOLTIP_PROCESSOR, (entry, t) -> {
                createFluidTooltip(t, fluidStackEntryStack.getValue());
                if (recipe.getId() != null){
                    t.add(Utils.literal("Recipe by: ").append(Utils.literal(AntimatterPlatformUtils.INSTANCE.getModName(recipe.getId().getNamespace())).withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC)));
                    Minecraft minecraft = Minecraft.getInstance();
                    boolean showAdvanced = minecraft.options.advancedItemTooltips || Screen.hasShiftDown();
                    if (showAdvanced){
                        t.add(Utils.literal("Recipe Id: " + recipe.getId().toString()).withStyle(ChatFormatting.DARK_GRAY));
                    }
                }
                return t;
            });
            return fluidStackEntryStack;
        }).map(EntryIngredient::of).toList();
    }

    public static List<EntryIngredient> createFluidInputEntries(List<List<FluidStack>> input) {
        return input.stream().map(i -> {
            List<EntryStack<FluidStack>> fluidStackEntryStack = i.stream().map(EntryStacks::of).toList();
            fluidStackEntryStack.stream().forEach(e -> {
                e.setting(EntryStack.Settings.TOOLTIP_PROCESSOR, (entry, t) -> {
                    createFluidTooltip(t, e.getValue());
                    return t;
                });
            });
            return fluidStackEntryStack;
        }).map(EntryIngredient::of).toList();
    }

    private static void createFluidTooltip(Tooltip tooltip, FluidStack stack) {
        Tooltip.Entry component = tooltip.entries().get(2);
        tooltip.entries().remove(2);
        tooltip.entries().remove(1);
        long mb = (stack.getAmount() / TesseractGraphWrappers.dropletMultiplier);
        if (AntimatterPlatformUtils.INSTANCE.isFabric()){
            tooltip.add(Utils.translatable("antimatter.tooltip.fluid.amount", Utils.literal(mb + " " + intToSuperScript(stack.getAmount() % 81L) + "/₈₁ L")).withStyle(ChatFormatting.BLUE));
        } else {
            tooltip.add(Utils.translatable("antimatter.tooltip.fluid.amount", mb + " L").withStyle(ChatFormatting.BLUE));
        }
        tooltip.add(Utils.translatable("antimatter.tooltip.fluid.temp", FluidPlatformUtils.INSTANCE.getFluidTemperature(stack.getFluid())).withStyle(ChatFormatting.RED));
        String liquid = !FluidPlatformUtils.INSTANCE.isFluidGaseous(stack.getFluid()) ? "liquid" : "gas";
        tooltip.add(Utils.translatable("antimatter.tooltip.fluid." + liquid).withStyle(ChatFormatting.GREEN));
        tooltip.add(component.getAsText());
    }

    public static List<EntryIngredient> createInputEntries(List<Ingredient> input, IRecipe recipe) {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        return input.stream().map(i -> {
            int chance = recipe.hasOutputChances() ? Objects.requireNonNull(recipe.getOutputChances())[atomicInteger.getAndIncrement()] : 10000;
            List<EntryStack<ItemStack>> entry = Arrays.stream(i.getItems()).map(EntryStacks::of).toList();
            if (i instanceof RecipeIngredient ri){
                entry.forEach(e -> {
                    //e.setting(EntryStack.Settings.TOOLTIP_PROCESSOR)
                    e.setting(EntryStack.Settings.TOOLTIP_APPEND_EXTRA, f -> {
                        List<Component> components = new ArrayList<>();
                        Component c = getProbabilityTooltip(chance, true);
                        if (c != null){
                            components.add(c);
                        }
                        if (ri.ignoreConsume()) {
                           components.add(Utils.literal("Does not get consumed in the process.").withStyle(ChatFormatting.WHITE));
                        }
                        if (ri.ignoreNbt()) {
                            components.add(Utils.literal("Ignores NBT.").withStyle(ChatFormatting.WHITE));
                        }
                        if (RecipeMap.isIngredientSpecial(i)) {
                            components.add(Utils.literal("Special ingredient. Class name: ").withStyle(ChatFormatting.GRAY).append(Utils.literal(i.getClass().getSimpleName()).withStyle(ChatFormatting.GOLD)));
                        }
                        return components;
                    });
                });
            }
            return entry;
        }).map(EntryIngredient::of).toList();
    }

    public static Component getProbabilityTooltip(int probability, boolean input) {
        if (probability == 10000) {
            return null;
        } else {
            MutableComponent text = Utils.literal((input ? "Consumption" : "Output") +  " Chance: " + ((float)probability / 100) + "%");
            text.withStyle(ChatFormatting.WHITE);
            return text;
        }
    }

    private static Function<EntryStack<?>, List<Component>> getFluidSetting(FluidStack fluidStack) {
        @Nullable
        Component tooltip = Utils.literal((fluidStack.getAmount() / 81L) + " " + intToSuperScript(fluidStack.getAmount() % 81L) + "/₈₁ mb");
        return es -> List.of(tooltip);
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        return input;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return output;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return id;
    }

    public IRecipe getRecipe() {
        return recipe;
    }
}
