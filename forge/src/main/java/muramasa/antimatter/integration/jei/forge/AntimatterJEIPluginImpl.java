package muramasa.antimatter.integration.jei.forge;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;
import muramasa.antimatter.integration.jei.AntimatterJEIPlugin;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.Optional;

public class AntimatterJEIPluginImpl {
    public static void uses(FluidStack val, boolean USE) {
        FluidStack v = val.copy();
        AntimatterJEIPlugin.getRuntime().getRecipesGui().show(new IFocus<FluidStack>() {
            @Override
            public ITypedIngredient<FluidStack> getTypedValue() {
                return new ITypedIngredient<>() {
                    @Override
                    public IIngredientType<FluidStack> getType() {
                        return VanillaTypes.FLUID;
                    }

                    @Override
                    public FluidStack getIngredient() {
                        return v;
                    }

                    @Override
                    public <V> Optional<V> getIngredient(IIngredientType<V> ingredientType) {
                        if (ingredientType == VanillaTypes.FLUID) return ((Optional<V>) Optional.of(v));
                        return Optional.empty();
                    }
                };
            }

            @Override
            public RecipeIngredientRole getRole() {
                return USE ? RecipeIngredientRole.INPUT : RecipeIngredientRole.OUTPUT;
            }

            @Override
            public <T> Optional<IFocus<T>> checkedCast(IIngredientType<T> ingredientType) {
                return Optional.empty();
            }

            @Override
            public Mode getMode() {
                return USE ? Mode.INPUT : Mode.OUTPUT;
            }

        });
    }

    public static void addFluidIngredients(IRecipeSlotBuilder builder, List<FluidStack> stacks){
        builder.addIngredients(VanillaTypes.FLUID, stacks);
    }

    public static FluidStack getIngredient(ITypedIngredient<?> ingredient){
        return ingredient.getIngredient(VanillaTypes.FLUID).get();
    }
}
