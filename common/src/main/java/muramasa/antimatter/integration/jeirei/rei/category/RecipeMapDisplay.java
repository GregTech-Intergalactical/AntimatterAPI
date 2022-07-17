package muramasa.antimatter.integration.jeirei.rei.category;

import com.google.common.collect.ImmutableList;
import dev.architectury.fluid.FluidStack;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import muramasa.antimatter.integration.jeirei.rei.AntimatterREIPlugin;
import muramasa.antimatter.recipe.Recipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RecipeMapDisplay implements Display {
    private final ResourceLocation id;
    private final List<EntryIngredient> input, output;
    private final Recipe recipe;

    public RecipeMapDisplay(ResourceLocation id, Recipe recipe){
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
            //Stream<ItemStack> stream = Arrays.stream()
            builder.add(EntryIngredients.of(VanillaEntryTypes.ITEM, Arrays.asList(recipe.getOutputItems(false))));
        }
        this.output = builder.build();
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
