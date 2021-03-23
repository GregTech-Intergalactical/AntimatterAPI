package muramasa.antimatter.recipe.ingredient;

import net.minecraft.util.LazyValue;

import java.util.function.Supplier;

/**
 * Small wrapper, to avoid typing lazyvalue.
 */
public class RecipeIngredient {
    private final LazyValue<AntimatterIngredient> value;

    public RecipeIngredient(LazyValue<AntimatterIngredient> source) {
        this.value = source;
    }

    public RecipeIngredient(Supplier<AntimatterIngredient> source) {
        this.value = new LazyValue<>(source);
    }

    public RecipeIngredient(AntimatterIngredient source) {
        this.value = new LazyValue<>(() -> source);
    }

    public AntimatterIngredient get() {
        return value.getValue();
    }
}
