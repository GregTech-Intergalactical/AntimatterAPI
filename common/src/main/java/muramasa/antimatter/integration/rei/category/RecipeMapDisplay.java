package muramasa.antimatter.integration.rei.category;

import com.google.common.collect.ImmutableList;
import dev.architectury.fluid.FluidStack;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import muramasa.antimatter.integration.rei.AntimatterREIPlugin;
import muramasa.antimatter.recipe.IRecipe;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class RecipeMapDisplay implements Display {
    private final ResourceLocation id;
    private final List<EntryIngredient> input, output;
    private final IRecipe recipe;

    public RecipeMapDisplay(ResourceLocation id, IRecipe recipe){
        this.id = id;
        this.recipe = recipe;
        List<FluidStack> fluidInputs = recipe.getInputFluids().stream().flatMap(fluidIngredient -> Arrays.stream(fluidIngredient.getStacks())).map(AntimatterREIPlugin::toREIFLuidStack).toList();
        List<ItemStack> itemInputs = recipe.getInputItems().stream().flatMap(ingredient -> Arrays.stream(ingredient.getItems())).toList();
        this.input = List.of(EntryIngredients.of(VanillaEntryTypes.FLUID, fluidInputs), EntryIngredients.of(VanillaEntryTypes.ITEM, itemInputs));
        ImmutableList.Builder<EntryIngredient> builder = ImmutableList.builder();
        if (recipe.getOutputFluids() != null){
            builder.add(EntryIngredients.of(VanillaEntryTypes.FLUID, Arrays.stream(recipe.getOutputFluids()).map(AntimatterREIPlugin::toREIFLuidStack).toList()));
        }
        if (recipe.getOutputItems(false) != null){
            builder.add(createOutputEntries(Arrays.asList(recipe.getOutputItems(false)), recipe));
        }
        this.output = builder.build();
    }
    public static EntryIngredient createOutputEntries(List<ItemStack> input, IRecipe recipe) {
        return EntryIngredient.of(input.stream().map(i -> EntryStacks.of(i).setting(EntryStack.Settings.TOOLTIP_APPEND_EXTRA, getProbabilitySetting(recipe.getChancesWithStacks().get(i)))).toList());
    }

    public static Component getProbabilityTooltip(double probability) {
        if (probability == 1.0) {
            return null;
        } else {
            MutableComponent text = new TextComponent("Chance: " + (probability * 100) + "%");
            text.withStyle(ChatFormatting.WHITE);
            return text;
        }
    }

    public static Function<EntryStack<?>, List<Component>> getProbabilitySetting(double probability) {
        @Nullable
        Component tooltip = getProbabilityTooltip(probability);
        return es -> tooltip == null ? List.of() : List.of(tooltip);
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
        return CategoryIdentifier.of(id);
    }
}
