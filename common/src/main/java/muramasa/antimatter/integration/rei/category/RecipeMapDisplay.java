package muramasa.antimatter.integration.rei.category;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import muramasa.antimatter.Ref;
import muramasa.antimatter.integration.rei.REIUtils;
import muramasa.antimatter.recipe.IRecipe;
import muramasa.antimatter.recipe.ingredient.RecipeIngredient;
import muramasa.antimatter.recipe.map.RecipeMap;
import muramasa.antimatter.recipe.serializer.AntimatterRecipeSerializer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class RecipeMapDisplay implements Display {
    private final CategoryIdentifier<RecipeMapDisplay> id;
    private final List<EntryIngredient> input, output;
    private final IRecipe recipe;

    public RecipeMapDisplay(IRecipe recipe){
        this.id = CategoryIdentifier.of(Ref.SHARED_ID, recipe.getMapId());
        this.recipe = recipe;
        List<EntryIngredient> fluidInputs = recipe.getInputFluids().stream().map(fluidIngredient -> Arrays.stream(fluidIngredient.getStacks()).map(REIUtils::toREIFLuidStack).toList()).map(l -> EntryIngredients.of(VanillaEntryTypes.FLUID, l)).toList();
        List<EntryIngredient> itemInputs = createInputEntries(recipe.getInputItems(), recipe);
        this.input = new ArrayList<>(itemInputs);
        input.addAll(fluidInputs);
        ImmutableList.Builder<EntryIngredient> builder = ImmutableList.builder();
        if (recipe.getOutputItems(false) != null){
            builder.addAll(createOutputEntries(Arrays.asList(recipe.getOutputItems(false)), recipe));
        }
        if (recipe.getOutputFluids() != null){
            builder.addAll(Arrays.stream(recipe.getOutputFluids()).map(REIUtils::toREIFLuidStack).map(EntryIngredients::of).toList());
        }

        this.output = builder.build();
    }
    public static List<EntryIngredient> createOutputEntries(List<ItemStack> input, IRecipe recipe) {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        return input.stream().map(i -> {
            double chance = recipe.hasChances() ? Objects.requireNonNull(recipe.getChances())[atomicInteger.getAndIncrement()] : 1.0;
            return EntryStacks.of(i).setting(EntryStack.Settings.TOOLTIP_APPEND_EXTRA, getProbabilitySetting(chance));
        }).map(EntryIngredient::of).toList();
    }

    public static List<EntryIngredient> createInputEntries(List<Ingredient> input, IRecipe recipe) {
        return input.stream().map(i -> {
            List<EntryStack<ItemStack>> entry = Arrays.stream(i.getItems()).map(EntryStacks::of).toList();
            if (i instanceof RecipeIngredient ri){
                entry.forEach(e -> {
                    e.setting(EntryStack.Settings.TOOLTIP_APPEND_EXTRA, f -> {
                        List<Component> components = new ArrayList<>();
                        if (ri.ignoreConsume()) {
                           components.add(new TextComponent("Does not get consumed in the process.").withStyle(ChatFormatting.WHITE));
                        }
                        if (ri.ignoreNbt()) {
                            components.add(new TextComponent("Ignores NBT.").withStyle(ChatFormatting.WHITE));
                        }
                        if (RecipeMap.isIngredientSpecial(i)) {
                            components.add(new TextComponent("Special ingredient. Class name: ").withStyle(ChatFormatting.GRAY).append(new TextComponent(i.getClass().getSimpleName()).withStyle(ChatFormatting.GOLD)));
                        }
                        return components;
                    });
                });
            }
            return entry;
        }).map(EntryIngredient::of).toList();
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
        return id;
    }

    public IRecipe getRecipe() {
        return recipe;
    }
}
